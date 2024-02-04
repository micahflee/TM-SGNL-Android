/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.archiver

import android.app.Application
import org.archiver.call.TeleMessageSignalCallManager
import org.tm.archive.dependencies.ApplicationDependencyProvider
import org.tm.archive.service.webrtc.SignalCallManager

class TeleMessageApplicationDependencyProvider(private val application: Application) : ApplicationDependencyProvider(application) {

  override fun provideSignalCallManager(): SignalCallManager {
    return TeleMessageSignalCallManager(application)
  }
}