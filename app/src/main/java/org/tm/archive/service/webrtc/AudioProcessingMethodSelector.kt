package org.tm.archive.service.webrtc

import android.os.Build
import org.signal.ringrtc.CallManager.AudioProcessingMethod
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.util.FeatureFlags

/**
 * Utility class to determine which AEC method RingRTC should use.
 */
object AudioProcessingMethodSelector {

  private val hardwareModels: Set<String> by lazy {
    FeatureFlags.hardwareAecModels()
      .split(",")
      .map { it.trim() }
      .filter { it.isNotEmpty() }
      .toSet()
  }

  @JvmStatic
  fun get(): AudioProcessingMethod {
    if (SignalStore.internalValues().audioProcessingMethod() != AudioProcessingMethod.Default) {
      return SignalStore.internalValues().audioProcessingMethod()
    }

    return when {
      FeatureFlags.forceDefaultAec() -> AudioProcessingMethod.Default
      hardwareModels.contains(Build.MODEL) -> AudioProcessingMethod.ForceHardware
      else -> AudioProcessingMethod.ForceSoftware
    }
  }
}
