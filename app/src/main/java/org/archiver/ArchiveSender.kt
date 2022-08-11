package org.archiver

import android.content.Context
import com.tm.androidcopysdk.DataGrabber
import com.tm.androidcopysdk.utils.Contact
import org.archiver.ArchiveLogger.Companion.sendArchiveLog
import org.archiver.ArchiveUtil.Companion.cleanMessageBodyFromUnusedCharacters
import org.archiver.ArchiveUtil.Companion.createMessageNameList
import org.archiver.ArchiveUtil.Companion.createSubjectForArchiving
import org.archiver.ArchiveUtil.Companion.createToRecipientList
import org.archiver.ArchiveUtil.Companion.fromContactName
import org.archiver.ArchiveUtil.Companion.getChatMode
import org.archiver.ArchiveUtil.Companion.getChatName
import org.archiver.ArchiveUtil.Companion.getFromPartForSubject
import org.archiver.ArchiveUtil.Companion.getGroupInboxRecipientNumber
import org.archiver.ArchiveUtil.Companion.groupId
import org.signal.glide.Log
import org.tm.archive.attachments.DatabaseAttachment
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MediaMmsMessageRecord
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.mms.IncomingMediaMessage
import org.tm.archive.mms.OutgoingMediaMessage
import org.tm.archive.recipients.Recipient
import org.tm.archive.sms.IncomingTextMessage
import java.io.File

class ArchiveSender {

    companion object{

        private fun sendArchiveMessage(context: Context, uniqueMessageId: String , aProtocolType: ArchiveConstants.ProtocolType, toRecipientsList: Array<String>, from: String, messageBody: String?, dateInTimeStamp: Long, subject: String, chatMode: DataGrabber.CHAT_MODE, chatName: String, chatId: String?, fromNameString: Contact, toRecipientsListNames: Array<Contact>, archiveFile: Array<File?>? = null){
            Log.d("MNMNMDD", "messageId = " + uniqueMessageId + " message text " + messageBody)

            if(archiveFile == null) {
                DataGrabber.getInstance(context).setMessage(aProtocolType.type, toRecipientsList, from, messageBody, uniqueMessageId, dateInTimeStamp.toString(), subject, ArchiveUtil.getPhoneNumberInTestMode(context), chatMode, chatName, chatId, fromNameString, from, toRecipientsListNames, toRecipientsList)
            }else {
                DataGrabber.getInstance(context).setMmsMessage(aProtocolType.type, toRecipientsList, from, messageBody, uniqueMessageId /*+ "M"*/, dateInTimeStamp.toString(), subject, ArchiveUtil.getPhoneNumberInTestMode(context), chatMode, chatName, chatId, fromNameString, from, toRecipientsListNames, toRecipientsList, archiveFile)
            }
        }


      fun updateArchiveSDKToSendMMSMessage(context: Context, fileName: String, needCompress: Boolean){
            DataGrabber.getInstance(context).updateFileMms(fileName, needCompress)
        }

        fun archiveMessageInbox(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, message: IncomingTextMessage, messageId: Long, groupTile: String) {

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
            val toName = createMessageNameList(context, archiveRecipient, isInbox, archiveRecipient.participants, isGroup, Contact(from))
            val messageBody = if(message.messageBody != null){
                message.messageBody
            }else{
                ""
            }

            val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, message.sentTimestampMillis, from)

            sendArchiveMessage(context,uniqueMessageId , type, toRecipientsList, from, messageBody, System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)

            sendArchiveLog("archiveMessageInbox --> type = $type  uniqueMessageId Message ID = $uniqueMessageId subject = $subject group name = $groupTile")

        }

        fun archiveMessageOutbox(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, messageBody: String, messageId: Long, sendingTime: Long) {

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
            val toName = createMessageNameList(context, archiveRecipient, isInbox, archiveRecipient.participants, isGroup, Contact(from))
            val cleanMessageBody = cleanMessageBodyFromUnusedCharacters(messageBody)

            val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, sendingTime, from)

            sendArchiveMessage(context,uniqueMessageId,  type, toRecipientsList, from, cleanMessageBody, System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)
            sendArchiveLog("archiveMessageOutbox --> type = $type subject = $subject  uniqueMessageId Message ID = $messageId")
        }


        //This method also sent sms if attachments list size is 0
        fun archiveMessageOutboxMMS(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, message: OutgoingMediaMessage, messageId: Long, archiveFile: Array<File?>? = null) {

            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = archiveRecipient.isGroup
            val inboxRecipient = ""
            var groupTitle = ""
            if (message.recipient.groupId.isPresent) {
                groupTitle = SignalDatabase.groups.getGroup(message.recipient.groupId.get()).get().title
            }

            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false, groupTitle)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient, isGroup)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, archiveRecipient.participants, isGroup, Contact(from))
            val messageBody = ArchiveUtil.createPreviewLinkBody(null, message)

            val uniqueMessageId = ArchiveUtil.getUniqueMessageId(context, message.sentTimeMillis, from)

            sendArchiveMessage(context, uniqueMessageId, type, toRecipientsList, from, messageBody , System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)

            sendArchiveLog("archiveMessageOutboxMMS --> type = $type subject = $subject  uniqueMessageId Message ID = $uniqueMessageId")

        }

        fun archiveMessageInboxMMS(aContext: Context, aGroupTitle: String?, aType: ArchiveConstants.ProtocolType, aArchiveRecipient: Recipient, aRecipientList: MutableList<Recipient>?, aMessage: IncomingMediaMessage, aMessageId: Long, aArchiveFile: File? = null) {
            var listOfFile: Array<File?>? = null
            if(aArchiveFile != null) {
                listOfFile = Array(1) { aArchiveFile }
            }
            archiveMessageInboxMMS(aContext, aGroupTitle, aType, aArchiveRecipient, aRecipientList, aMessage, aMessageId, listOfFile)
        }

        fun archiveMessageInboxMMS(context: Context, groupTitle: String?, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, recipientList: MutableList<Recipient>?, message: IncomingMediaMessage, messageId: Long, archiveFile: Array<File?>? = null) {
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

        fun archiveMessageOutboxSyncMMS(context: Context, groupTitle: String, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, recipientList: MutableList<Recipient>?, message: OutgoingMediaMessage, messageId: Long, archiveFile: Array<File?>? = null) {
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

        val archiveRecipient = message.recipient
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
        val toName = createMessageNameList(context, archiveRecipient, isInbox, archiveRecipient.participants, isGroup, Contact(from))
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
        if (message.readReceiptCount == 0) "UNREAD" else "READ"
      } else {
        "UNKNOWN"
      }

      private fun getListFileAsString(
        messageRecord: MessageRecord
      ): String {
        //(message as MediaMmsMessageRecord).slideDeck
        if (!messageRecord.isMms) {
          return ""
        }

        val filesName = (messageRecord as MediaMmsMessageRecord).slideDeck

        val listOfDeletedFileNames = StringBuilder()

        listOfDeletedFileNames.append("\n").append("Files: ").append("\n")
        for (i in filesName.slides) {
          listOfDeletedFileNames.append(ArchiveUtil.generateAttachmentName((i.asAttachment() as DatabaseAttachment).attachmentId.rowId, (i.asAttachment() as DatabaseAttachment).attachmentId.uniqueId)).append("\n")
        }

        return "$listOfDeletedFileNames"
      }



    }
}