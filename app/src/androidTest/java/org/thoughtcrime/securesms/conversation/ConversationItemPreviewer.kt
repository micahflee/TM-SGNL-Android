package org.tm.archive.conversation

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.signal.core.util.ThreadUtil
import org.tm.archive.attachments.PointerAttachment
import org.tm.archive.database.SignalDatabase
import org.tm.archive.mms.IncomingMediaMessage
import org.tm.archive.mms.OutgoingMessage
import org.tm.archive.profiles.ProfileName
import org.tm.archive.recipients.Recipient
import org.tm.archive.releasechannel.ReleaseChannel
import org.tm.archive.testing.SignalActivityRule
import org.whispersystems.signalservice.api.messages.SignalServiceAttachmentPointer
import org.whispersystems.signalservice.api.messages.SignalServiceAttachmentRemoteId
import java.util.Optional

/**
 * Helper test for rendering conversation items for preview.
 */
@RunWith(AndroidJUnit4::class)
@Ignore("For testing/previewing manually, no assertions")
class ConversationItemPreviewer {

  @get:Rule
  val harness = SignalActivityRule(othersCount = 10)

  @Test
  fun testShowLongName() {
    val other: Recipient = Recipient.resolved(harness.others.first())

    SignalDatabase.recipients.setProfileName(other.id, ProfileName.fromParts("Seef", "$$$"))

    insertFailedMediaMessage(other = other, attachmentCount = 1)
    insertFailedMediaMessage(other = other, attachmentCount = 2)
    insertFailedMediaMessage(other = other, body = "Test", attachmentCount = 1)
//    insertFailedOutgoingMediaMessage(other = other, body = "Test", attachmentCount = 1)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)
//    insertMediaMessage(other = other)

    val scenario: ActivityScenario<ConversationActivity> = harness.launchActivity { putExtra("recipient_id", other.id.serialize()) }
    scenario.onActivity {
    }

    // Uncomment to make dialog stay on screen, otherwise will show/dismiss immediately
//    ThreadUtil.sleep(45000)
  }

  private fun insertMediaMessage(other: Recipient, body: String? = null, attachmentCount: Int = 1) {
    val attachments: List<SignalServiceAttachmentPointer> = (0 until attachmentCount).map {
      attachment()
    }

    val message = IncomingMediaMessage(
      from = other.id,
      body = body,
      sentTimeMillis = System.currentTimeMillis(),
      serverTimeMillis = System.currentTimeMillis(),
      receivedTimeMillis = System.currentTimeMillis(),
      attachments = PointerAttachment.forPointers(Optional.of(attachments))
    )

    SignalDatabase.messages.insertSecureDecryptedMessageInbox(message, SignalDatabase.threads.getOrCreateThreadIdFor(other)).get()

    ThreadUtil.sleep(1)
  }

  private fun insertFailedMediaMessage(other: Recipient, body: String? = null, attachmentCount: Int = 1) {
    val attachments: List<SignalServiceAttachmentPointer> = (0 until attachmentCount).map {
      attachment()
    }

    val message = IncomingMediaMessage(
      from = other.id,
      body = body,
      sentTimeMillis = System.currentTimeMillis(),
      serverTimeMillis = System.currentTimeMillis(),
      receivedTimeMillis = System.currentTimeMillis(),
      attachments = PointerAttachment.forPointers(Optional.of(attachments))
    )

    val insert = SignalDatabase.messages.insertSecureDecryptedMessageInbox(message, SignalDatabase.threads.getOrCreateThreadIdFor(other)).get()

    SignalDatabase.attachments.getAttachmentsForMessage(insert.messageId).forEachIndexed { index, attachment ->
//      if (index != 1) {
      SignalDatabase.attachments.setTransferProgressPermanentFailure(attachment.attachmentId, insert.messageId)
//      } else {
//        SignalDatabase.attachments.setTransferState(insert.messageId, attachment, TRANSFER_PROGRESS_STARTED)
//      }
    }

    ThreadUtil.sleep(1)
  }

  private fun insertFailedOutgoingMediaMessage(other: Recipient, body: String? = null, attachmentCount: Int = 1) {
    val attachments: List<SignalServiceAttachmentPointer> = (0 until attachmentCount).map {
      attachment()
    }

    val message = OutgoingMessage(
      recipient = other,
      body = body,
      attachments = PointerAttachment.forPointers(Optional.of(attachments)),
      timestamp = System.currentTimeMillis(),
      isSecure = true
    )

    val insert = SignalDatabase.messages.insertMessageOutbox(
      message,
      SignalDatabase.threads.getOrCreateThreadIdFor(other),
      false,
      null
    )

    SignalDatabase.attachments.getAttachmentsForMessage(insert).forEachIndexed { index, attachment ->
      SignalDatabase.attachments.setTransferProgressPermanentFailure(attachment.attachmentId, insert)
    }

    ThreadUtil.sleep(1)
  }

  private fun attachment(): SignalServiceAttachmentPointer {
    return SignalServiceAttachmentPointer(
      ReleaseChannel.CDN_NUMBER,
      SignalServiceAttachmentRemoteId.from(""),
      "image/webp",
      null,
      Optional.empty(),
      Optional.empty(),
      1024,
      1024,
      Optional.empty(),
      Optional.of("/not-there.jpg"),
      false,
      false,
      false,
      Optional.empty(),
      Optional.empty(),
      System.currentTimeMillis()
    )
  }
}
