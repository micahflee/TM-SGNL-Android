package org.tm.archive.migrations

import org.signal.core.util.logging.Log
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobs.OptimizeMessageSearchIndexJob

/**
 * Kicks off a job to optimize the message search index.
 */
internal class OptimizeMessageSearchIndexMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(OptimizeMessageSearchIndexMigrationJob::class.java)
    const val KEY = "OptimizeMessageSearchIndexMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    OptimizeMessageSearchIndexJob.enqueue()
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<OptimizeMessageSearchIndexMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): OptimizeMessageSearchIndexMigrationJob {
      return OptimizeMessageSearchIndexMigrationJob(parameters)
    }
  }
}
