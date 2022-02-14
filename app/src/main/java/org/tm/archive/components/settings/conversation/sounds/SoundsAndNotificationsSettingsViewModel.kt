package org.tm.archive.components.settings.conversation.sounds

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tm.archive.database.RecipientDatabase
import org.tm.archive.notifications.NotificationChannels
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.util.livedata.Store

class SoundsAndNotificationsSettingsViewModel(
  private val recipientId: RecipientId,
  private val repository: SoundsAndNotificationsSettingsRepository
) : ViewModel() {

  private val store = Store(SoundsAndNotificationsSettingsState())

  val state: LiveData<SoundsAndNotificationsSettingsState> = store.stateLiveData

  init {
    store.update(Recipient.live(recipientId).liveData) { recipient, state ->
      state.copy(
        recipientId = recipientId,
        muteUntil = if (recipient.isMuted) recipient.muteUntil else 0L,
        mentionSetting = recipient.mentionSetting,
        hasMentionsSupport = recipient.isPushV2Group,
        hasCustomNotificationSettings = recipient.notificationChannel != null || !NotificationChannels.supported()
      )
    }
  }

  fun setMuteUntil(muteUntil: Long) {
    repository.setMuteUntil(recipientId, muteUntil)
  }

  fun unmute() {
    repository.setMuteUntil(recipientId, 0L)
  }

  fun setMentionSetting(mentionSetting: RecipientDatabase.MentionSetting) {
    repository.setMentionSetting(recipientId, mentionSetting)
  }

  fun channelConsistencyCheck() {
    store.update { s -> s.copy(channelConsistencyCheckComplete = false) }
    repository.ensureCustomChannelConsistency {
      store.update { s -> s.copy(channelConsistencyCheckComplete = true) }
    }
  }

  class Factory(
    private val recipientId: RecipientId,
    private val repository: SoundsAndNotificationsSettingsRepository
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      return requireNotNull(modelClass.cast(SoundsAndNotificationsSettingsViewModel(recipientId, repository)))
    }
  }
}
