package org.tm.archive.components.settings.app.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobs.MultiDeviceContactUpdateJob
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.storage.StorageSyncHelper
import org.tm.archive.util.BackupUtil
import org.tm.archive.util.ConversationUtil
import org.tm.archive.util.ThrottledDebouncer
import org.tm.archive.util.livedata.Store

class ChatsSettingsViewModel(private val repository: ChatsSettingsRepository) : ViewModel() {

  private val refreshDebouncer = ThrottledDebouncer(500L)

  private val store: Store<ChatsSettingsState> = Store(
    ChatsSettingsState(
      generateLinkPreviews = SignalStore.settings().isLinkPreviewsEnabled,
      useAddressBook = SignalStore.settings().isPreferSystemContactPhotos,
      useSystemEmoji = SignalStore.settings().isPreferSystemEmoji,
      enterKeySends = SignalStore.settings().isEnterKeySends,
      chatBackupsEnabled = SignalStore.settings().isBackupEnabled && BackupUtil.canUserAccessBackupDirectory(ApplicationDependencies.getApplication())
    )
  )

  val state: LiveData<ChatsSettingsState> = store.stateLiveData

  fun setGenerateLinkPreviewsEnabled(enabled: Boolean) {
    store.update { it.copy(generateLinkPreviews = enabled) }
    SignalStore.settings().isLinkPreviewsEnabled = enabled
    repository.syncLinkPreviewsState()
  }

  fun setUseAddressBook(enabled: Boolean) {
    store.update { it.copy(useAddressBook = enabled) }
    SignalStore.settings().isPreferSystemContactPhotos = enabled
    refreshDebouncer.publish { ConversationUtil.refreshRecipientShortcuts() }
    ApplicationDependencies.getJobManager().add(MultiDeviceContactUpdateJob(true))
    StorageSyncHelper.scheduleSyncForDataChange()
  }

  fun setUseSystemEmoji(enabled: Boolean) {
    store.update { it.copy(useSystemEmoji = enabled) }
    SignalStore.settings().isPreferSystemEmoji = enabled
  }

  fun setEnterKeySends(enabled: Boolean) {
    store.update { it.copy(enterKeySends = enabled) }
    SignalStore.settings().isEnterKeySends = enabled
  }

  class Factory(private val repository: ChatsSettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      return requireNotNull(modelClass.cast(ChatsSettingsViewModel(repository)))
    }
  }
}
