package org.tm.archive

import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.AndroidCopySettings
import com.tm.androidcopysdk.BackupService
import com.tm.androidcopysdk.CommonUtils
import com.tm.androidcopysdk.api.SdkModule
import com.tm.androidcopysdk.device.ArchiveMessagesProcessor
import com.tm.androidcopysdk.device.SendSignatureProcessor
import com.tm.androidcopysdk.model.ArchiveSettings
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticatorConstants
import com.tm.logger.Log
import kotlinx.coroutines.flow.MutableStateFlow
import org.archiver.ArchiveConstants
import org.archiver.ArchiveLogger
import org.archiver.SignalLoggerAdapter
import org.archiver.device.CallManagerRecordingDelegate
import org.archiver.di.TeleMessageApplicationDependencyProvider
import org.archiver.di.TeleMessageApplicationDependencyProvider.Companion.getSdkModule
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.Log.blockUntilAllWritesFinished
import org.signal.libsignal.protocol.logging.SignalProtocolLoggerProvider
import org.signal.ringrtc.CallManager
import org.tm.archive.database.LogDatabase.Companion.getInstance
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.logging.CustomSignalProtocolLogger
import org.tm.archive.logging.PersistentLogger
import org.tm.archive.util.FeatureFlags

class TeleMessageSignalApplication : ApplicationContext() {

  private val dependencyProvider by lazy { TeleMessageApplicationDependencyProvider(this) }

  override fun onCreate() {
    super.onCreate()
    Log.createInstance(applicationContext)
    ArchiveLogger.sendArchiveLog("TeleMessage logger created")

    initializeSdk()
    initArchiveUrlsAndStartArchive()
  }

  override fun initializeAppDependencies() {
    ApplicationDependencies.init(this, dependencyProvider)
  }

  override fun beforeInitializeCallManager() {
    CallManager.setDelegate(CallManagerRecordingDelegate.getInstance(applicationContext))
    super.beforeInitializeCallManager()
  }

  override fun initializeLogging() {
    org.signal.core.util.logging.Log.initialize({ FeatureFlags.internalUser() }, SignalLoggerAdapter(this), PersistentLogger(this))

    SignalProtocolLoggerProvider.setProvider(CustomSignalProtocolLogger())

    SignalExecutors.UNBOUNDED.execute {
      blockUntilAllWritesFinished()
      getInstance(this).logs.trimToSize()
      getInstance(this).crashes.trimToSize()
    }
  }

  private fun initializeSdk() {
    val module = getSdkModule(requireNotNull(SignalDatabase.instance))
    val messageObserver = TeleMessageApplicationDependencyProvider.messageStoreObserver
    messageObserver.addProcessor(ArchiveMessagesProcessor(module))
    messageObserver.addProcessor(SendSignatureProcessor(module))
    messageObserver.initialize(module)
  }

  private fun initArchiveUrlsAndStartArchive() {
    val context = applicationContext
    if (CommonUtils.isMyServiceRunning(context, BackupService::class.java)) {
      CommonUtils.stopBackupService(context, false)
    }
    ArchiveLogger.sendArchiveLog("initializeTMAndroidArchive \nsetUrl: \nchosenUrl =" + ArchiveConstants.charlieProduction + "\nKeeperUrl =" + ArchiveConstants.prodKeeper)
    if (BuildConfig.DEBUG) {
      val baseUrlPrefProd =
        PrefManager.getStringPref(context, ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_PRODUCTION_KEY, ArchiveConstants.charlieProduction)
      val baseUrlPrefKeeper =
        PrefManager.getStringPref(context, ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_KEEPER_KEY, ArchiveConstants.prodKeeper)
      AuthenticatorConstants.BASE_URL = baseUrlPrefProd to baseUrlPrefKeeper
      CommonUtils.setUrl(context, baseUrlPrefProd, baseUrlPrefKeeper)
    } else {
      CommonUtils.setUrl(context, ArchiveConstants.charlieProduction, ArchiveConstants.prodKeeper)
    }
    CommonUtils.setSqlInfo(context, if (ArchiveConstants.isTestMode) ArchiveConstants.signalTestPassword else ArchiveConstants.signalCurrentPassword)

    //set SDK to active -> need to change it with the self register
    val installationEventSent: Boolean = PrefManager.getBooleanPref(context, R.string.installation_event_sent, false)
    PrefManager.setBooleanPref(context, "activated_aa", true)
    if (ArchiveConstants.isTestMode || !installationEventSent) {
      initializeTMAndroidArchive()
      ArchiveLogger.sendArchiveLog("initializeTMAndroidArchive")
    }
    CommonUtils.startBackupService(context)
    ArchiveLogger.sendArchiveLog("Backup service started")
  }

  private fun initializeTMAndroidArchive() {
    val context = applicationContext
    val mSettings = AndroidCopySettings()
    PrefManager.setStringPref(context, "wifi3g", "WIFI3G")
    mSettings.data = AndroidCopySettings.DataSaving.WIFI3G
    Log.d("initializeTMAndroidArchive", "signupSucess with emptey password and user name")
    AndroidCopySDK.getInstance(context).signupSucess( /*ArchiveConstants.signalTestUserName, ArchiveConstants.signalTestPassword*/"", "")
    ArchiveLogger.sendArchiveLog("User name = " + "Password = ")
    val installationEventSent: Boolean = PrefManager.getBooleanPref(context, R.string.installation_event_sent, false)
    // InstallEvent should be sent only once
    if (!installationEventSent) {
      PrefManager.setBooleanPref(context, R.string.installation_event_sent, true)
    }
  }
}