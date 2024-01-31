package org.archiver.integration

import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.AndroidCopySettings
import com.tm.androidcopysdk.BackupService
import com.tm.androidcopysdk.CommonUtils
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticatorConstants.Companion.BASE_URL
import com.tm.logger.Log
import org.archiver.ArchiveConstants
import org.archiver.ArchiveLogger.Companion.sendArchiveLog
import org.tm.archive.ApplicationContext
import org.tm.archive.BuildConfig
import org.tm.archive.R
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.dependencies.ApplicationDependencyProvider


class TeleMessageSignalApplication : ApplicationContext() {


  override fun onCreate() {
    super.onCreate()
    Log.createInstance(applicationContext)
    sendArchiveLog("TeleMessage logger created")
    initArchiveUrlsAndStartArchive()
  }

  override fun createDependencyProvider(): ApplicationDependencyProvider {
    return TeleMessageApplicationDependencyProvider(this)
  }

  private fun initArchiveUrlsAndStartArchive() {
    val context = applicationContext
    if (CommonUtils.isMyServiceRunning(context, BackupService::class.java)) {
      CommonUtils.stopBackupService(context, false)
    }
    sendArchiveLog("initializeTMAndroidArchive \nsetUrl: \nchosenUrl =" + ArchiveConstants.charlieProduction + "\nKeeperUrl =" + ArchiveConstants.prodKeeper)
    if (BuildConfig.DEBUG) {
      val baseUrlPrefProd =
        PrefManager.getStringPref(context, ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_PRODUCTION_KEY, ArchiveConstants.charlieProduction)
      val baseUrlPrefKeeper =
        PrefManager.getStringPref(context, ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_KEEPER_KEY, ArchiveConstants.prodKeeper)
      BASE_URL = baseUrlPrefProd to baseUrlPrefKeeper
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
      sendArchiveLog("initializeTMAndroidArchive")
    }
    CommonUtils.startBackupService(context)
    sendArchiveLog("Backup service started")
  }

  private fun initializeTMAndroidArchive() {
    val context = applicationContext
    val mSettings = AndroidCopySettings()
    PrefManager.setStringPref(context, "wifi3g", "WIFI3G")
    mSettings.data = AndroidCopySettings.DataSaving.WIFI3G
    Log.d("initializeTMAndroidArchive", "signupSucess with emptey password and user name")
    AndroidCopySDK.getInstance(context).signupSucess( /*ArchiveConstants.signalTestUserName, ArchiveConstants.signalTestPassword*/"", "")
    sendArchiveLog("User name = " + "Password = ")
    val installationEventSent: Boolean = PrefManager.getBooleanPref(context, R.string.installation_event_sent, false)
    // InstallEvent should be sent only once
    if (!installationEventSent) {
      PrefManager.setBooleanPref(context, R.string.installation_event_sent, true)
    }
  }
}