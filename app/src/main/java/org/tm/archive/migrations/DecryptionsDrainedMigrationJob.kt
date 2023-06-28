package org.tm.archive.migrations

import org.signal.core.util.logging.Log
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobs.PushDecryptDrainedJob

/**
 * Kicks off a job to notify the [org.tm.archive.messages.IncomingMessageObserver] when the decryption queue is empty.
 */
internal class DecryptionsDrainedMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(DecryptionsDrainedMigrationJob::class.java)
    const val KEY = "DecryptionsDrainedMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    ApplicationDependencies.getJobManager().add(PushDecryptDrainedJob())
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<DecryptionsDrainedMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): DecryptionsDrainedMigrationJob {
      return DecryptionsDrainedMigrationJob(parameters)
    }
  }
}
