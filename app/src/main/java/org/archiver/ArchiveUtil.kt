package org.archiver

import android.content.Context
import com.klinker.android.send_message.Utils
import com.tm.androidcopysdk.DataGrabber
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_CHAT_GROUP
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_FROM_TEXT
import org.archiver.ArchiveConstants.Companion.ARCHIVE_SUBJECT_TO_TEXT
import org.archiver.ArchiveConstants.Companion.SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX
import org.archiver.ArchiveConstants.Companion.isTestMode
import org.archiver.ArchiveConstants.Companion.signalTestMobileNumber
import org.signal.glide.Log
import org.thoughtcrime.securesms.recipients.Recipient
import org.thoughtcrime.securesms.sms.IncomingTextMessage
import org.thoughtcrime.securesms.util.Util
import java.util.*

class ArchiveUtil {

    companion object{

        fun createToRecipientList(context: Context, isInboxArchiveMessage: Boolean, aRecipient: Recipient, from: String): Array<String> {
            var recipientListFromRecipient: List<String> = if (aRecipient.isGroup) {
                aRecipient.participants.filter { it.e164.isPresent }.map { it.e164.get()}
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
                recipientListFromRecipient.filter { it != getPhoneNumberInTestMode(context) }
            }else{
                recipientListFromRecipient.filter { it != from }
            }
            return recipientListFromRecipient.toTypedArray();
        }




        fun createSubjectForArchiving(context: Context, isInboxArchiveMessage: Boolean, isGroup: Boolean, recipient: Recipient, inboxRecipient : String = "",forceSms : Boolean) : String{

            val archiveType: String = getArchiveType(isInboxArchiveMessage, isGroup, forceSms)
            val to = getToPartForSubject(context, isInboxArchiveMessage, recipient, isGroup)
            val from = getFromPartForSubject(context, isInboxArchiveMessage, recipient, inboxRecipient)
            return "$archiveType $ARCHIVE_SUBJECT_FROM_TEXT $from $ARCHIVE_SUBJECT_TO_TEXT $to"

        }

        private fun getToPartForSubject(context: Context, isInboxArchiveMessage: Boolean, recipient: Recipient, isGroup: Boolean): String {
            return when {
                isGroup -> {
                    "$ARCHIVE_SUBJECT_CHAT_GROUP ${recipient.getName(context)}"
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


        fun getFromPartForSubject(context: Context, isInboxArchiveMessage: Boolean, recipient: Recipient, inboxRecipient : String = ""): String {
            return when {
                isInboxArchiveMessage -> {
                    if(recipient.isGroup){
                        inboxRecipient
                    }else {
                        if(recipient.e164.isPresent) {
                            recipient.e164.get()
                        }else{
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

        fun getChatName(context: Context, recipient: Recipient): String {
            return if(recipient.isGroup){
                recipient.getName(context).toString()
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

        fun groupId(recipient: Recipient): String? {
            return if(recipient.isGroup){
               // recipient.groupId.get().toString()
                UUID.randomUUID().toString()
            }else{
                null
            }
        }

        fun fromContactName(context: Context,recipient: Recipient, isInboxArchiveMessage: Boolean ): String {
            return if(isInboxArchiveMessage){
                recipient.getDisplayName(context)
            }else{
                Recipient.self().profileName.toString()
            }

        }

        fun createMessageNameList(context: Context, recipient: Recipient, isInboxArchiveMessage: Boolean, from: String): Array<String> {

            val rl  = if (!isInboxArchiveMessage) {
                recipient.participants.filter {
                    it.e164.isPresent && it.e164.get() != getPhoneNumberInTestMode(context)
                }
            }else{
                recipient.participants.filter {
                    it.e164.isPresent &&  it.e164.get() != from
                }
            }

            val recipientListFromRecipient: List<String> = if (recipient.isGroup) {

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

            return recipientListFromRecipient.toTypedArray()
        }

        fun generateAttachmentName(attachmentId: Long, messageId: Long) : String{
            return SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX + attachmentId + "_" + messageId
        }

    }
}