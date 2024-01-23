package org.tm.archive.migrations

import org.signal.core.util.logging.Log
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobs.PaymentLedgerUpdateJob
import org.tm.archive.jobs.PaymentTransactionCheckJob

/**
 * Migration to recheck incoming payments that may have been missed due to db race.
 */
internal class RecheckPaymentsMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    const val KEY = "RecheckPaymentsMigrationJob"

    val TAG = Log.tag(RecheckPaymentsMigrationJob::class.java)
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  @Suppress("UsePropertyAccessSyntax")
  override fun performMigration() {
    val jobs: MutableList<Job> = SignalDatabase
      .payments
      .getSubmittedIncomingPayments()
      .filterNotNull()
      .map { PaymentTransactionCheckJob(it) }
      .toMutableList()

    Log.i(TAG, "Rechecking ${jobs.size} payments")
    if (jobs.isNotEmpty()) {
      jobs += PaymentLedgerUpdateJob.updateLedger()
    }
    ApplicationDependencies.getJobManager().addAll(jobs)
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<RecheckPaymentsMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): RecheckPaymentsMigrationJob {
      return RecheckPaymentsMigrationJob(parameters)
    }
  }
}
