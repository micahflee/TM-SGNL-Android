package org.tm.archive.components.settings.conversation.sounds

import org.tm.archive.database.RecipientDatabase
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId

data class SoundsAndNotificationsSettingsState(
  val recipientId: RecipientId = Recipient.UNKNOWN.id,
  val muteUntil: Long = 0L,
  val mentionSetting: RecipientDatabase.MentionSetting = RecipientDatabase.MentionSetting.DO_NOT_NOTIFY,
  val hasCustomNotificationSettings: Boolean = false,
  val hasMentionsSupport: Boolean = false
)
