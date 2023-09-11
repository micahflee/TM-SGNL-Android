package org.tm.archive.jobs

import org.signal.core.util.logging.Log
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobmanager.impl.DataRestoreConstraint
import org.tm.archive.transport.RetryLaterException
import java.lang.Exception
import java.lang.IllegalStateException
import kotlin.time.Duration.Companion.seconds

class RebuildMessageSearchIndexJob private constructor(params: Parameters) : BaseJob(params) {

  companion object {
    private val TAG = Log.tag(RebuildMessageSearchIndexJob::class.java)

    const val KEY = "RebuildMessageSearchIndexJob"

    fun enqueue() {
      ApplicationDependencies.getJobManager().add(RebuildMessageSearchIndexJob())
    }
  }

  private constructor() : this(
    Parameters.Builder()
      .setQueue("RebuildMessageSearchIndex")
      .addConstraint(DataRestoreConstraint.KEY)
      .setMaxAttempts(3)
      .build()
  )

  override fun serialize(): ByteArray? = null

  override fun getFactoryKey(): String = KEY

  override fun onFailure() = Unit

  override fun onRun() {
    try {
      SignalDatabase.messageSearch.rebuildIndex()
    } catch (e: IllegalStateException) {
      throw RetryLaterException(e)
    }
  }

  override fun getNextRunAttemptBackoff(pastAttemptCount: Int, exception: Exception): Long {
    return 10.seconds.inWholeMilliseconds
  }

  override fun onShouldRetry(e: Exception): Boolean = e is RetryLaterException

  class Factory : Job.Factory<RebuildMessageSearchIndexJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): RebuildMessageSearchIndexJob {
      return RebuildMessageSearchIndexJob(parameters)
    }
  }
}
