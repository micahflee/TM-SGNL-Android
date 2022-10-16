package org.tm.archive.mediasend.v2

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.BreakIteratorCompat
import org.signal.core.util.ThreadUtil
import org.signal.core.util.logging.Log
import org.signal.imageeditor.core.model.EditorModel
import org.tm.archive.contacts.paged.ContactSearchKey
import org.tm.archive.conversation.MessageSendType
import org.tm.archive.database.AttachmentDatabase.TransformProperties
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.ThreadDatabase
import org.tm.archive.database.model.Mention
import org.tm.archive.database.model.StoryType
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.keyvalue.StorySend
import org.tm.archive.mediasend.CompositeMediaTransform
import org.tm.archive.mediasend.ImageEditorModelRenderMediaTransform
import org.tm.archive.mediasend.Media
import org.tm.archive.mediasend.MediaRepository
import org.tm.archive.mediasend.MediaSendActivityResult
import org.tm.archive.mediasend.MediaTransform
import org.tm.archive.mediasend.MediaUploadRepository
import org.tm.archive.mediasend.SentMediaQualityTransform
import org.tm.archive.mediasend.VideoEditorFragment
import org.tm.archive.mediasend.VideoTrimTransform
import org.tm.archive.mms.MediaConstraints
import org.tm.archive.mms.OutgoingMediaMessage
import org.tm.archive.mms.OutgoingSecureMediaMessage
import org.tm.archive.mms.SentMediaQuality
import org.tm.archive.mms.Slide
import org.tm.archive.providers.BlobProvider
import org.tm.archive.recipients.Recipient
import org.tm.archive.scribbles.ImageEditorFragment
import org.tm.archive.sms.MessageSender
import org.tm.archive.sms.MessageSender.PreUploadResult
import org.tm.archive.stories.Stories
import org.tm.archive.util.MessageUtil
import java.util.Collections
import java.util.Optional
import java.util.concurrent.TimeUnit

private val TAG = Log.tag(MediaSelectionRepository::class.java)

class MediaSelectionRepository(context: Context) {

  private val context: Context = context.applicationContext

  private val mediaRepository = MediaRepository()

  val uploadRepository = MediaUploadRepository(this.context)
  val isMetered: Observable<Boolean> = MeteredConnectivity.isMetered(this.context)

  fun populateAndFilterMedia(media: List<Media>, mediaConstraints: MediaConstraints, maxSelection: Int, isStory: Boolean): Single<MediaValidator.FilterResult> {
    return Single.fromCallable {
      val populatedMedia = mediaRepository.getPopulatedMedia(context, media)

      MediaValidator.filterMedia(context, populatedMedia, mediaConstraints, maxSelection, isStory)
    }.subscribeOn(Schedulers.io())
  }

