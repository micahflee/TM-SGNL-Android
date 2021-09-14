package org.tm.archive.components.settings.app.chats

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.database.DatabaseFactory
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobs.MultiDeviceConfigurationUpdateJob
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.megaphone.Megaphones
import org.tm.archive.recipients.Recipient
import org.tm.archive.storage.StorageSyncHelper
import org.tm.archive.util.TextSecurePreferences

class ChatsSettingsRepository {

  private val context: Context = ApplicationDependencies.getApplication()

  fun syncLinkPreviewsState() {
    SignalExecutors.BOUNDED.execute {
      val isLinkPreviewsEnabled = SignalStore.settings().isLinkPreviewsEnabled

      DatabaseFactory.getRecipientDatabase(context).markNeedsSync(Recipient.self().id)
      StorageSyncHelper.scheduleSyncForDataChange()
      ApplicationDependencies.getJobManager().add(
        MultiDeviceConfigurationUpdateJob(
          TextSecurePreferences.isReadReceiptsEnabled(context),
          TextSecurePreferences.isTypingIndicatorsEnabled(context),
          TextSecurePreferences.isShowUnidentifiedDeliveryIndicatorsEnabled(context),
          isLinkPreviewsEnabled
        )
      )
      if (isLinkPreviewsEnabled) {
        ApplicationDependencies.getMegaphoneRepository().markFinished(Megaphones.Event.LINK_PREVIEWS)
      }
    }
  }
}
