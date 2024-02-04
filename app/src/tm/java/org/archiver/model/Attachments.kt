package org.archiver.model

import com.tm.androidcopysdk.Models.MessageAttachmentStatus
import org.tm.archive.database.AttachmentTable
import org.tm.archive.mms.MmsSlide
import org.tm.archive.mms.Slide

object Attachments {

  fun Slide.status(): MessageAttachmentStatus {
    return when (transferState) {
      AttachmentTable.TRANSFER_PROGRESS_FAILED, AttachmentTable.TRANSFER_PROGRESS_PERMANENT_FAILURE -> MessageAttachmentStatus.Failed
      AttachmentTable.TRANSFER_PROGRESS_PENDING, AttachmentTable.TRANSFER_PROGRESS_STARTED -> MessageAttachmentStatus.Loading
      AttachmentTable.TRANSFER_PROGRESS_DONE -> MessageAttachmentStatus.Success
      else -> MessageAttachmentStatus.Failed
    }
  }


}