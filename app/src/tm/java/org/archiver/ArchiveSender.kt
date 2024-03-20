package org.archiver

import android.app.Activity
import android.content.Context
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.DataGrabber
import com.tm.androidcopysdk.ISendLogCallback
import com.tm.androidcopysdk.utils.Contact
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.androidcopysdk.utils.RuntimeObject.getCallerClassMethodAndLine
import com.tm.androidcopysdk.utils.TMCredentialsStore
import com.tm.logger.Log
import org.archiver.ArchiveLogger.Companion.sendArchiveLog
import org.archiver.ArchiveUtil.Companion.cleanMessageBodyFromUnusedCharacters
import org.archiver.ArchiveUtil.Companion.createMessageNameList
import org.archiver.ArchiveUtil.Companion.createMessageNameListV2
import org.archiver.ArchiveUtil.Companion.createSubjectForArchiving
import org.archiver.ArchiveUtil.Companion.createToRecipientList
import org.archiver.ArchiveUtil.Companion.createToRecipientListV2
import org.archiver.ArchiveUtil.Companion.fromContactName
import org.archiver.ArchiveUtil.Companion.getChatMode
import org.archiver.ArchiveUtil.Companion.getChatName
import org.archiver.ArchiveUtil.Companion.getChatNameV2
import org.archiver.ArchiveUtil.Companion.getFromPartForSubject
import org.archiver.ArchiveUtil.Companion.getGroupInboxRecipientNumber
import org.archiver.ArchiveUtil.Companion.groupId

import org.tm.archive.attachments.DatabaseAttachment
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.mms.IncomingMessage
import org.tm.archive.mms.OutgoingMessage
import org.tm.archive.recipients.Recipient
import java.io.File

class ArchiveSender {