  /**
   * Tries to send the selected media, performing proper transformations for edited images and videos.
   */
  fun send(
    selectedMedia: List<Media>,
    stateMap: Map<Uri, Any>,
    quality: SentMediaQuality,
    message: CharSequence?,
    isSms: Boolean,
    isViewOnce: Boolean,
    singleContact: ContactSearchKey.RecipientSearchKey?,
    contacts: List<ContactSearchKey.RecipientSearchKey>,
    mentions: List<Mention>,
    sendType: MessageSendType
  ): Maybe<MediaSendActivityResult> {
    if (isSms && contacts.isNotEmpty()) {
      throw IllegalStateException("Provided recipients to send to, but this is SMS!")
    }

    if (selectedMedia.isEmpty()) {
      throw IllegalStateException("No selected media!")
    }

    val isSendingToStories = singleContact?.isStory == true || contacts.any { it.isStory }
    val sentMediaQuality = if (isSendingToStories) SentMediaQuality.STANDARD else quality

    return Maybe.create<MediaSendActivityResult> { emitter ->
      val trimmedBody: String = if (isViewOnce) "" else getTruncatedBody(message?.toString()?.trim()) ?: ""
      val trimmedMentions: List<Mention> = if (isViewOnce) emptyList() else mentions
      val modelsToTransform: Map<Media, MediaTransform> = buildModelsToTransform(selectedMedia, stateMap, sentMediaQuality)
      val oldToNewMediaMap: Map<Media, Media> = MediaRepository.transformMediaSync(context, selectedMedia, modelsToTransform)
      val updatedMedia = oldToNewMediaMap.values.toList()

      for (media in updatedMedia) {
        Log.w(TAG, media.uri.toString() + " : " + media.transformProperties.map { t: TransformProperties -> "" + t.isVideoTrim }.orElse("null"))
      }

      val singleRecipient: Recipient? = singleContact?.let { Recipient.resolved(it.recipientId) }
      val storyType: StoryType = if (singleRecipient?.isDistributionList == true) {
        SignalDatabase.distributionLists.getStoryType(singleRecipient.requireDistributionListId())
      } else {
        StoryType.NONE
      }

      if (isSms || MessageSender.isLocalSelfSend(context, singleRecipient, isSms)) {
        Log.i(TAG, "SMS or local self-send. Skipping pre-upload.")
        emitter.onSuccess(MediaSendActivityResult.forTraditionalSend(singleRecipient!!.id, updatedMedia, trimmedBody, sendType, isViewOnce, trimmedMentions, StoryType.NONE))
      } else {
        val splitMessage = MessageUtil.getSplitMessage(context, trimmedBody, sendType.calculateCharacters(trimmedBody).maxPrimaryMessageSize)
        val splitBody = splitMessage.body

        if (splitMessage.textSlide.isPresent) {
          val slide: Slide = splitMessage.textSlide.get()
          uploadRepository.startUpload(
            MediaBuilder.buildMedia(
              uri = requireNotNull(slide.uri),
              mimeType = slide.contentType,
              date = System.currentTimeMillis(),
              size = slide.fileSize,
              borderless = slide.isBorderless,
              videoGif = slide.isVideoGif
            ),
            singleRecipient
          )
        }

        val clippedVideosForStories: List<Media> = if (isSendingToStories) {
          updatedMedia.filter {
            Stories.MediaTransform.getSendRequirements(it) == Stories.MediaTransform.SendRequirements.REQUIRES_CLIP
          }.map { media ->
            Stories.MediaTransform.clipMediaToStoryDuration(media)
          }.flatten()
        } else emptyList()

        uploadRepository.applyMediaUpdates(oldToNewMediaMap, singleRecipient)
        uploadRepository.updateCaptions(updatedMedia)
        uploadRepository.updateDisplayOrder(updatedMedia)
        uploadRepository.getPreUploadResults { uploadResults ->
          if (contacts.isNotEmpty()) {
            sendMessages(contacts, splitBody, uploadResults, trimmedMentions, isViewOnce, clippedVideosForStories)
            uploadRepository.deleteAbandonedAttachments()
            emitter.onComplete()
          } else if (uploadResults.isNotEmpty()) {
            emitter.onSuccess(MediaSendActivityResult.forPreUpload(singleRecipient!!.id, uploadResults, splitBody, sendType, isViewOnce, trimmedMentions, storyType))
          } else {
            Log.w(TAG, "Got empty upload results! isSms: $isSms, updatedMedia.size(): ${updatedMedia.size}, isViewOnce: $isViewOnce, target: $singleContact")
            emitter.onSuccess(MediaSendActivityResult.forTraditionalSend(singleRecipient!!.id, updatedMedia, trimmedBody, sendType, isViewOnce, trimmedMentions, storyType))
          }
        }
      }
    }.subscribeOn(Schedulers.io()).cast(MediaSendActivityResult::class.java)
  }

  private fun getTruncatedBody(body: String?): String? {
    return if (!Stories.isFeatureEnabled() || body.isNullOrEmpty()) {
      body
    } else {
      val iterator = BreakIteratorCompat.getInstance()
      iterator.setText(body)
      iterator.take(Stories.MAX_CAPTION_SIZE).toString()
    }
  }

  fun deleteBlobs(media: List<Media>) {
    media
      .map(Media::getUri)
      .filter(BlobProvider::isAuthority)
      .forEach { BlobProvider.getInstance().delete(context, it) }
  }

  fun cleanUp(selectedMedia: List<Media>) {
    deleteBlobs(selectedMedia)
    uploadRepository.cancelAllUploads()
    uploadRepository.deleteAbandonedAttachments()
  }

  fun isLocalSelfSend(recipient: Recipient?, isSms: Boolean): Boolean {
    return MessageSender.isLocalSelfSend(context, recipient, isSms)
  }

  @WorkerThread
  private fun buildModelsToTransform(
    selectedMedia: List<Media>,
    stateMap: Map<Uri, Any>,
    quality: SentMediaQuality
  ): Map<Media, MediaTransform> {
    val modelsToRender: MutableMap<Media, MediaTransform> = mutableMapOf()

    selectedMedia.forEach {
      val state = stateMap[it.uri]
      if (state is ImageEditorFragment.Data) {
        val model: EditorModel? = state.readModel()
        if (model != null && model.isChanged) {
          modelsToRender[it] = ImageEditorModelRenderMediaTransform(model)
        }
      }

      if (state is VideoEditorFragment.Data && state.isDurationEdited) {
        modelsToRender[it] = VideoTrimTransform(state)
      }

      if (quality == SentMediaQuality.HIGH) {
        val existingTransform: MediaTransform? = modelsToRender[it]

        modelsToRender[it] = if (existingTransform == null) {
          SentMediaQualityTransform(quality)
        } else {
          CompositeMediaTransform(existingTransform, SentMediaQualityTransform(quality))
        }
      }
    }

    return modelsToRender
  }

