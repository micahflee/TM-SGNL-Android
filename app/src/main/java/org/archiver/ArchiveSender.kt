package org.archiver

import android.content.Context
import com.tm.androidcopysdk.DataGrabber
import org.archiver.ArchiveUtil.Companion.createMessageNameList
import org.archiver.ArchiveUtil.Companion.createSubjectForArchiving
import org.archiver.ArchiveUtil.Companion.createToRecipientList
import org.archiver.ArchiveUtil.Companion.fromContactName
import org.archiver.ArchiveUtil.Companion.getChatMode
import org.archiver.ArchiveUtil.Companion.getChatName
import org.archiver.ArchiveUtil.Companion.getFromPartForSubject
import org.archiver.ArchiveUtil.Companion.getGroupInboxRecipientNumber
import org.archiver.ArchiveUtil.Companion.groupId
import org.thoughtcrime.securesms.mms.IncomingMediaMessage
import org.thoughtcrime.securesms.mms.OutgoingMediaMessage
import org.thoughtcrime.securesms.recipients.Recipient
import org.thoughtcrime.securesms.sms.IncomingTextMessage
import org.thoughtcrime.securesms.sms.OutgoingTextMessage
import java.io.File

class ArchiveSender {

    companion object{

        private fun sendArchiveMessage(context: Context, aProtocolType: ArchiveConstants.ProtocolType, toRecipientsList: Array<String>, from: String, messageBody: String? = "", messageId: String, dateInTimeStamp: Long, subject: String, chatMode: DataGrabber.CHAT_MODE, chatName: String, chatId: String?, fromNameString: String, toRecipientsListNames: Array<String>, archiveFile: File? = null){

            if(archiveFile == null) {
                DataGrabber.getInstance(context).setMessage(aProtocolType.type, toRecipientsList, from, messageBody, messageId, dateInTimeStamp.toString(), subject, ArchiveUtil.getPhoneNumberInTestMode(context), chatMode, chatName, chatId, fromNameString, ArchiveUtil.getPhoneNumberInTestMode(context), toRecipientsListNames, toRecipientsList)
            }else {
                DataGrabber.getInstance(context).setMmsMessage(aProtocolType.type, toRecipientsList, from, messageBody, messageId, dateInTimeStamp.toString(), subject, ArchiveUtil.getPhoneNumberInTestMode(context), chatMode, chatName, chatId, fromNameString, ArchiveUtil.getPhoneNumberInTestMode(context), toRecipientsListNames, toRecipientsList, archiveFile)
            }
        }


        fun updateArchiveSDKToSendMMSMessage(context: Context, fileName: String, needCompress: Boolean){
            DataGrabber.getInstance(context).updateFileMms(fileName, needCompress)
        }

        fun archiveMessageInbox(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, message: IncomingTextMessage, messageId: Long) {
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = message.groupId != null
            var inboxRecipient = ""
            if (archiveRecipient.isGroup) {
                inboxRecipient = getGroupInboxRecipientNumber(archiveRecipient, message)
            }
            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, from)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, from)
            sendArchiveMessage(context, type, toRecipientsList, from, message.messageBody, messageId.toString(), System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)
        }

        fun archiveMessageOutbox(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, message: OutgoingTextMessage, messageId: Long) {
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = archiveRecipient.isGroup
            val inboxRecipient = ""
            /*  if(isInbox && isGroup) {
      inboxRecipient = ArchiveUtil.Companion.getGroupInboxRecipientNumber(archiveRecipient, null);
    }*/
            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, from)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, from)
            sendArchiveMessage(context, type, toRecipientsList, from, message.messageBody, messageId.toString(), System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName)
        }


        //This method also sent sms if attachments list size is 0
        fun archiveMessageOutboxMMS(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, message: OutgoingMediaMessage, messageId: Long, archiveFile: File? = null) {
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = archiveRecipient.isGroup
            val inboxRecipient = ""
            /*  if(isInbox && isGroup) {
      inboxRecipient = ArchiveUtil.Companion.getGroupInboxRecipientNumber(archiveRecipient, null);
    }*/
            val toRecipientList = if(!isGroup) {
                listOf(archiveRecipient)
            } else{
                message.recipient.participants.filter { it.e164.isPresent }
            }

            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox,/*isGroup, archiveRecipient*/archiveRecipient, from)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, from)
            sendArchiveMessage(context, type, toRecipientsList, from, message.body, messageId.toString(), System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)
        }

        //This method also sent sms if attachments list size is 0
        fun archiveMessageInboxMMS(context: Context, type: ArchiveConstants.ProtocolType, archiveRecipient: Recipient, recipientList: MutableList<Recipient>, message: IncomingMediaMessage, messageId: Long, archiveFile: File? = null) {
            val isInbox = type === ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_INBOX
            val isGroup = archiveRecipient.isGroup
            val inboxRecipient = ""
            /*  if(isInbox && isGroup) {
      inboxRecipient = ArchiveUtil.Companion.getGroupInboxRecipientNumber(archiveRecipient, null);
    }*/
            val from = getFromPartForSubject(context, isInbox, archiveRecipient, inboxRecipient)
            val toRecipientsList = createToRecipientList(context, isInbox, archiveRecipient, from)
            val subject = createSubjectForArchiving(context, isInbox, isGroup, archiveRecipient, inboxRecipient, false)
            val chatMode = getChatMode(isGroup)
            val chatName = getChatName(context, archiveRecipient)
            val chatId = groupId(archiveRecipient)
            val fromContactName = fromContactName(context, archiveRecipient, isInbox)
            val toName = createMessageNameList(context, archiveRecipient, isInbox, from)
            sendArchiveMessage(context, type, toRecipientsList, from, message.body, messageId.toString(), System.currentTimeMillis(), subject, chatMode, chatName, chatId, fromContactName, toName, archiveFile)
        }



    }
}