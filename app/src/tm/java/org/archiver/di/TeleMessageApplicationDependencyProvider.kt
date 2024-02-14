/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.archiver.di

import android.app.Application
import com.tm.androidcopysdk.api.IMessageStoreObserver
import com.tm.androidcopysdk.device.DefaultMessageStoreObserver
import org.archiver.device.TeleMessageSignalCallManager
import org.tm.archive.dependencies.ApplicationDependencyProvider
import org.tm.archive.service.webrtc.SignalCallManager

class TeleMessageApplicationDependencyProvider(
  private val application: Application,

) : ApplicationDependencyProvider(application) {

  override fun provideSignalCallManager(): SignalCallManager {
    return TeleMessageSignalCallManager(application)
  }

  companion object {
    val messageStoreObserver: IMessageStoreObserver<Long> by lazy { DefaultMessageStoreObserver.getInstance() }
  }
}