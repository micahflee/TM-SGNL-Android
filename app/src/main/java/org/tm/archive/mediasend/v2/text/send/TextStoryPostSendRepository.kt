package org.tm.archive.mediasend.v2.text.send

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.ThreadUtil
import org.signal.core.util.logging.Log
import org.tm.archive.contacts.paged.ContactSearchKey
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.ThreadDatabase
import org.tm.archive.database.model.StoryType
import org.tm.archive.database.model.databaseprotos.StoryTextPost
import org.tm.archive.fonts.TextFont
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.keyvalue.StorySend
import org.tm.archive.linkpreview.LinkPreview
import org.tm.archive.mediasend.v2.UntrustedRecords
import org.tm.archive.mediasend.v2.text.TextStoryPostCreationState
import org.tm.archive.mms.OutgoingMediaMessage
import org.tm.archive.mms.OutgoingSecureMediaMessage
import org.tm.archive.providers.BlobProvider
import org.tm.archive.recipients.Recipient
import org.tm.archive.stories.Stories
import org.tm.archive.util.Base64
import java.io.ByteArrayOutputStream

private val TAG = Log.tag(TextStoryPostSendRepository::class.java)

class TextStoryPostSendRepository {

  fun compressToBlob(bitmap: Bitmap): Single<Uri> {
    return Single.fromCallable {
      val outputStream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
      bitmap.recycle()
      BlobProvider.getInstance().forData(outputStream.toByteArray()).createForSingleUseInMemory()
    }.subscribeOn(Schedulers.computation())
  }

  fun send(contactSearchKey: Set<ContactSearchKey>, textStoryPostCreationState: TextStoryPostCreationState, linkPreview: LinkPreview?, identityChangesSince: Long): Single<TextStoryPostSendResult> {
    return UntrustedRecords
      .checkForBadIdentityRecords(contactSearchKey.filterIsInstance(ContactSearchKey.RecipientSearchKey::class.java).toSet(), identityChangesSince)
      .toSingleDefault<TextStoryPostSendResult>(TextStoryPostSendResult.Success)
      .onErrorReturn {
        if (it is UntrustedRecords.UntrustedRecordsException) {
          TextStoryPostSendResult.UntrustedRecordsError(it.untrustedRecords)
        } else {
          Log.w(TAG, "Unexpected error occurred", it)
          TextStoryPostSendResult.Failure
        }
      }
      .flatMap { result ->
        if (result is TextStoryPostSendResult.Success) {
          performSend(contactSearchKey, textStoryPostCreationState, linkPreview)
        } else {
          Single.just(result)
        }
      }
  }

  private fun performSend(contactSearchKey: Set<ContactSearchKey>, textStoryPostCreationState: TextStoryPostCreationState, linkPreview: LinkPreview?): Single<TextStoryPostSendResult> {
    return Single.fromCallable {
      val messages: MutableList<OutgoingSecureMediaMessage> = mutableListOf()
      val distributionListSentTimestamp = System.currentTimeMillis()

      for (contact in contactSearchKey) {
        val recipient = Recipient.resolved(contact.requireShareContact().recipientId.get())
        val isStory = contact is ContactSearchKey.RecipientSearchKey.Story || recipient.isDistributionList

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
          serializeTextStoryState(textStoryPostCreationState),
          emptyList(),
          if (recipient.isDistributionList) distributionListSentTimestamp else System.currentTimeMillis(),
          -1,
          0,
          false,
          ThreadDatabase.DistributionTypes.DEFAULT,
          storyType.toTextStoryType(),
          null,
          false,
          null,
          emptyList(),
          listOfNotNull(linkPreview),
          emptyList(),
          mutableSetOf(),
          mutableSetOf(),
          null
        )

        messages.add(OutgoingSecureMediaMessage(message))
        ThreadUtil.sleep(5)
      }

      Stories.sendTextStories(messages)
    }.flatMap { messages ->
      messages.toSingleDefault<TextStoryPostSendResult>(TextStoryPostSendResult.Success)
    }
  }

  private fun serializeTextStoryState(textStoryPostCreationState: TextStoryPostCreationState): String {
    val builder = StoryTextPost.newBuilder()

    builder.body = textStoryPostCreationState.body.toString()
    builder.background = textStoryPostCreationState.backgroundColor.serialize()
    builder.style = when (textStoryPostCreationState.textFont) {
      TextFont.REGULAR -> StoryTextPost.Style.REGULAR
      TextFont.BOLD -> StoryTextPost.Style.BOLD
      TextFont.SERIF -> StoryTextPost.Style.SERIF
      TextFont.SCRIPT -> StoryTextPost.Style.SCRIPT
      TextFont.CONDENSED -> StoryTextPost.Style.CONDENSED
    }
    builder.textBackgroundColor = textStoryPostCreationState.textBackgroundColor
    builder.textForegroundColor = textStoryPostCreationState.textForegroundColor

    return Base64.encodeBytes(builder.build().toByteArray())
  }
}
