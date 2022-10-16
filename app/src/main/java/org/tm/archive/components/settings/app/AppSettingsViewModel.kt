package org.tm.archive.components.settings.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.tm.archive.conversationlist.model.UnreadPaymentsLiveData
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.util.livedata.Store

class AppSettingsViewModel : ViewModel() {

  private val store = Store(AppSettingsState(Recipient.self(), 0, SignalStore.donationsValues().getExpiredGiftBadge() != null))

  private val unreadPaymentsLiveData = UnreadPaymentsLiveData()
  private val selfLiveData: LiveData<Recipient> = Recipient.self().live().liveData

  val state: LiveData<AppSettingsState> = store.stateLiveData

  init {
    store.update(unreadPaymentsLiveData) { payments, state -> state.copy(unreadPaymentsCount = payments.map { it.unreadCount }.orElse(0)) }
    store.update(selfLiveData) { self, state -> state.copy(self = self) }
  }

  fun refreshExpiredGiftBadge() {
    store.update { it.copy(hasExpiredGiftBadge = SignalStore.donationsValues().getExpiredGiftBadge() != null) }
  }
}
