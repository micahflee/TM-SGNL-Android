/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.jobs

import org.signal.core.util.concurrent.safeBlockingGet
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobmanager.impl.NetworkConstraint
import org.tm.archive.service.webrtc.links.CallLinkCredentials
import org.tm.archive.service.webrtc.links.ReadCallLinkResult
import org.tm.archive.service.webrtc.links.SignalCallLinkManager
import org.whispersystems.signalservice.internal.push.SyncMessage.CallLinkUpdate
import java.util.concurrent.TimeUnit

/**
 * Requests the latest call link state from the call service.
 */
class RefreshCallLinkDetailsJob private constructor(
  parameters: Parameters,
  private val callLinkUpdate: CallLinkUpdate
) : BaseJob(parameters) {

  constructor(callLinkUpdate: CallLinkUpdate) : this(
    Parameters.Builder()
      .addConstraint(NetworkConstraint.KEY)
      .setQueue("__RefreshCallLinkDetailsJob__")
      .setLifespan(TimeUnit.DAYS.toMillis(1))
      .setMaxAttempts(Parameters.UNLIMITED)
      .build(),
    callLinkUpdate
  )

  companion object {
    const val KEY = "RefreshCallLinkDetailsJob"
  }

  override fun serialize(): ByteArray = callLinkUpdate.encode()

  override fun getFactoryKey(): String = KEY

  override fun onFailure() = Unit

  override fun onRun() {
    val manager: SignalCallLinkManager = ApplicationDependencies.getSignalCallManager().callLinkManager
    val credentials = CallLinkCredentials(
      linkKeyBytes = callLinkUpdate.rootKey!!.toByteArray(),
      adminPassBytes = callLinkUpdate.adminPassKey?.toByteArray()
    )

    when (val result = manager.readCallLink(credentials).safeBlockingGet()) {
      is ReadCallLinkResult.Success -> {
        SignalDatabase.callLinks.updateCallLinkState(credentials.roomId, result.callLinkState)
      }
      else -> Unit
    }
  }

  override fun onShouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<RefreshCallLinkDetailsJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): RefreshCallLinkDetailsJob {
      val callLinkUpdate = CallLinkUpdate.ADAPTER.decode(serializedData!!)
      return RefreshCallLinkDetailsJob(parameters, callLinkUpdate)
    }
  }
}
