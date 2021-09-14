package org.tm.archive.components.settings.app.privacy

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.database.DatabaseFactory
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobs.MultiDeviceConfigurationUpdateJob
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.storage.StorageSyncHelper
import org.tm.archive.util.TextSecurePreferences

class PrivacySettingsRepository {

  private val context: Context = ApplicationDependencies.getApplication()

  fun getBlockedCount(consumer: (Int) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val recipientDatabase = DatabaseFactory.getRecipientDatabase(context)

      consumer(recipientDatabase.blocked.count)
    }
  }

  fun syncReadReceiptState() {
    SignalExecutors.BOUNDED.execute {
      DatabaseFactory.getRecipientDatabase(context).markNeedsSync(Recipient.self().id)
      StorageSyncHelper.scheduleSyncForDataChange()
      ApplicationDependencies.getJobManager().add(
        MultiDeviceConfigurationUpdateJob(
          TextSecurePreferences.isReadReceiptsEnabled(context),
          TextSecurePreferences.isTypingIndicatorsEnabled(context),
          TextSecurePreferences.isShowUnidentifiedDeliveryIndicatorsEnabled(context),
          SignalStore.settings().isLinkPreviewsEnabled
        )
      )
    }
  }

  fun syncTypingIndicatorsState() {
    val enabled = TextSecurePreferences.isTypingIndicatorsEnabled(context)

    DatabaseFactory.getRecipientDatabase(context).markNeedsSync(Recipient.self().id)
    StorageSyncHelper.scheduleSyncForDataChange()
    ApplicationDependencies.getJobManager().add(
      MultiDeviceConfigurationUpdateJob(
        TextSecurePreferences.isReadReceiptsEnabled(context),
        enabled,
        TextSecurePreferences.isShowUnidentifiedDeliveryIndicatorsEnabled(context),
        SignalStore.settings().isLinkPreviewsEnabled
      )
    )

    if (!enabled) {
      ApplicationDependencies.getTypingStatusRepository().clear()
    }
  }
}
