package org.archiver

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.tm.androidcopysdk.DataGrabber
import com.tm.androidcopysdk.network.NetworkManager
import com.tm.androidcopysdk.network.keepAlive.KeepALiveResponse
import com.tm.androidcopysdk.network.keepAlive.KeepAliveRequest
import com.tm.androidcopysdk.utils.Contact
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticationAppType
import com.tm.logger.Log
import com.tm.utils.Definitions
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_CHAT_GROUP
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_FROM_TEXT
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_TO_TEXT
import org.archiver.ArchiveConstants.Companion.SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX
import org.archiver.ArchiveConstants.Companion.isTestMode
import org.archiver.ArchiveConstants.Companion.signalTestMobileNumber
import org.archiver.ArchiveSender.Companion.archiveMessageOutboxMMS
import org.archiver.ArchiveSender.Companion.archiveMessageOutboxV1
import org.archiver.ArchiveSender.Companion.updateArchiveSDKToSendMMSMessage
import org.tm.archive.BuildConfig
import org.tm.archive.attachments.AttachmentId
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.Mention
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.groups.GroupId
import org.tm.archive.linkpreview.LinkPreview
import org.tm.archive.mms.IncomingMediaMessage
import org.tm.archive.mms.OutgoingMessage
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.sms.IncomingTextMessage
import java.io.File
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors


class ArchiveUtil {

  companion object {

    const val TAG = "ArchiveUtil"
    @JvmStatic
    var listAttachmentId : List<AttachmentId> = emptyList()

    @JvmStatic
    fun createToRecipientList(
      context: Context,
      isInboxArchiveMessage: Boolean,
      aRecipient: Recipient,
      isGroup: Boolean,
      from: String,
      recipientList: MutableList<Recipient>? = null
    ): Array<String> {
      var recipientListFromRecipient: List<String> = if (isGroup) {
        recipientList?.filter { it.e164.isPresent }?.map { it.e164.get() }
          ?: getRecipientsListFromParticipantIds(aRecipient).filter { it.e164.isPresent }.map { it.e164.get() }

      } else {
        if (isInboxArchiveMessage) {
          listOf(getPhoneNumberInTestMode(context))
        } else {
          if (aRecipient.e164.isPresent) {
            listOf(aRecipient.e164.get().toString())
          } else {
            listOf("")
          }
        }
      }

      recipientListFromRecipient = if (!isInboxArchiveMessage) {
        if (recipientListFromRecipient.size > 1) {
          recipientListFromRecipient.filter { it != getPhoneNumberInTestMode(context) }
        } else {
          //Sending message in group that contains only me
          recipientListFromRecipient
        }
      } else {
        recipientListFromRecipient.filter { it != from }
      }
      return recipientListFromRecipient.toTypedArray();
    }


@JvmStatic
    fun createToRecipientListV2(
      context: Context,
      isInboxArchiveMessage: Boolean,
      aRecipient: Recipient,
      threadRecipient: Recipient,
      isGroup: Boolean,
      from: String,
      recipientList: MutableList<Recipient>? = null
    ): Array<String> {
      var recipientListFromRecipient: List<String> = if (isGroup) {
         getRecipientsListFromThreadRecipient(threadRecipient).filter { it.e164.isPresent }.map { it.e164.get() }
      } else {
        if (isInboxArchiveMessage) {
          listOf(getPhoneNumberInTestMode(context))
        } else {
          if (aRecipient.e164.isPresent) {
            listOf(aRecipient.e164.get().toString())
          } else {
            listOf("")
          }
        }
      }

      recipientListFromRecipient = if (!isInboxArchiveMessage) {
        if (recipientListFromRecipient.size > 1) {
          recipientListFromRecipient.filter { it != getPhoneNumberInTestMode(context) }
        } else {
          //Sending message in group that contains only me
          recipientListFromRecipient
        }
      } else {
        recipientListFromRecipient.filter { it != from }
      }
      return recipientListFromRecipient.toTypedArray();
    }


    @JvmStatic
    fun createSubjectForArchiving(
      context: Context,
      isInboxArchiveMessage: Boolean,
      isGroup: Boolean,
      recipient: Recipient,
      inboxRecipient: String = "",
      forceSms: Boolean,
      groupTitle: String? = "",
      deletePrefix : String =""
    ): String {

      val archiveType: String = getArchiveType(isInboxArchiveMessage, isGroup, forceSms)
      val to = getToPartForSubject(context, isInboxArchiveMessage, recipient, isGroup, groupTitle)
      val from = getFromPartForSubject(context, isInboxArchiveMessage, recipient, inboxRecipient)

      val clearSubject = "$archiveType $ARCHIVE_SUBJECT_FROM_TEXT ${
        from.toString().replace("+", "")
      } $ARCHIVE_SUBJECT_TO_TEXT ${to.replace("+", "")}"
      return deletePrefix + clearSubject.replace("\u2069", "").replace("\u2068", "")
    }

