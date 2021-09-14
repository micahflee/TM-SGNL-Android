package org.tm.archive.components.settings.conversation.sounds

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.database.DatabaseFactory
import org.tm.archive.database.RecipientDatabase
import org.tm.archive.notifications.NotificationChannels
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId

class SoundsAndNotificationsSettingsRepository(private val context: Context) {

  fun setMuteUntil(recipientId: RecipientId, muteUntil: Long) {
    SignalExecutors.BOUNDED.execute {
      DatabaseFactory.getRecipientDatabase(context).setMuted(recipientId, muteUntil)
    }
  }

  fun setMentionSetting(recipientId: RecipientId, mentionSetting: RecipientDatabase.MentionSetting) {
    SignalExecutors.BOUNDED.execute {
      DatabaseFactory.getRecipientDatabase(context).setMentionSetting(recipientId, mentionSetting)
    }
  }

  fun hasCustomNotificationSettings(recipientId: RecipientId, consumer: (Boolean) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val recipient = Recipient.resolved(recipientId)
      consumer(
        if (recipient.notificationChannel != null || !NotificationChannels.supported()) {
          true
        } else {
          NotificationChannels.updateWithShortcutBasedChannel(context, recipient)
        }
      )
    }
  }
}
