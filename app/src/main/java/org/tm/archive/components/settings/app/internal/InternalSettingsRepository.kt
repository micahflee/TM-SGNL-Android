package org.tm.archive.components.settings.app.internal

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.database.MessageDatabase
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.addStyle
import org.tm.archive.database.model.databaseprotos.BodyRangeList
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.emoji.EmojiFiles
import org.tm.archive.jobs.AttachmentDownloadJob
import org.tm.archive.jobs.CreateReleaseChannelJob
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.notifications.v2.ConversationId
import org.tm.archive.recipients.Recipient
import org.tm.archive.releasechannel.ReleaseChannel

class InternalSettingsRepository(context: Context) {

  private val context = context.applicationContext

  fun getEmojiVersionInfo(consumer: (EmojiFiles.Version?) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      consumer(EmojiFiles.Version.readVersion(context))
    }
  }

  fun addSampleReleaseNote() {
    SignalExecutors.UNBOUNDED.execute {
      ApplicationDependencies.getJobManager().runSynchronously(CreateReleaseChannelJob.create(), 5000)

      val title = "Release Note Title"
      val bodyText = "Release note body. Aren't I awesome?"
      val body = "$title\n\n$bodyText"
      val bodyRangeList = BodyRangeList.newBuilder()
        .addStyle(BodyRangeList.BodyRange.Style.BOLD, 0, title.length)

      val recipientId = SignalStore.releaseChannelValues().releaseChannelRecipientId!!
      val threadId = SignalDatabase.threads.getOrCreateThreadIdFor(Recipient.resolved(recipientId))

      val insertResult: MessageDatabase.InsertResult? = ReleaseChannel.insertReleaseChannelMessage(
        recipientId = recipientId,
        body = body,
        threadId = threadId,
        messageRanges = bodyRangeList.build(),
        image = "/static/release-notes/signal.png",
        imageWidth = 1800,
        imageHeight = 720
      )

      SignalDatabase.sms.insertBoostRequestMessage(recipientId, threadId)

      if (insertResult != null) {
        SignalDatabase.attachments.getAttachmentsForMessage(insertResult.messageId)
          .forEach { ApplicationDependencies.getJobManager().add(AttachmentDownloadJob(insertResult.messageId, it.attachmentId, false)) }

        ApplicationDependencies.getMessageNotifier().updateNotification(context, ConversationId.forConversation(insertResult.threadId))
      }
    }
  }
}
