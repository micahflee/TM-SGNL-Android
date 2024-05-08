/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.archiver.di

import android.app.Application
import android.content.Context
import com.tm.androidcopysdk.api.IFiler
import com.tm.androidcopysdk.model.ArchiveSettings
import kotlinx.coroutines.flow.MutableStateFlow
import org.archiver.device.TeleMessageSignalCallManager
import org.archiver.model.SignalFiler
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencyProvider
import org.tm.archive.providers.BlobProvider
import org.tm.archive.service.webrtc.SignalCallManager

class TeleMessageApplicationDependencyProvider(
  private val application: Application,

) : ApplicationDependencyProvider(application) {

  override fun provideSignalCallManager(): SignalCallManager {
    return TeleMessageSignalCallManager(application, SignalFiler.getInstance(application.applicationContext), SignalDatabase.calls)
  }


}