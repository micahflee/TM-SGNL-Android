package org.tm.archive.components.settings.app.data

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.database.DatabaseFactory
import org.tm.archive.dependencies.ApplicationDependencies

class DataAndStorageSettingsRepository {

  private val context: Context = ApplicationDependencies.getApplication()

  fun getTotalStorageUse(consumer: (Long) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val breakdown = DatabaseFactory.getMediaDatabase(context).storageBreakdown

      consumer(listOf(breakdown.audioSize, breakdown.documentSize, breakdown.photoSize, breakdown.videoSize).sum())
    }
  }
}
