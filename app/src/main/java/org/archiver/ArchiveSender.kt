package org.archiver

import android.content.Context
import com.tm.androidcopysdk.DataGrabber
import org.archiver.ArchiveUtil.Companion.cleanMessageBodyFromUnusedCharacters
import org.archiver.ArchiveUtil.Companion.createMessageNameList
import org.archiver.ArchiveUtil.Companion.createSubjectForArchiving
import org.archiver.ArchiveUtil.Companion.createToRecipientList
import org.archiver.ArchiveUtil.Companion.fromContactName
import org.archiver.ArchiveUtil.Companion.getChatMode
import org.archiver.ArchiveUtil.Companion.getChatName
import org.archiver.ArchiveUtil.Companion.getFromPartForSubject
import org.archiver.ArchiveUtil.Companion.getGroupInboxRecipientNumber
import org.archiver.ArchiveUtil.Companion.getMessageBody
import org.archiver.ArchiveUtil.Companion.groupId
import org.tm.archive.database.DatabaseFactory
import org.tm.archive.mms.IncomingMediaMessage
import org.tm.archive.mms.OutgoingMediaMessage
import org.tm.archive.recipients.Recipient
import org.tm.archive.sms.IncomingTextMessage
import java.io.File

class ArchiveSender {

    companion object{

        private fun sendArchiveMessage(context: Context, aProtocolType: ArchiveConstants.ProtocolType, toRecipientsList: Array<String>, from: String,  messageBody: String?, messageId: String, dateInTimeStamp: Long, subject: String, chatMode: DataGrabber.CHAT_MODE, chatName: String, chatId: String?, fromNameString: String, toRecipientsListNames: Array<String>, archiveFile: Array<File?>? = null){

            if(archiveFile == null) {
                DataGrabber.getInstance(context).setMessage(aProtocolType.type, toRecipientsList, from, messageBody, messageId, dateInTimeStamp.toString(), subject, ArchiveUtil.getPhoneNumberInTestMode(context), chatMode, chatName, chatId, fromNameString, ArchiveUtil.getPhoneNumberInTestMode(context), toRecipientsListNames, toRecipientsList)
            }else {
                DataGrabber.getInstance(context).setMmsMessage(aProtocolType.type, toRecipientsList, from, messageBody, messageId, dateInTimeStamp.toString(), subject, ArchiveUtil.getPhoneNumberInTestMode(context), chatMode, chatName, chatId, fromNameString, ArchiveUtil.getPhoneNumberInTestMode(context), toRecipientsListNames, toRecipientsList, archiveFile)
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
            val toName = createMessageNameList(context, archiveRecipient, isInbox, archiveRecipient.participants, isGroup)
            val messageBody = if(message.messageBody != null){
                message.messageBody
            }else{
                ""
            }
            sendArchiveMessage(context, type, toRecipientsList, from, messageBody , messageId.toString(), System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)
        }

        fun archiveMessageOutbox(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, messageBody: String, messageId: Long) {
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
            val toName = createMessageNameList(context, archiveRecipient, isInbox, archiveRecipient.participants, isGroup)
            val cleanMessageBody = cleanMessageBodyFromUnusedCharacters(messageBody)
            sendArchiveMessage(context, type, toRecipientsList, from, cleanMessageBody, messageId.toString(), System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)
        }


        //This method also sent sms if attachments list size is 0
        fun archiveMessageOutboxMMS(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, message: OutgoingMediaMessage, messageId: Long, archiveFile: Array<File?>? = null) {
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = archiveRecipient.isGroup
            val inboxRecipient = ""
            var groupTitle = ""
            if (message.recipient.groupId.isPresent) {
                groupTitle = DatabaseFactory.getGroupDatabase(context).getGroup(message.recipient.groupId.get()).get().title
            }

            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false, groupTitle)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient, isGroup)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, archiveRecipient.participants, isGroup)
            
            sendArchiveMessage(context, type, toRecipientsList, from, message.body, messageId.toString(), System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)
        }

        fun archiveMessageInboxMMS(aContext: Context, aGroupTitle: String, aType: ArchiveConstants.ProtocolType, aArchiveRecipient: Recipient, aRecipientList: MutableList<Recipient>?, aMessage: IncomingMediaMessage, aMessageId: Long, aArchiveFile:  File? = null) {
            val listOfFile = Array(1){aArchiveFile}
            archiveMessageInboxMMS(aContext, aGroupTitle, aType, aArchiveRecipient, aRecipientList, aMessage,aMessageId, listOfFile)
        }

        fun archiveMessageInboxMMS(context: Context, groupTitle: String, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, recipientList: MutableList<Recipient>?, message: IncomingMediaMessage, messageId: Long, archiveFile:  Array<File?>? = null) {
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = message.isGroupMessage
            val inboxRecipient = ""
            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, isGroup, from, recipientList)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false, groupTitle)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient, isGroup)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox,  recipientList, isGroup)
            val messageBody = getMessageBody(message.body, message.mentions)
            sendArchiveMessage(context, type, toRecipientsList, from, messageBody, messageId.toString(), System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)
        }

    }
}