    @JvmStatic
    private fun getToPartForSubject(
      context: Context,
      isInboxArchiveMessage: Boolean,
      recipient: Recipient,
      isGroup: Boolean,
      groupTitle: String?
    ): String {
      return when {
        isGroup -> {
          "$ARCHIVE_SUBJECT_CHAT_GROUP $groupTitle"
        }
        isInboxArchiveMessage -> {
          getPhoneNumberInTestMode(context)
        }
        else -> {
          if (recipient.e164.isPresent) {
            recipient.e164.get()
          } else {
            ""
          }
        }
      }
    }

    @JvmStatic
    fun getFromPartForSubject(
      context: Context,
      isInboxArchiveMessage: Boolean,
      recipient: Recipient,
      inboxRecipient: String = ""
    ): String {
      return when {
        isInboxArchiveMessage -> {
          when {
            recipient.e164.isPresent -> {
              recipient.e164.get()
            }
            inboxRecipient.isNotEmpty() -> {
              inboxRecipient
            }
            else -> {
              recipient.requireE164()
            }
          }
        }
        else -> {
          getPhoneNumberInTestMode(context)
        }
      }
    }

    @JvmStatic
    fun getArchiveType(
      isInboxArchiveMessage: Boolean,
      isGroupMessage: Boolean,
      forceSms: Boolean
    ): String {

      return if (isInboxArchiveMessage || isGroupMessage) {
        ArchiveConstants.ARCHIVE_TYPE_APP_MESSAGE
      } else {
        if (forceSms) {
          ArchiveConstants.ARCHIVE_TYPE_SMS
        } else {
          ArchiveConstants.ARCHIVE_TYPE_APP_MESSAGE
        }
      }
    }

    @JvmStatic
    fun getPhoneNumberInTestMode(context: Context): String {
      return if (isTestMode) {
        signalTestMobileNumber
      } else {
        PrefManager.getStringPref(
          context,
          ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER,
          ""
        );
      }
    }

    @JvmStatic
    fun getChatMode(isGroup: Boolean): DataGrabber.CHAT_MODE {
      return when {
        isGroup -> {
          DataGrabber.CHAT_MODE.group
        }
        else -> {
          DataGrabber.CHAT_MODE.chat
        }
      }
    }

    @JvmStatic
    fun getChatName(
      context: Context,
      recipient: Recipient,
      isGroup: Boolean,
      groupTitle: String = ""
    ): String {
      return if (isGroup) {
        if (groupTitle.isNotEmpty()) {
          groupTitle
        } else {
          recipient.getGroupName(context) ?: ""
        }
      } else {
        ""
      }
    }

    @JvmStatic
    fun getChatNameV2(
      context: Context,
      threadRecipient: Recipient,
      isGroup: Boolean,
      groupTitle: String = ""
    ): String {
      return if (isGroup) {
        if (groupTitle.isNotEmpty()) {
          groupTitle
        } else {
          threadRecipient.getGroupName(context) ?: ""
        }
      } else {
        ""
      }
    }

    @JvmStatic
    fun getGroupInboxRecipientNumber(
      archiveRecipient: Recipient,
      message: IncomingTextMessage
    ): String {

      val recipientList = getRecipientsListFromParticipantIds(archiveRecipient).filter {
        message.authorId.toLong() == it.id.toLong()
      }
      return recipientList[0].e164.get()
    }

    @JvmStatic
    fun groupId(recipient: Recipient, groupId: GroupId? = null): String? {
      return when {
        recipient.isGroup -> {
          recipient.groupId.get().toString()
        }
        groupId != null -> {
          groupId.toString()
        }

        else -> {
          ""
        }
      }
    }


    @JvmStatic
    fun fromContactName(
      context: Context,
      recipient: Recipient,
      isInboxArchiveMessage: Boolean
    ): Contact {
      return if (isInboxArchiveMessage) {
        Contact(recipient.getDisplayName(context)).cleanContactNameFromUnUsedCharacters()
      } else {
        Contact(Recipient.self().profileName.toString()).cleanContactNameFromUnUsedCharacters()
      }

    }

