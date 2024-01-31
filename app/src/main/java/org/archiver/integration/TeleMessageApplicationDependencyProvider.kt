package org.archiver.integration

import android.app.Application
import org.archiver.call.TeleMessageSignalCallManager
import org.tm.archive.dependencies.ApplicationDependencyProvider
import org.tm.archive.service.webrtc.SignalCallManager

class TeleMessageApplicationDependencyProvider(private val application: Application) : ApplicationDependencyProvider(application) {

  override fun provideSignalCallManager(): SignalCallManager {
    return TeleMessageSignalCallManager(application)
  }
}