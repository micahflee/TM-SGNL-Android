package org.archiver

import android.content.Context
import com.tm.androidcopysdk.DataGrabber
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_CHAT_GROUP
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_FROM_TEXT
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_TO_TEXT
import org.archiver.ArchiveConstants.Companion.SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX
import org.archiver.ArchiveConstants.Companion.isTestMode
import org.archiver.ArchiveConstants.Companion.signalTestMobileNumber
import org.archiver.ArchiveSender.Companion.archiveMessageOutbox
import org.archiver.ArchiveSender.Companion.archiveMessageOutboxMMS
import org.archiver.ArchiveSender.Companion.updateArchiveSDKToSendMMSMessage
import org.tm.archive.database.model.Mention
import org.tm.archive.groups.GroupId
import org.tm.archive.linkpreview.LinkPreview
import org.tm.archive.mms.IncomingMediaMessage
import org.tm.archive.mms.OutgoingExpirationUpdateMessage
import org.tm.archive.mms.OutgoingGroupUpdateMessage
import org.tm.archive.mms.OutgoingMediaMessage
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.sms.IncomingTextMessage
import java.io.File

class ArchiveUtil {

    companion object{

        fun createToRecipientList(context: Context, isInboxArchiveMessage: Boolean, aRecipient: Recipient, isGroup: Boolean, from: String, recipientList: MutableList<Recipient>? = null): Array<String> {
            var recipientListFromRecipient: List<String> = if (isGroup) {
                recipientList?.filter { it.e164.isPresent }?.map { it.e164.get()}
                        ?: aRecipient.participants.filter { it.e164.isPresent }.map { it.e164.get()}

            } else {
                if(isInboxArchiveMessage){
                    listOf(getPhoneNumberInTestMode(context))
                }else {
                    if(aRecipient.e164.isPresent) {
                        listOf(aRecipient.e164.get().toString())
                    }else{
                        listOf("")
                    }
                }
            }

            recipientListFromRecipient = if (!isInboxArchiveMessage) {
                if(recipientListFromRecipient.size > 1) {
                    recipientListFromRecipient.filter { it != getPhoneNumberInTestMode(context) }
                }else{
                    //Sending message in group that contains only me
                    recipientListFromRecipient
                }
            }else{
                recipientListFromRecipient.filter { it != from }
            }
            return recipientListFromRecipient.toTypedArray();
        }




        fun createSubjectForArchiving(context: Context, isInboxArchiveMessage: Boolean, isGroup: Boolean, recipient: Recipient, inboxRecipient: String = "", forceSms: Boolean, groupTitle: String? = "") : String{

            val archiveType: String = getArchiveType(isInboxArchiveMessage, isGroup, forceSms)
            val to = getToPartForSubject(context, isInboxArchiveMessage, recipient, isGroup, groupTitle)
            val from = getFromPartForSubject(context, isInboxArchiveMessage, recipient, inboxRecipient)

            return "$archiveType $ARCHIVE_SUBJECT_FROM_TEXT ${from.replace("+", "")} $ARCHIVE_SUBJECT_TO_TEXT ${to.replace("+", "")}"
        }

        private fun getToPartForSubject(context: Context, isInboxArchiveMessage: Boolean, recipient: Recipient, isGroup: Boolean, groupTitle: String?): String {
            return when {
                isGroup -> {
                    "$ARCHIVE_SUBJECT_CHAT_GROUP $groupTitle"
                }
                isInboxArchiveMessage -> {
                    getPhoneNumberInTestMode(context)
                }
                else -> {
                    if(recipient.e164.isPresent) {
                        recipient.e164.get()
                    }else{
                        ""
                    }
                }
            }
        }


        fun getFromPartForSubject(context: Context, isInboxArchiveMessage: Boolean, recipient: Recipient, inboxRecipient: String = ""): String {
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
                            ""
                        }
                    }
                }else -> {
                    getPhoneNumberInTestMode(context)
                }
            }
        }

        fun getArchiveType(isInboxArchiveMessage: Boolean, isGroupMessage: Boolean, forceSms: Boolean): String {

            return if(isInboxArchiveMessage || isGroupMessage){
                ArchiveConstants.ARCHIVE_TYPE_APP_MESSAGE
            }else{
                if(forceSms){
                    ArchiveConstants.ARCHIVE_TYPE_SMS
                }else{
                    ArchiveConstants.ARCHIVE_TYPE_APP_MESSAGE
                }
            }
        }

        fun getPhoneNumberInTestMode(context: Context) : String{
           return if(isTestMode){
               signalTestMobileNumber
            }else{
               PrefManager.getStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER, "");
            }
        }

        fun getChatMode(isGroup: Boolean) : DataGrabber.CHAT_MODE {
            return when {
                isGroup -> {
                    DataGrabber.CHAT_MODE.group
                }else -> {
                    DataGrabber.CHAT_MODE.chat
                }
            }
        }

        fun getChatName(context: Context, recipient: Recipient, isGroup: Boolean, groupTitle: String = ""): String {
            return if(isGroup){
                if(groupTitle.isNotEmpty()){
                    groupTitle
                }else {
                    recipient.getName(context).toString()
                }
            }else{
                ""
            }
        }

        fun getGroupInboxRecipientNumber(archiveRecipient: Recipient, message: IncomingTextMessage): String {
            val recipientList = archiveRecipient.participants.filter {
                message.sender.toLong() == it.id.toLong()
            }
            return recipientList[0].e164.get()
        }

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

        fun fromContactName(context: Context, recipient: Recipient, isInboxArchiveMessage: Boolean): String {
            return if(isInboxArchiveMessage){
                recipient.getDisplayName(context)
            }else{
                Recipient.self().profileName.toString()
            }

        }

        fun createMessageNameList(context: Context, recipient: Recipient, isInboxArchiveMessage: Boolean, recipientList: MutableList<Recipient>? = null, isGroup: Boolean, from : String = ""): Array<String> {

            val rl = if (!isInboxArchiveMessage) {
                if(recipientList!!.size > 1) {
                    recipientList!!.filter {
                        it.e164.isPresent && it.e164.get() != getPhoneNumberInTestMode(context)
                    } ?: recipient.participants.filter {
                                it.e164.isPresent && it.e164.get() != getPhoneNumberInTestMode(context)
                            }
                }else{
                    //Sending message in group that contains only me
                    recipientList
                }
            } else {
                recipientList?.filter {
                    it.e164.isPresent && it.e164.get() != null && it.e164.get() != from
                }
                        ?: recipient.participants.filter {
                            it.e164.isPresent && it.e164.get() != null && it.e164.get() != from
                        }
            }

            val recipientListFromRecipient: List<String> = if (isGroup) {

                rl.map {
                    it.getDisplayName(context)
                }

            } else {
                if(isInboxArchiveMessage){
                    listOf(Recipient.self().profileName.toString())
                }else {
                    listOf(recipient.getDisplayName(context))
                }
            }

            if(recipientListFromRecipient.toTypedArray().isEmpty()){
                return arrayOf("")
            }
            
            return recipientListFromRecipient.toTypedArray()
        }

        fun generateAttachmentName(messageId: Long, attachmentId: Long) : String{
            return SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX + attachmentId + "_" + messageId
        }


        fun getMessageBody(messageBody: String?, mentionsList: List<Mention>): String? {
            return if(messageBody != null) {
                var result = messageBody?: ""
                    return if (mentionsList.isNotEmpty()) {
                        mentionsList.forEachIndexed { index, mention ->
                            result = result.replaceFirst("\uFFFC", "\u0040" + getRecipientFromRecipientID(mentionsList[index].recipientId).profileName.givenName)
                        }
                        result
                    } else {
                        messageBody
                    }
            }else{
                null
            }
        }

        fun createPreviewLinkBody(incomingMediaMessage: IncomingMediaMessage?, outComingMediaMessage: OutgoingMediaMessage?) : String? {
            var body = ""
            if(incomingMediaMessage != null){
                return if(incomingMediaMessage.linkPreviews.isEmpty()){
                    getMessageBody(incomingMediaMessage.body, incomingMediaMessage.mentions)
                }else{
                    generateBodyFromLinkPreview(incomingMediaMessage.linkPreviews[0]) + "\n" + incomingMediaMessage.body
                }
            }else if(outComingMediaMessage != null){
                return if(outComingMediaMessage.linkPreviews.isEmpty()){
                    getMessageBody(outComingMediaMessage.body, outComingMediaMessage.mentions)
                }else{
                    generateBodyFromLinkPreview(outComingMediaMessage.linkPreviews[0]) + "\n" + outComingMediaMessage.body
                }
            }
            return ""
        }

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

        fun cleanMessageBodyFromUnusedCharacters(messageBody: String?): String {
            return if(messageBody != null && messageBody.isNotEmpty()) {
                messageBody.replace("\u2069", "").replace("\u2068", "")
            }else{
                ""
            }
        }

        fun getRecipientFromRecipientID(recipientId: RecipientId) : Recipient{
            return Recipient.resolved(recipientId)
        }


        fun archiveMediaMessage(context: Context, messageId: Long, message: OutgoingMediaMessage) {
            var tempFileForArchiving: File? = null
            var isMediaMessage = false
            var filesToSend = arrayOfNulls<File>(message.attachments.size)
            for (i in message.attachments.indices) {
                tempFileForArchiving = ArchiveFileUtil.getFileFromDataBaseUri(context, message.attachments[i].uri.toString())
                filesToSend[i] = tempFileForArchiving
                isMediaMessage = true
            }
            if (message.linkPreviews.isNotEmpty()) {
                if (message.linkPreviews[0].thumbnail.isPresent && message.linkPreviews[0].thumbnail.get().uri.toString().isNotEmpty()) {
                    filesToSend = arrayOfNulls(1)
                    filesToSend[0] = ArchiveFileUtil.createFileFromContentUri(context, message.linkPreviews[0].thumbnail.get().uri.toString())
                    isMediaMessage = true
                }else{
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
                archiveMessageOutboxMMS(context, ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_SEND, message.recipient, message, messageId, null)
                isMediaMessage = true
            }

            if(isMediaMessage) {
                archiveMessageOutboxMMS(context, ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_SEND, message.recipient, message, messageId, filesToSend)
                for (i in filesToSend.indices) {
                    updateArchiveSDKToSendMMSMessage(context, filesToSend[i]!!.name, true)
                }
            }else{
                if (message !is OutgoingGroupUpdateMessage
                        && message !is OutgoingExpirationUpdateMessage) {

                    archiveMessageOutbox(context, ArchiveConstants.ProtocolType.ARCHIVE_PARAM_PROTOCOL_SEND, message.recipient, message.body, messageId)
                }else{
                    //TODO - Group events/updates!!
                }

            }
        }

    }

    enum class InboxArchiveTypes{
        MEDIA,
        HYPER_LINK,
        STICKER,
        CONTACT,
        MENTIONS;
    }
}