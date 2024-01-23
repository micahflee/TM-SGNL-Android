package org.tm.archive.jobmanager.migrations

import org.tm.archive.jobmanager.JobMigration

/**
 * Migrate DonationReceiptRedemptionJob to use more lax lifespan and retries to accommodate SEPA.
 */
class DonationReceiptRedemptionJobMigration : JobMigration(11) {
  override fun migrate(jobData: JobData): JobData {
    return if ("DonationReceiptRedemptionJob" == jobData.factoryKey) {
      jobData.copy(
        maxAttempts = 1500,
        lifespan = -1
      )
    } else {
      jobData
    }
  }
}