    @JvmStatic
    fun getRecipientsListFromParticipantIds(recipient: Recipient) : MutableList<Recipient> {
      val selfId = ApplicationDependencies.getRecipientCache().selfId
      return recipient.participantIds.stream()
        .filter(Predicate { id: RecipientId -> id != selfId })
        .limit(ArchiveConstants.MAX_MEMBER_NAMES.toLong())
        .map(Function { id: RecipientId? -> Recipient.resolved(id!!) })
        .collect(Collectors.toList())
    }

    @JvmStatic
    fun getRecipientsListFromThreadRecipient(threadRecipient: Recipient) : List<Recipient> {
      return threadRecipient.participantIds.stream()
        .limit(ArchiveConstants.MAX_MEMBER_NAMES.toLong())
        .map(Function { id: RecipientId? -> Recipient.resolved(id!!) })
        .collect(Collectors.toList())
    }

    @JvmStatic
    fun createMessageNameList(
      context: Context,
      recipient: Recipient,
      isInboxArchiveMessage: Boolean,
      recipientList: List<Recipient>? = null,
      isGroup: Boolean,
      from: Contact = Contact("")
    ): Array<Contact> {


      val tempRecipientList = getRecipientsListFromParticipantIds(recipient)

      val rl = if (!isInboxArchiveMessage) {
        if (recipientList!!.size > 1) {
          recipientList!!.filter {
            it.e164.isPresent && it.e164.get() != getPhoneNumberInTestMode(context)
          } ?:tempRecipientList.filter {
            it.e164.isPresent && it.e164.get() != getPhoneNumberInTestMode(context)
          }
        } else {
          //Sending message in group that contains only me
          recipientList
        }
      } else {
        recipientList?.filter {
          it.e164.isPresent && it.e164.get() != from.toString()
        }
          ?: tempRecipientList.filter {
            it.e164.isPresent && it.e164.get() != from.toString()
          }
      }

      val recipientListFromRecipient: List<Contact> = if (isGroup) {

        rl.map {
          Contact(it.getDisplayName(context))
        }

      } else {
        if (isInboxArchiveMessage) {
          listOf(Contact(Recipient.self().profileName.toString()))
        } else {
          listOf(Contact(recipient.getDisplayName(context)))
        }
      }

      if (recipientListFromRecipient.toTypedArray().isEmpty()) {
        return arrayOf(Contact())
      }

      //SIG-437 - Clean list from [FSI]*[PDI]
      recipientListFromRecipient.forEachIndexed { index, contact ->
        contact.firstName = contact.cleanContactNameFromUnUsedCharacters().firstName
        contact.lastName = contact.cleanContactNameFromUnUsedCharacters().lastName
      }

      return recipientListFromRecipient.toTypedArray()
    }

    @JvmStatic
    fun createMessageNameListV2(
      context: Context,
      recipient: Recipient,
      threadRecipient: Recipient,
      isInboxArchiveMessage: Boolean,
      recipientList: List<Recipient>? = null,
      isGroup: Boolean,
      from: Contact = Contact("")
    ): Array<Contact> {


      val threadRecipientList = getRecipientsListFromThreadRecipient(threadRecipient)

      val rl = if (!isInboxArchiveMessage) {
        if (recipientList!!.size > 1) {
          recipientList!!.filter {
            it.e164.isPresent && it.e164.get() != getPhoneNumberInTestMode(context)
          } ?:threadRecipientList.filter {
            it.e164.isPresent && it.e164.get() != getPhoneNumberInTestMode(context)
          }
        } else {
          //Sending message in group that contains only me
          recipientList
        }
      } else {
        threadRecipientList.filter {
            it.e164.isPresent && it.e164.get() != from.toString()
          }
      }

      val recipientListFromRecipient: List<Contact> = if (isGroup) {

        rl.map {
          Contact(it.getDisplayName(context))
        }

      } else {
        if (isInboxArchiveMessage) {
          listOf(Contact(Recipient.self().profileName.toString()))
        } else {
          listOf(Contact(recipient.getDisplayName(context)))
        }
      }

      if (recipientListFromRecipient.toTypedArray().isEmpty()) {
        return arrayOf(Contact())
      }

      //SIG-437 - Clean list from [FSI]*[PDI]
      recipientListFromRecipient.forEachIndexed { index, contact ->
        contact.firstName = contact.cleanContactNameFromUnUsedCharacters().firstName
        contact.lastName = contact.cleanContactNameFromUnUsedCharacters().lastName
      }

      return recipientListFromRecipient.toTypedArray()
    }

    @JvmStatic
    fun generateAttachmentName(messageId: Long, attachmentId: Long): String {
      return SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX + attachmentId + "_" + messageId
    }

