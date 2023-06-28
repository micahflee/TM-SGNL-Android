package org.tm.archive.migrations

import org.signal.core.util.logging.Log
import org.tm.archive.database.SignalDatabase
import org.tm.archive.jobmanager.Job

/**
 * Rebuilds the full-text search index for the messages table.
 */
internal class RebuildMessageSearchIndexMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(RebuildMessageSearchIndexMigrationJob::class.java)
    const val KEY = "RebuildMessageSearchIndexMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    val startTime = System.currentTimeMillis()
    SignalDatabase.messageSearch.rebuildIndex()
    Log.d(TAG, "It took ${System.currentTimeMillis() - startTime} ms to rebuild the search index.")
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<RebuildMessageSearchIndexMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): RebuildMessageSearchIndexMigrationJob {
      return RebuildMessageSearchIndexMigrationJob(parameters)
    }
  }
}
