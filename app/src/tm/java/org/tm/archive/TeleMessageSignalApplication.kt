package org.tm.archive

import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.AndroidCopySettings
import com.tm.androidcopysdk.BackupService
import com.tm.androidcopysdk.CommonUtils
import com.tm.androidcopysdk.DataGrabber
import com.tm.androidcopysdk.api.IArchiveDatabase
import com.tm.androidcopysdk.api.IMessageStoreObserver
import com.tm.androidcopysdk.api.SdkModule
import com.tm.androidcopysdk.database.DefaultArchiveDatabase
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticatorConstants
import com.tm.logger.Log
import org.archiver.ArchiveConstants
import org.archiver.ArchiveLogger
import org.archiver.ArchiveUtil
import org.archiver.core.DefaultMessageStoreObserver
import org.archiver.di.TeleMessageApplicationDependencyProvider
import org.archiver.model.SignalArchiveType
import org.archiver.model.SignalFiler
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies

class TeleMessageSignalApplication : ApplicationContext() {

  override fun onCreate() {
    super.onCreate()
    Log.createInstance(applicationContext)
    ArchiveLogger.sendArchiveLog("TeleMessage logger created")

    initializeSdk()
    initArchiveUrlsAndStartArchive()
  }

  override fun initializeAppDependencies() {
    ApplicationDependencies.init(this, TeleMessageApplicationDependencyProvider(this))
  }

  private fun initializeSdk() {
    val database = SignalDatabase.instance ?: return
    val archiveDatabase: IArchiveDatabase = DefaultArchiveDatabase(this, SignalArchiveType.values().map { it }.toTypedArray())
    val filer = SignalFiler(applicationContext, database.attachmentTable)
    val module = SdkModule(DataGrabber.getInstance(applicationContext), database, archiveDatabase, filer)
    val messageStoreObserver: IMessageStoreObserver<Long> = DefaultMessageStoreObserver.getInstance()
    messageStoreObserver.initialize(module)
    messageStoreObserver.setAccountPhoneNumber(ArchiveUtil.getPhoneNumberInTestMode(applicationContext))
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