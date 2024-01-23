package org.tm.archive.migrations

import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobs.MultiDeviceKeysUpdateJob
import org.tm.archive.util.TextSecurePreferences

/**
 * Migration to sync keys with linked devices.
 */
internal class SyncKeysMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    const val KEY = "SyncKeysMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    if (TextSecurePreferences.isMultiDevice(context)) {
      ApplicationDependencies.getJobManager().add(MultiDeviceKeysUpdateJob())
    }
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<SyncKeysMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): SyncKeysMigrationJob {
      return SyncKeysMigrationJob(parameters)
    }
  }
}
