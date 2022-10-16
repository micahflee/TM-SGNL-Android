package org.tm.archive.components.settings.app.subscription.manage

import io.reactivex.rxjava3.core.Observable
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.JobTracker
import org.tm.archive.jobs.DonationReceiptRedemptionJob
import org.tm.archive.jobs.SubscriptionReceiptRequestResponseJob
import org.tm.archive.keyvalue.SignalStore
import java.util.Optional
import java.util.concurrent.TimeUnit

/**
 * Allows observer to poll for the status of the latest pending, running, or completed redemption job for subscriptions.
 */
object SubscriptionRedemptionJobWatcher {
  fun watch(): Observable<Optional<JobTracker.JobState>> = Observable.interval(0, 5, TimeUnit.SECONDS).map {
    val redemptionJobState: JobTracker.JobState? = ApplicationDependencies.getJobManager().getFirstMatchingJobState {
      it.factoryKey == DonationReceiptRedemptionJob.KEY && it.parameters.queue == DonationReceiptRedemptionJob.SUBSCRIPTION_QUEUE
    }

    val receiptJobState: JobTracker.JobState? = ApplicationDependencies.getJobManager().getFirstMatchingJobState {
      it.factoryKey == SubscriptionReceiptRequestResponseJob.KEY && it.parameters.queue == DonationReceiptRedemptionJob.SUBSCRIPTION_QUEUE
    }

    val jobState: JobTracker.JobState? = redemptionJobState ?: receiptJobState

    if (jobState == null && SignalStore.donationsValues().getSubscriptionRedemptionFailed()) {
      Optional.of(JobTracker.JobState.FAILURE)
    } else {
      Optional.ofNullable(jobState)
    }
  }.distinctUntilChanged()
}
