package org.tm.archive.components.settings.app.chats.sms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.util.Util
import org.tm.archive.util.livedata.Store

class SmsSettingsViewModel : ViewModel() {

  private val store = Store(
    SmsSettingsState(
      useAsDefaultSmsApp = Util.isDefaultSmsProvider(ApplicationDependencies.getApplication()),
      smsDeliveryReportsEnabled = SignalStore.settings().isSmsDeliveryReportsEnabled,
      wifiCallingCompatibilityEnabled = SignalStore.settings().isWifiCallingCompatibilityModeEnabled
    )
  )

  val state: LiveData<SmsSettingsState> = store.stateLiveData

  fun setSmsDeliveryReportsEnabled(enabled: Boolean) {
    store.update { it.copy(smsDeliveryReportsEnabled = enabled) }
    SignalStore.settings().isSmsDeliveryReportsEnabled = enabled
  }

  fun setWifiCallingCompatibilityEnabled(enabled: Boolean) {
    store.update { it.copy(wifiCallingCompatibilityEnabled = enabled) }
    SignalStore.settings().isWifiCallingCompatibilityModeEnabled = enabled
  }

  fun checkSmsEnabled() {
    store.update { it.copy(useAsDefaultSmsApp = Util.isDefaultSmsProvider(ApplicationDependencies.getApplication())) }
  }
}
