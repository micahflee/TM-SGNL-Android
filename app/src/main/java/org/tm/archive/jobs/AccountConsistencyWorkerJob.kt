package org.tm.archive.jobs

import org.signal.core.util.logging.Log
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobmanager.impl.NetworkConstraint
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.util.Base64
import org.tm.archive.util.ProfileUtil
import org.whispersystems.signalservice.api.profiles.SignalServiceProfile
import java.io.IOException
import kotlin.time.Duration.Companion.days

/**
 * The worker job for [org.tm.archive.migrations.AccountConsistencyMigrationJob].
 */
class AccountConsistencyWorkerJob private constructor(parameters: Parameters) : BaseJob(parameters) {

  companion object {
    private val TAG = Log.tag(AccountConsistencyWorkerJob::class.java)

    const val KEY = "AccountConsistencyWorkerJob"

    @JvmStatic
    fun enqueueIfNecessary() {
      if (System.currentTimeMillis() - SignalStore.misc().lastConsistencyCheckTime > 3.days.inWholeMilliseconds) {
        ApplicationDependencies.getJobManager().add(AccountConsistencyWorkerJob())
      }
    }
  }

  constructor() : this(
    Parameters.Builder()
      .setMaxInstancesForFactory(1)
      .addConstraint(NetworkConstraint.KEY)
      .setMaxAttempts(Parameters.UNLIMITED)
      .setLifespan(30.days.inWholeMilliseconds)
      .build()
  )

  override fun serialize(): ByteArray? = null

  override fun getFactoryKey(): String = KEY

  override fun onFailure() = Unit

  override fun onRun() {
    if (!SignalStore.account().hasAciIdentityKey()) {
      Log.i(TAG, "No identity set yet, skipping.")
      return
    }

    if (!SignalStore.account().isRegistered || SignalStore.account().aci == null) {
      Log.i(TAG, "Not yet registered, skipping.")
      return
    }

    val profile: SignalServiceProfile = ProfileUtil.retrieveProfileSync(context, Recipient.self(), SignalServiceProfile.RequestType.PROFILE, false).profile
    val encodedPublicKey = Base64.encodeBytes(SignalStore.account().aciIdentityKey.publicKey.serialize())

    if (profile.identityKey != encodedPublicKey) {
      Log.w(TAG, "Identity key on profile differed from the one we have locally! Marking ourselves unregistered.")

      SignalStore.account().setRegistered(false)
      SignalStore.registrationValues().clearRegistrationComplete()
      SignalStore.registrationValues().clearHasUploadedProfile()
    } else {
      Log.i(TAG, "Everything matched.")
    }

    SignalStore.misc().lastConsistencyCheckTime = System.currentTimeMillis()
  }

  override fun onShouldRetry(e: Exception): Boolean {
    return e is IOException
  }

  class Factory : Job.Factory<AccountConsistencyWorkerJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): AccountConsistencyWorkerJob {
      return AccountConsistencyWorkerJob(parameters)
    }
  }
}