    @JvmStatic
    fun getFileFromAttachmentId(context: Context, attachmentId: AttachmentId) : File {
      val uri = SignalDatabase.attachments.getAttachment(attachmentId)!!.uri
      Log.d("ArchiveUtil", "getFileFromAttachmentId -> uri $uri")
      return ArchiveFileUtil.getFileFromDataBaseUri(context, uri.toString())
    }

    @JvmStatic
    fun getMessageBody(messageBody: String?, mentionsList: List<Mention>): String? {
      return if (messageBody != null) {
        var result = messageBody ?: ""
        return if (mentionsList.isNotEmpty()) {
          mentionsList.forEachIndexed { index, mention ->
            val givenName =
              getRecipientFromRecipientID(mentionsList[index].recipientId).profileName.givenName
            val e164Object = getRecipientFromRecipientID(mentionsList[index].recipientId).e164
            val name = if (givenName.isEmpty()) {
              if (e164Object.isPresent) {
                e164Object.get()
              } else {
                ""
              }
            } else {
              givenName
            }
            result = result.replaceFirst("\uFFFC", "\u0040" + name)
          }
          result
        } else {
          messageBody
        }
      } else {
        null
      }
    }

    @JvmStatic
    fun createPreviewLinkBody(
      incomingMediaMessage: IncomingMediaMessage?,
      outComingMediaMessage: OutgoingMessage?
    ): String? {
      var body = ""
      if (incomingMediaMessage != null) {
        return if (incomingMediaMessage.linkPreviews.isEmpty()) {
          getMessageBody(incomingMediaMessage.body, incomingMediaMessage.mentions)
        } else {
          generateBodyFromLinkPreview(incomingMediaMessage.linkPreviews[0]) + "\n" + incomingMediaMessage.body
        }
      } else if (outComingMediaMessage != null) {
        return if (outComingMediaMessage.linkPreviews.isEmpty()) {
          getMessageBody(outComingMediaMessage.body, outComingMediaMessage.mentions)
        } else {
          generateBodyFromLinkPreview(outComingMediaMessage.linkPreviews[0]) + "\n" + outComingMediaMessage.body
        }
      }
      return ""
    }

    @JvmStatic
    private fun generateBodyFromLinkPreview(linkPreview: LinkPreview?): String {
      var body = ""
      if (linkPreview!!.title.isNotEmpty()) {
        body += """
                Site title: ${linkPreview.title}
                
                """.trimIndent()
      }
      if (linkPreview.url.isNotEmpty()) {
        body += """
                Site url: ${linkPreview.url}
                
                """.trimIndent()
      }

      if (linkPreview.description.isNotEmpty()) {
        body += """
                Site description: ${linkPreview.description}
                
                """.trimIndent()
      }
      return body
    }

    @JvmStatic
    fun cleanMessageBodyFromUnusedCharacters(messageBody: String?): String {
      return if (messageBody != null && messageBody.isNotEmpty()) {
        messageBody.replace("\u2069", "").replace("\u2068", "")
      } else {
        ""
      }
    }

    @JvmStatic
    fun getRecipientFromRecipientID(recipientId: RecipientId): Recipient {
      return Recipient.resolved(recipientId)
    }

    @JvmStatic
    fun archiveOutboxMessage(context: Context, messageId: Long, message: OutgoingMessage) {


      var tempFileForArchiving: File? = null
      var isMediaMessage = false
      var filesToSend = arrayOfNulls<File>(message.attachments.size)
      for (i in message.attachments.indices) {
        tempFileForArchiving =
          ArchiveFileUtil.createFileFromContentUri(context, message.attachments[i].uri.toString())
        filesToSend[i] = tempFileForArchiving
        isMediaMessage = true
      }
      if (message.linkPreviews.isNotEmpty()) {
        if (message.linkPreviews[0].thumbnail.isPresent && message.linkPreviews[0].thumbnail.get().uri.toString()
            .isNotEmpty()
        ) {
          filesToSend = arrayOfNulls(1)
          filesToSend[0] = ArchiveFileUtil.createFileFromContentUri(
            context,
            message.linkPreviews[0].thumbnail.get().uri.toString()
          )
          isMediaMessage = true
        } else {
          isMediaMessage = false
        }

      }
      if (message.sharedContacts.size > 0) {
        filesToSend = arrayOfNulls<File>(1)
        val vcfFile = ArchiveFileUtil.createVCFFileFromContact(context, message.sharedContacts[0])
        filesToSend[0] = vcfFile
        isMediaMessage = true
      }

      //TODO - Archiving of "replay messages" (the original message is in "message.getQuote")
      if (message.outgoingQuote != null) {
        archiveMessageOutboxMMS(
          context,
          ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_SEND,
          message.threadRecipient,
          message,
          messageId,
          null
        )
        isMediaMessage = true
      }

      if (isMediaMessage) {
        archiveMessageOutboxMMS(
          context,
          ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_SEND,
          message.threadRecipient,
          message,
          messageId,
          filesToSend
        )
        for (i in filesToSend.indices) {
          updateArchiveSDKToSendMMSMessage(context, filesToSend[i]!!.name, true)
        }
      } else {
        if (!message.isGroupUpdate
          && !message.isExpirationUpdate
        ) {

          val messageBody = createPreviewLinkBody(null, message)
          archiveMessageOutboxV1(
            context,
            ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_SEND,
            message.threadRecipient,
            messageBody!!,
            messageId,
            message.sentTimeMillis
          )
        } else {
          //TODO - Group events/updates!!
        }

      }
    }