    companion object{

        const val TAG = "ArchiveSender"
        private fun sendArchiveMessage(context: Context, uniqueMessageId: String , aProtocolType: ArchiveConstants.ProtocolType, toRecipientsList: Array<String>, from: String, messageBody: String?, dateInTimeStamp: Long, subject: String, chatMode: DataGrabber.CHAT_MODE, chatName: String, chatId: String?, fromNameString: Contact, toRecipientsListNames: Array<Contact>, archiveFile: Array<File?>? = null){
          //  Log.d(TAG, "messageId = $uniqueMessageId message text $messageBody")
          Log.d(TAG, "sendArchiveMessage -> body = $messageBody sub = $subject")

          if(archiveFile == null || archiveFile[0] == null) {
            android.util.Log.d("DavidLogger", "setMessage $uniqueMessageId")
//            DataGrabber.getInstance(context).setMessage(aProtocolType.type, toRecipientsList, from, messageBody, uniqueMessageId, dateInTimeStamp.toString(), subject, ArchiveUtil.getPhoneNumberInTestMode(context), chatMode, chatName, chatId, fromNameString, from, toRecipientsListNames, toRecipientsList)
            }else {
            android.util.Log.d("DavidLogger", "setMmsMessage(${getCallerClassMethodAndLine(2)}) $uniqueMessageId ${archiveFile.map { it?.absolutePath }}")
//                DataGrabber.getInstance(context).setMmsMessage(aProtocolType.type, toRecipientsList, from, messageBody, uniqueMessageId /*+ "M"*/, dateInTimeStamp.toString(), subject, ArchiveUtil.getPhoneNumberInTestMode(context), chatMode, chatName, chatId, fromNameString, from, toRecipientsListNames, toRecipientsList, archiveFile)
            }
        }


      fun updateArchiveSDKToSendMMSMessage(context: Context, fileName: String, needCompress: Boolean){
        android.util.Log.d("DavidLogger", "updateFileMms(${getCallerClassMethodAndLine(2)}) $fileName")
//            DataGrabber.getInstance(context).updateFileMms(fileName, needCompress)
        }

        fun archiveMessageInbox(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, message: IncomingMessage, messageId: Long, groupTile: String) {
          Log.d(TAG, "archiveMessageInbox")
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = message.groupId != null
            var inboxRecipient = ""
            if (archiveRecipient.isGroup) {
                inboxRecipient = getGroupInboxRecipientNumber(archiveRecipient, message)
            }
            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false, groupTile)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient, isGroup)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, ArchiveUtil.getRecipientsListFromParticipantIds(archiveRecipient), isGroup, Contact(from))
            val messageBody = if(message.body != null){
                message.body
            }else{
                ""
            }

            val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, message.sentTimeMillis/*sentTimestampMillis*/, from)

            sendArchiveMessage(context,uniqueMessageId , type, toRecipientsList, from, messageBody, System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)

            sendArchiveLog("archiveMessageInbox --> type = $type  uniqueMessageId Message ID = $uniqueMessageId subject = $subject group name = $groupTile")

        }

      fun archiveMessageInboxV2(context: Context, type: ArchiveConstants.ProtocolType, senderRecipient: Recipient, threadRecipient: Recipient, messageBody: String? , messageSendingTime: Long?) {
        Log.d(TAG, "archiveMessageInboxV2 -> message = $messageBody")
        val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
        val isGroup = threadRecipient.isGroup
        var inboxRecipient = ""
        if (isGroup) {
          inboxRecipient = if(senderRecipient.e164.isPresent) senderRecipient.e164.get() else "0"//getGroupInboxRecipientNumberV2(senderRecipient, threadRecipient)
        }
        val groupTile = if(threadRecipient.getGroupName(context) != null) threadRecipient.getGroupName(context) else ""
        val from = getFromPartForSubject(context, isInbox, senderRecipient, inboxRecipient)
        val toRecipientsList = createToRecipientListV2(context, isInbox, senderRecipient,threadRecipient, isGroup, from)
        val subject = createSubjectForArchiving(context, isInbox, isGroup, senderRecipient, inboxRecipient, false, groupTile)
        val chatMode = getChatMode(isGroup)
        val chatName = getChatNameV2(context, threadRecipient, isGroup)
        val chatId = groupId(threadRecipient)
        val fromContactName = fromContactName(context, senderRecipient, isInbox)
        val toName = createMessageNameListV2(context, senderRecipient, threadRecipient, isInbox, ArchiveUtil.getRecipientsListFromParticipantIds(senderRecipient), isGroup, Contact(from))
        var sendingTimeCheck = 0L
        if (messageSendingTime == null) {
          Log.e("ArchiveSender", "messageSendingTime is null!!")
        } else {
          sendingTimeCheck = messageSendingTime
        }
        val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, sendingTimeCheck, from)

        sendArchiveMessage(context,uniqueMessageId , type, toRecipientsList, from, messageBody, System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)

        sendArchiveLog("archiveMessageInbox --> type = $type  uniqueMessageId Message ID = $uniqueMessageId subject = $subject group name = $groupTile")

      }


      fun archiveMessageOutboxV1(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, messageBody: String, messageId: Long, sendingTime: Long?) {
            Log.d(TAG, "archiveMessageOutboxV1 -> message = $messageBody")
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = archiveRecipient.isGroup
            val inboxRecipient = ""

            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from)
            val chatName = getChatName(context, archiveRecipient, isGroup)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false, chatName)
            val chatMode = getChatMode(isGroup)

            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, ArchiveUtil.getRecipientsListFromParticipantIds(archiveRecipient), isGroup, Contact(from))
            val cleanMessageBody = cleanMessageBodyFromUnusedCharacters(messageBody)

        var sendingTimeCheck = 0L
        if (sendingTime == null) {
          Log.e("ArchiveSender", "sendingTime is null!!")
        } else {
          sendingTimeCheck = sendingTime
        }
        val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, sendingTimeCheck, from)

            sendArchiveMessage(context,uniqueMessageId,  type, toRecipientsList, from, cleanMessageBody, System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)
            sendArchiveLog("archiveMessageOutbox --> type = $type subject = $subject  uniqueMessageId Message ID = $messageId")
        }


        //This method also sent sms if attachments list size is 0
        fun archiveMessageOutboxMMS(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, message: OutgoingMessage, messageId: Long, archiveFile: Array<File?>? = null) {

            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = archiveRecipient.isGroup
            val inboxRecipient = ""
            var groupTitle = ""
            if (message.threadRecipient.groupId.isPresent) {
                groupTitle = SignalDatabase.groups.getGroup(message.threadRecipient.groupId.get()).get().title!!
            }

            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false, groupTitle)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient, isGroup)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, ArchiveUtil.getRecipientsListFromParticipantIds(archiveRecipient), isGroup, Contact(from))
            val messageBody = ArchiveUtil.createPreviewLinkBody(null, message)

            val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, message.sentTimeMillis, from)

            sendArchiveMessage(context, uniqueMessageId, type, toRecipientsList, from, messageBody , System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)

            sendArchiveLog("archiveMessageOutboxMMS --> type = $type subject = $subject  uniqueMessageId Message ID = $uniqueMessageId")

        }

        fun archiveMessageInboxMMS(aContext: Context, aGroupTitle: String?, aType: ArchiveConstants.ProtocolType, aArchiveRecipient: Recipient, aRecipientList: MutableList<Recipient>?, aMessage: IncomingMessage, aMessageId: Long, aArchiveFile: File? = null) {
            var listOfFile: Array<File?>? = null
            if(aArchiveFile != null) {
                listOfFile = Array(1) { aArchiveFile }
            }
            archiveMessageInboxMMS(aContext, aGroupTitle, aType, aArchiveRecipient, aRecipientList, aMessage, aMessageId, listOfFile)
        }

        fun archiveMessageInboxMMS(context: Context, groupTitle: String?, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, recipientList: MutableList<Recipient>?, message: IncomingMessage, messageId: Long, archiveFile: Array<File?>? = null) {
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = message.isGroupMessage
            val inboxRecipient = ""
            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from, recipientList)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false, groupTitle)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient, isGroup,groupTitle?: "")
            val chatId = groupId(archiveRecipient, message.groupId)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, recipientList, isGroup, Contact(from))
            val messageBody = ArchiveUtil.createPreviewLinkBody(message, null)

            val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, message.sentTimeMillis, from)

            sendArchiveMessage(context, uniqueMessageId, type, toRecipientsList, from, messageBody, System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)

            sendArchiveLog("archiveMessageInboxMMS --> type = $type subject = $subject recipientList $recipientList  uniqueMessageId Message ID = $uniqueMessageId")


        }


      fun archiveMessageInboxMMSV2(context: Context, type: ArchiveConstants.ProtocolType, senderRecipient: Recipient, threadRecipient: Recipient, messageBody: String , messageSendingTime: Long, aArchiveFile: File? = null) {
        var listOfFile: Array<File?>? = null
        if(aArchiveFile != null) {
          listOfFile = Array(1) { aArchiveFile }
        }
        archiveMessageInboxMMSV2(context, type, senderRecipient, threadRecipient, messageBody, messageSendingTime, listOfFile)
      }

      fun archiveMessageInboxMMSV2(context: Context, type: ArchiveConstants.ProtocolType, senderRecipient: Recipient, threadRecipient: Recipient, messageBody: String? , messageSendingTime: Long, archiveFile: Array<File?>? = null) {
        val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
        val isGroup = threadRecipient.isGroup
        val inboxRecipient = ""
        val groupTile = if(threadRecipient.getGroupName(context) != null) threadRecipient.getGroupName(context) else ""
        val from = getFromPartForSubject(context, isInbox, senderRecipient, inboxRecipient)
        val toRecipientsList = createToRecipientListV2(context, isInbox, senderRecipient,threadRecipient, isGroup, from)
        val subject = createSubjectForArchiving(context, isInbox, isGroup, senderRecipient, inboxRecipient, false, groupTile)
        val chatMode = getChatMode(isGroup)
        val chatName =  getChatNameV2(context, threadRecipient, isGroup)
        val chatId = groupId(threadRecipient)
        val fromContactName = fromContactName(context, senderRecipient, isInbox)
        val toName = createMessageNameListV2(context, senderRecipient, threadRecipient, isInbox, ArchiveUtil.getRecipientsListFromParticipantIds(senderRecipient), isGroup, Contact(from))

      //  val messageBody = ArchiveUtil.createPreviewLinkBody(message, null) TODO: FIXIT!!

        val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, messageSendingTime, from)

        sendArchiveMessage(context, uniqueMessageId, type, toRecipientsList, from,
          messageBody ?: "", System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)

      //  sendArchiveLog("archiveMessageInboxMMS --> type = $type subject = $subject recipientList $recipientList  uniqueMessageId Message ID = $uniqueMessageId")


      }

        fun archiveMessageOutboxSyncMMS(context: Context, groupTitle: String, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, recipientList: MutableList<Recipient>?, message: OutgoingMessage, messageId: Long, archiveFile: Array<File?>? = null) {
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = archiveRecipient.isGroup
            val inboxRecipient = ""
            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from, recipientList)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false, groupTitle)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient, isGroup)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, recipientList, isGroup, Contact(from))
            val messageBody = ArchiveUtil.createPreviewLinkBody( null, message)

            val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, message.sentTimeMillis, from)

            sendArchiveMessage(context,uniqueMessageId,  type, toRecipientsList, from, messageBody, System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)

            sendArchiveLog("archiveMessageOutboxSyncMMS --> type = $type subject = $subject  uniqueMessageId Message ID = $uniqueMessageId")
        }






      ///////////DELETE MESSAGE/////////

      fun sendArchiveDeleteMessage(context: Context, message: MessageRecord, type: ArchiveConstants.ProtocolType, isDeletedForAll: Boolean){

        val archiveRecipient = message.fromRecipient
        val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
        val isGroup = archiveRecipient.isGroup

        val status = getMessageStatus(isGroup, message)
        val deletePrefixMessage = getDeleteMessagePrefix(isDeletedForAll, status)

        val inboxRecipient = ""

        val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
        val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from)
        val chatName = getChatName(context, archiveRecipient, isGroup)
        val subject = createSubjectForArchiving(
          context,
          isInbox,
          isGroup,
          archiveRecipient,
          inboxRecipient,
          false,
          chatName
        )
        val chatMode = getChatMode(isGroup)

        val chatId = groupId(archiveRecipient)
        val fromContactName = fromContactName(context, archiveRecipient, isInbox)
        val toName = createMessageNameList(context, archiveRecipient, isInbox, ArchiveUtil.getRecipientsListFromParticipantIds(archiveRecipient), isGroup, Contact(from))
        val cleanMessageBody = cleanMessageBodyFromUnusedCharacters(message.body)
        val listFileAsString = getListFileAsString(message)
        val uniqueMessageId = ArchiveUtil.getUniqueDeleteMessageId(message.timestamp, from, true, isDeletedForAll)
        val originalMessageId = "Original Message (Msg ID - " + ArchiveUtil.getUniqueDeleteMessageId(message.timestamp, from, false, isDeletedForAll) + ")"
        val deleteMessageBody = deletePrefixMessage + subject + "\n\n" + originalMessageId + "\n\n" + cleanMessageBody + listFileAsString

        sendArchiveMessage(context,uniqueMessageId,  type, toRecipientsList, from, deleteMessageBody, System.currentTimeMillis(), deletePrefixMessage + subject, chatMode, chatName, chatId, fromContactName, toName)

      }

      private fun getDeleteMessagePrefix(
        isDeletedForAll: Boolean,
        status: String
      ) = if (isDeletedForAll) {
        "DELETED For All – $status "
      } else {
        "DELETED For Me – $status "
      }

      private fun getMessageStatus(
        isGroup: Boolean,
        message: MessageRecord
      ) = if (!isGroup) {
        if (message.hasReadReceipt()) "READ" else "UNREAD"
      } else {
        "UNKNOWN"
      }

      private fun getListFileAsString(
        messageRecord: MessageRecord
      ): String {
        //(message as MmsMessageRecord).slideDeck
        if (!messageRecord.isMms) {
          return ""
        }

        val filesName = (messageRecord as MmsMessageRecord).slideDeck

        val listOfDeletedFileNames = StringBuilder()

        listOfDeletedFileNames.append("\n").append("Files: ").append("\n")
        for (i in filesName.slides) {
          listOfDeletedFileNames.append(ArchiveUtil.generateAttachmentName((i.asAttachment() as DatabaseAttachment).attachmentId.id, (i.asAttachment() as DatabaseAttachment).attachmentId.id)).append("\n")
        }

        return "$listOfDeletedFileNames"
      }

      fun sendLogs(activity: Activity, listener : ISendLogCallback) {
        AndroidCopySDK.getInstance(activity).sentLogs(
          activity,
          listener,
          PrefManager.getStringPref(activity, ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER, ""),
          "Signal Archiver logs",
          TMCredentialsStore.getInstance(activity).userName(activity),
          "",
          PrefManager.getStringPref(activity, "pref_my_first_name", ""),
          PrefManager.getStringPref(activity, "pref_my_last_name", ""),
          PrefManager.getStringPref(activity, "pref_my_email", ""),
          ArchivePreferenceConstants.GENERATE_TOK_NAME,
          ArchivePreferenceConstants.GENERATE_TOK_PASS
        )
      }

    }
}