  @WorkerThread
  private fun sendMessages(
    contacts: List<ContactSearchKey.RecipientSearchKey>,
    body: String,
    preUploadResults: Collection<PreUploadResult>,
    mentions: List<Mention>,
    isViewOnce: Boolean,
    storyClips: List<Media>
  ) {
    val nonStoryMessages: MutableList<OutgoingSecureMediaMessage> = ArrayList(contacts.size)
    val storyPreUploadMessages: MutableMap<PreUploadResult, MutableList<OutgoingSecureMediaMessage>> = mutableMapOf()
    val storyClipMessages: MutableList<OutgoingSecureMediaMessage> = ArrayList()
    val distributionListPreUploadSentTimestamps: MutableMap<PreUploadResult, Long> = mutableMapOf()
    val distributionListStoryClipsSentTimestamps: MutableMap<MediaKey, Long> = mutableMapOf()

    for (contact in contacts) {
      val recipient = Recipient.resolved(contact.recipientId)
      val isStory = contact.isStory || recipient.isDistributionList

      if (isStory && !recipient.isMyStory) {
        SignalStore.storyValues().setLatestStorySend(StorySend.newSend(recipient))
      }

      val storyType: StoryType = when {
        recipient.isDistributionList -> SignalDatabase.distributionLists.getStoryType(recipient.requireDistributionListId())
        isStory -> StoryType.STORY_WITH_REPLIES
        else -> StoryType.NONE
      }

      val message = OutgoingMediaMessage(
        recipient,
        body,
        emptyList(),
        if (recipient.isDistributionList) distributionListPreUploadSentTimestamps.getOrPut(preUploadResults.first()) { System.currentTimeMillis() } else System.currentTimeMillis(),
        -1,
        TimeUnit.SECONDS.toMillis(recipient.expiresInSeconds.toLong()),
        isViewOnce,
        ThreadDatabase.DistributionTypes.DEFAULT,
        storyType,
        null,
        false,
        null,
        emptyList(),
        emptyList(),
        mentions,
        mutableSetOf(),
        mutableSetOf(),
        null
      )

      if (isStory) {
        preUploadResults.filterNot { result -> storyClips.any { it.uri == result.media.uri } }.forEach {
          val list = storyPreUploadMessages[it] ?: mutableListOf()
          list.add(
            OutgoingSecureMediaMessage(message).withSentTimestamp(
              if (recipient.isDistributionList) {
                distributionListPreUploadSentTimestamps.getOrPut(it) { System.currentTimeMillis() }
              } else {
                System.currentTimeMillis()
              }
            )
          )
          storyPreUploadMessages[it] = list

          // XXX We must do this to avoid sending out messages to the same recipient with the same
          //     sentTimestamp. If we do this, they'll be considered dupes by the receiver.
          ThreadUtil.sleep(5)
        }

        storyClips.forEach {
          storyClipMessages.add(
            OutgoingSecureMediaMessage(
              OutgoingMediaMessage(
                recipient,
                body,
                listOf(MediaUploadRepository.asAttachment(context, it)),
                if (recipient.isDistributionList) distributionListStoryClipsSentTimestamps.getOrPut(it.asKey()) { System.currentTimeMillis() } else System.currentTimeMillis(),
                -1,
                TimeUnit.SECONDS.toMillis(recipient.expiresInSeconds.toLong()),
                isViewOnce,
                ThreadDatabase.DistributionTypes.DEFAULT,
                storyType,
                null,
                false,
                null,
                emptyList(),
                emptyList(),
                mentions,
                mutableSetOf(),
                mutableSetOf(),
                null
              )
            )
          )

          // XXX We must do this to avoid sending out messages to the same recipient with the same
          //     sentTimestamp. If we do this, they'll be considered dupes by the receiver.
          ThreadUtil.sleep(5)
        }
      } else {
        nonStoryMessages.add(OutgoingSecureMediaMessage(message))

        // XXX We must do this to avoid sending out messages to the same recipient with the same
        //     sentTimestamp. If we do this, they'll be considered dupes by the receiver.
        ThreadUtil.sleep(5)
      }
    }

    if (nonStoryMessages.isNotEmpty()) {
      Log.d(TAG, "Sending ${nonStoryMessages.size} preupload messages to chats")
      MessageSender.sendMediaBroadcast(
        context,
        nonStoryMessages,
        preUploadResults,
        true
      )
    }

    if (storyPreUploadMessages.isNotEmpty()) {
      Log.d(TAG, "Sending ${storyPreUploadMessages.size} preload messages to stories")
      storyPreUploadMessages.forEach { (preUploadResult, messages) ->
        MessageSender.sendMediaBroadcast(context, messages, Collections.singleton(preUploadResult), nonStoryMessages.isEmpty())
      }
    }

    if (storyClipMessages.isNotEmpty()) {
      Log.d(TAG, "Sending ${storyClipMessages.size} video clip messages to stories")
      MessageSender.sendStories(context, storyClipMessages, null, null)
    }
  }

  private fun Media.asKey(): MediaKey {
    return MediaKey(this, this.transformProperties)
  }

  data class MediaKey(val media: Media, val mediaTransform: Optional<TransformProperties>)
}
