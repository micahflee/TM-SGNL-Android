package org.archiver.data

import android.content.Context
import com.tm.androidcopysdk.api.IMessageStoreObserver
import org.archiver.di.TeleMessageApplicationDependencyProvider
import org.tm.archive.attachments.Attachment
import org.tm.archive.attachments.AttachmentId
import org.tm.archive.attachments.DatabaseAttachment
import org.tm.archive.crypto.AttachmentSecret
import org.tm.archive.database.AttachmentTable
import org.tm.archive.database.SignalDatabase
import java.io.InputStream

class TeleAttachmentTable(

  context: Context,

  databaseHelper: SignalDatabase,

  attachmentSecret: AttachmentSecret,

) : AttachmentTable(context, databaseHelper, attachmentSecret) {

  private val messageStoreObserver: IMessageStoreObserver<Long> = TeleMessageApplicationDependencyProvider.messageStoreObserver

  override fun insertAttachmentForPreUpload(attachment: Attachment): DatabaseAttachment {
    val result = super.insertAttachmentForPreUpload(attachment)
    messageStoreObserver.afterMessageIdStateChanged(result.mmsId)
    return result
  }

  /*override fun insertAttachmentsForPlaceholder(mmsId: Long, attachmentId: AttachmentId, inputStream: InputStream) {
    super.insertAttachmentsForPlaceholder(mmsId, attachmentId, inputStream)
    messageStoreObserver.afterMessageIdStateChanged(mmsId)
  }*/

  override fun insertAttachmentsForMessage(mmsId: Long, attachments: List<Attachment>, quoteAttachment: List<Attachment>): Map<Attachment, AttachmentId> {
    val result = super.insertAttachmentsForMessage(mmsId, attachments, quoteAttachment)
    messageStoreObserver.afterMessageIdStateChanged(mmsId)
    return result
  }

  override fun markAttachmentAsTransformed(attachmentId: AttachmentId, withFastStart: Boolean) {
    super.markAttachmentAsTransformed(attachmentId, withFastStart)
  }

  override fun markAttachmentUploaded(messageId: Long, attachment: Attachment) {
    super.markAttachmentUploaded(messageId, attachment)
    messageStoreObserver.afterMessageIdStateChanged(messageId)
  }
}