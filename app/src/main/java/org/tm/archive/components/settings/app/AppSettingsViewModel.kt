package org.tm.archive.components.settings.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.tm.archive.conversationlist.model.UnreadPaymentsLiveData
import org.tm.archive.recipients.Recipient
import org.tm.archive.util.livedata.LiveDataUtil

class AppSettingsViewModel : ViewModel() {

  val unreadPaymentsLiveData = UnreadPaymentsLiveData()
  val selfLiveData: LiveData<Recipient> = Recipient.self().live().liveData

  val state: LiveData<AppSettingsState> = LiveDataUtil.combineLatest(unreadPaymentsLiveData, selfLiveData) { payments, self ->
    val unreadPaymentsCount = payments.transform { it.unreadCount }.or(0)

    AppSettingsState(self, unreadPaymentsCount)
  }
}
