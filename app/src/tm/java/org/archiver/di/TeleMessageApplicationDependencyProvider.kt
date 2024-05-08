/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.archiver.di

import android.app.Application
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.DataGrabber
import com.tm.androidcopysdk.api.IArchiveDatabase
import com.tm.androidcopysdk.api.IMessageStoreObserver
import com.tm.androidcopysdk.api.SdkModule
import com.tm.androidcopysdk.database.DefaultArchiveDatabase
import com.tm.androidcopysdk.device.DefaultMessageStoreObserver
import com.tm.androidcopysdk.model.ArchiveSettings
import kotlinx.coroutines.flow.MutableStateFlow
import com.tm.androidcopysdk.api.IFiler
import com.tm.androidcopysdk.model.ClientType
import org.archiver.device.TeleMessageSignalCallManager
import org.archiver.model.SignalFiler
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencyProvider
import org.tm.archive.providers.BlobProvider
import org.tm.archive.service.webrtc.SignalCallManager

class TeleMessageApplicationDependencyProvider(
  private val application: Application,

) : ApplicationDependencyProvider(application) {

  val filer: IFiler by lazy { SignalFiler(application.applicationContext, SignalDatabase.messages, SignalDatabase.attachments,
    SignalDatabase.stickers, BlobProvider.getInstance()) }

  override fun provideSignalCallManager(): SignalCallManager {
    return TeleMessageSignalCallManager(application, filer, SignalDatabase.calls)
  }

  companion object {

    private var sdkModule: SdkModule<Long>? = null

    val messageStoreObserver: IMessageStoreObserver<Long> by lazy { DefaultMessageStoreObserver.getInstance() }

    fun Application.getSdkModule(database: SignalDatabase): SdkModule<Long> {
      var sdkModule = sdkModule
      if (sdkModule == null) {
        val sdk = AndroidCopySDK.getInstance(applicationContext)
        val archiveDatabase: IArchiveDatabase = DefaultArchiveDatabase(this)
        val filer = SignalFiler(applicationContext, database.messageTable, database.attachmentTable, database.stickerTable, BlobProvider.getInstance())
        val settings = MutableStateFlow(ArchiveSettings(isAppActivated = true, editMessageArchivingSupported = false, supportCallArchiving = true))
        sdkModule = SdkModule(sdk, DataGrabber.getInstance(applicationContext), ClientType.Signal, database, archiveDatabase, filer, settings)
        TeleMessageApplicationDependencyProvider.sdkModule = sdkModule
      }
      return sdkModule
    }
  }
}