package org.tm.archive.components.settings.app.subscription.manage

import androidx.annotation.WorkerThread
import io.reactivex.rxjava3.core.Observable
import org.tm.archive.components.settings.app.subscription.DonationSerializationHelper
import org.tm.archive.components.settings.app.subscription.donate.stripe.Stripe3DSData
import org.tm.archive.database.model.databaseprotos.PendingOneTimeDonation
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.persistence.JobSpec
import org.tm.archive.jobs.BoostReceiptRequestResponseJob
import org.tm.archive.jobs.DonationReceiptRedemptionJob
import org.tm.archive.jobs.ExternalLaunchDonationJob
import org.tm.archive.jobs.SubscriptionReceiptRequestResponseJob
import org.tm.archive.keyvalue.SignalStore
import java.util.concurrent.TimeUnit

/**
 * Allows observer to poll for the status of the latest pending, running, or completed redemption job for subscriptions or one time payments.
 */
object DonationRedemptionJobWatcher {

  enum class RedemptionType {
    SUBSCRIPTION,
    ONE_TIME
  }

  @JvmStatic
  @WorkerThread
  fun hasPendingRedemptionJob(): Boolean {
    return getDonationRedemptionJobStatus(RedemptionType.SUBSCRIPTION).isInProgress() || getDonationRedemptionJobStatus(RedemptionType.ONE_TIME).isInProgress()
  }

  fun watchSubscriptionRedemption(): Observable<DonationRedemptionJobStatus> = watch(RedemptionType.SUBSCRIPTION)

  @JvmStatic
  @WorkerThread
  fun getSubscriptionRedemptionJobStatus(): DonationRedemptionJobStatus {
    return getDonationRedemptionJobStatus(RedemptionType.SUBSCRIPTION)
  }

  fun watchOneTimeRedemption(): Observable<DonationRedemptionJobStatus> = watch(RedemptionType.ONE_TIME)

  private fun watch(redemptionType: RedemptionType): Observable<DonationRedemptionJobStatus> {
    return Observable
      .interval(0, 5, TimeUnit.SECONDS)
      .map {
        getDonationRedemptionJobStatus(redemptionType)
      }
      .distinctUntilChanged()
  }

  private fun getDonationRedemptionJobStatus(redemptionType: RedemptionType): DonationRedemptionJobStatus {
    val queue = when (redemptionType) {
      RedemptionType.SUBSCRIPTION -> DonationReceiptRedemptionJob.SUBSCRIPTION_QUEUE
      RedemptionType.ONE_TIME -> DonationReceiptRedemptionJob.ONE_TIME_QUEUE
    }

    val donationJobSpecs = ApplicationDependencies
      .getJobManager()
      .find { it.queueKey?.startsWith(queue) == true }
      .sortedBy { it.createTime }

    val externalLaunchJobSpec: JobSpec? = donationJobSpecs.firstOrNull {
      it.factoryKey == ExternalLaunchDonationJob.KEY
    }

    val receiptRequestJobKey = when (redemptionType) {
      RedemptionType.SUBSCRIPTION -> SubscriptionReceiptRequestResponseJob.KEY
      RedemptionType.ONE_TIME -> BoostReceiptRequestResponseJob.KEY
    }

    val receiptJobSpec: JobSpec? = donationJobSpecs.firstOrNull {
      it.factoryKey == receiptRequestJobKey
    }

    val redemptionJobSpec: JobSpec? = donationJobSpecs.firstOrNull {
      it.factoryKey == DonationReceiptRedemptionJob.KEY
    }

    val jobSpec: JobSpec? = externalLaunchJobSpec ?: redemptionJobSpec ?: receiptJobSpec

    return if (redemptionType == RedemptionType.SUBSCRIPTION && jobSpec == null && SignalStore.donationsValues().getSubscriptionRedemptionFailed()) {
      DonationRedemptionJobStatus.FailedSubscription
    } else {
      jobSpec?.toDonationRedemptionStatus(redemptionType) ?: DonationRedemptionJobStatus.None
    }
  }

  private fun JobSpec.toDonationRedemptionStatus(redemptionType: RedemptionType): DonationRedemptionJobStatus {
    return when (factoryKey) {
      ExternalLaunchDonationJob.KEY -> {
        val stripe3DSData = ExternalLaunchDonationJob.Factory.parseSerializedData(serializedData!!)
        DonationRedemptionJobStatus.PendingExternalVerification(
          pendingOneTimeDonation = pendingOneTimeDonation(redemptionType, stripe3DSData),
          nonVerifiedMonthlyDonation = nonVerifiedMonthlyDonation(redemptionType, stripe3DSData)
        )
      }

      SubscriptionReceiptRequestResponseJob.KEY,
      BoostReceiptRequestResponseJob.KEY -> DonationRedemptionJobStatus.PendingReceiptRequest

      DonationReceiptRedemptionJob.KEY -> DonationRedemptionJobStatus.PendingReceiptRedemption

      else -> {
        DonationRedemptionJobStatus.None
      }
    }
  }

  private fun JobSpec.pendingOneTimeDonation(redemptionType: RedemptionType, stripe3DSData: Stripe3DSData): PendingOneTimeDonation? {
    if (redemptionType != RedemptionType.ONE_TIME) {
      return null
    }

    return DonationSerializationHelper.createPendingOneTimeDonationProto(
      badge = stripe3DSData.gatewayRequest.badge,
      paymentSourceType = stripe3DSData.paymentSourceType,
      amount = stripe3DSData.gatewayRequest.fiat
    ).copy(
      timestamp = createTime,
      pendingVerification = true,
      checkedVerification = runAttempt > 0
    )
  }

  private fun JobSpec.nonVerifiedMonthlyDonation(redemptionType: RedemptionType, stripe3DSData: Stripe3DSData): NonVerifiedMonthlyDonation? {
    if (redemptionType != RedemptionType.SUBSCRIPTION) {
      return null
    }

    return NonVerifiedMonthlyDonation(
      timestamp = createTime,
      price = stripe3DSData.gatewayRequest.fiat,
      level = stripe3DSData.gatewayRequest.level.toInt(),
      checkedVerification = runAttempt > 0
    )
  }
}