    @JvmStatic
    fun fetchFCMToken(context: Context, aITokenCallback : ITokenCallback?) {
      Log.d(TAG, "Starting fetch FCM token..")

      FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
          Log.d("ArchiverUtil", "fetchFCMToken Fetching FCM registration token failed  ${task.exception}")
          Log.d(TAG, "fetchFCMToken FCM_TM_UTILS Fetching FCM registration token failed  ${task.exception}")

          aITokenCallback?.onFetchingFailed()
          return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result
        Log.d(TAG, "FCM token is = $token")
        Log.d(TAG,"fetchFCMToken FCM_TM_UTILS FCM token is = $token")

        PrefManager.setStringPref(
          context,
          ArchivePreferenceConstants.FCM_TOKEN_PREFERENCE_KEY,
          token
        )

        aITokenCallback?.onFetchingSucceed()
      })

    }

    interface ITokenCallback {
      fun onFetchingFailed()
      fun onFetchingSucceed()
    }

    @JvmStatic
    fun getFCMTokenIfExists(context: Context): String? {
      return PrefManager.getStringPref(
        context,
        ArchivePreferenceConstants.FCM_TOKEN_PREFERENCE_KEY,
        ""
      )
    }

    @JvmStatic
    fun getUniqueMessageId(context: Context, messageSendingTime: Long,  from: String): String {

        return "${messageSendingTime}_${from.replace("+","")}"

    }

    @JvmStatic
    fun getUniqueDeleteMessageId(messageSendingTime: Long,  from: String, isDeleteMessage: Boolean = false, deletedForAll : Boolean = false): String {

      var uniqueMessageId = "${messageSendingTime}_${from.replace("+","")}"

      if(isDeleteMessage){
        uniqueMessageId = (if (deletedForAll) "DELETE_FOR_ALL-" else "DELETE_FOR_ME-") + uniqueMessageId
      }
        return uniqueMessageId

    }

    @JvmStatic
    fun doTeleMessageKeepAlivePing(context: Context) {

      object : Thread() {
        override fun run() {
          try {
            val nm = NetworkManager(
              context,
              PrefManager.getStringPref(context, "baseurl", Definitions.BaseUrl)
            )
            var sr: KeepAliveRequest? = null
            sr = if (getFCMTokenIfExists(context) == null || getFCMTokenIfExists(context)!!
                .isEmpty()
            ) {
              Log.d(TAG, "KeepAliveRequest with 1 param")
              KeepAliveRequest(AuthenticationAppType.TELEGRAM.aAppServerId.toString())
            } else {
              Log.d(TAG, "KeepAliveRequest with 4 param")
              KeepAliveRequest(
                AuthenticationAppType.SIGNAL.aAppServerId.toString(),
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE.toString(),
                getFCMTokenIfExists(context)
              )
            }
            Log.d(TAG, "request KeepAlive: " + Gson().toJson(sr))
            //Log.d(TAG, "send message:" + sr.getTextContent());
            val res = nm.start<KeepALiveResponse>(sr, null, context, true);
            //        Log.d(TAG, "response KeepAlive: " + new Gson().toJson(res));
            if (res == null) {
              Log.d(TAG, "response is null")
            } else if (res.isSuccessful) {
              Log.d(TAG, "response.isSuccessful() = true")
            } else {
              Log.d(TAG, "response.isSuccessful() = false")
            }
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }.start()


    }

    fun Contact.cleanContactNameFromUnUsedCharacters() : Contact{
        this.firstName = this.firstName.replace("\u2068", "").replace("\u2069","").replace("\u202c","")
        this.lastName = this.lastName.replace("\u2068", "").replace("\u2069","").replace("\u202c","")
      return this
    }
  }

  enum class InboxArchiveTypes {
    MEDIA,
    HYPER_LINK,
    STICKER,
    CONTACT,
    MENTIONS;
  }
}