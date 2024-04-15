package org.signal.benchmark.setup

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.signal.benchmark.DummyAccountManagerFactory
import org.signal.core.util.concurrent.safeBlockingGet
import org.signal.libsignal.protocol.SignalProtocolAddress
import org.tm.archive.crypto.IdentityKeyUtil
import org.tm.archive.crypto.MasterSecretUtil
import org.tm.archive.crypto.ProfileKeyUtil
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.net.DeviceTransferBlockingInterceptor
import org.tm.archive.profiles.ProfileName
import org.tm.archive.push.AccountManagerFactory
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.registration.RegistrationData
import org.tm.archive.registration.RegistrationRepository
import org.tm.archive.registration.RegistrationUtil
import org.tm.archive.registration.VerifyResponse
import org.tm.archive.util.Util
import org.whispersystems.signalservice.api.profiles.SignalServiceProfile
import org.whispersystems.signalservice.api.push.ServiceId.ACI
import org.whispersystems.signalservice.api.push.SignalServiceAddress
import org.whispersystems.signalservice.internal.ServiceResponse
import org.whispersystems.signalservice.internal.ServiceResponseProcessor
import org.whispersystems.signalservice.internal.push.VerifyAccountResponse
import java.util.UUID

object TestUsers {

  private var generatedOthers: Int = 0

  fun setupSelf(): Recipient {
    val application: Application = ApplicationDependencies.getApplication()
    DeviceTransferBlockingInterceptor.getInstance().blockNetwork()

    PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean("pref_prompted_push_registration", true).commit()
    val masterSecret = MasterSecretUtil.generateMasterSecret(application, MasterSecretUtil.UNENCRYPTED_PASSPHRASE)
    MasterSecretUtil.generateAsymmetricMasterSecret(application, masterSecret)
    val preferences: SharedPreferences = application.getSharedPreferences(MasterSecretUtil.PREFERENCES_NAME, 0)
    preferences.edit().putBoolean("passphrase_initialized", true).commit()

    SignalStore.account().generateAciIdentityKeyIfNecessary()
    SignalStore.account().generatePniIdentityKeyIfNecessary()

    val registrationRepository = RegistrationRepository(application)
    val registrationData = RegistrationData(
      code = "123123",
      e164 = "+15555550101",
      password = Util.getSecret(18),
      registrationId = registrationRepository.registrationId,
      profileKey = registrationRepository.getProfileKey("+15555550101"),
      fcmToken = "fcm-token",
      pniRegistrationId = registrationRepository.pniRegistrationId,
      recoveryPassword = "asdfasdfasdfasdf"
    )

    val verifyResponse = VerifyResponse(
      VerifyAccountResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false),
      masterKey = null,
      pin = null,
      aciPreKeyCollection = RegistrationRepository.generateSignedAndLastResortPreKeys(SignalStore.account().aciIdentityKey, SignalStore.account().aciPreKeys),
      pniPreKeyCollection = RegistrationRepository.generateSignedAndLastResortPreKeys(SignalStore.account().aciIdentityKey, SignalStore.account().pniPreKeys)
    )

    AccountManagerFactory.setInstance(DummyAccountManagerFactory())

    val response: ServiceResponse<VerifyResponse> = registrationRepository.registerAccount(
      registrationData,
      verifyResponse,
      false
    ).safeBlockingGet()

    ServiceResponseProcessor.DefaultProcessor(response).resultOrThrow

    SignalStore.svr().optOut()
    RegistrationUtil.maybeMarkRegistrationComplete()
    SignalDatabase.recipients.setProfileName(Recipient.self().id, ProfileName.fromParts("Tester", "McTesterson"))

    return Recipient.self()
  }

  fun setupTestRecipient(): RecipientId {
    return setupTestRecipients(1).first()
  }

  fun setupTestRecipients(othersCount: Int): List<RecipientId> {
    val others = mutableListOf<RecipientId>()
    synchronized(this) {
      if (generatedOthers + othersCount !in 0 until 1000) {
        throw IllegalArgumentException("$othersCount must be between 0 and 1000")
      }

      for (i in generatedOthers until generatedOthers + othersCount) {
        val aci = ACI.from(UUID.randomUUID())
        val recipientId = RecipientId.from(SignalServiceAddress(aci, "+15555551%03d".format(i)))
        SignalDatabase.recipients.setProfileName(recipientId, ProfileName.fromParts("Buddy", "#$i"))
        SignalDatabase.recipients.setProfileKeyIfAbsent(recipientId, ProfileKeyUtil.createNew())
        SignalDatabase.recipients.setCapabilities(recipientId, SignalServiceProfile.Capabilities(true, true, true))
        SignalDatabase.recipients.setProfileSharing(recipientId, true)
        SignalDatabase.recipients.markRegistered(recipientId, aci)
        val otherIdentity = IdentityKeyUtil.generateIdentityKeyPair()
        ApplicationDependencies.getProtocolStore().aci().saveIdentity(SignalProtocolAddress(aci.toString(), 0), otherIdentity.publicKey)

        others += recipientId
      }

      generatedOthers += othersCount
    }

    return others
  }
}
