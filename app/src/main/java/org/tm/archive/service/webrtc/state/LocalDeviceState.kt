package org.tm.archive.service.webrtc.state

import org.tm.archive.components.sensors.Orientation
import org.tm.archive.ringrtc.CameraState
import org.tm.archive.webrtc.audio.SignalAudioManager

/**
 * Local device specific state.
 */
data class LocalDeviceState constructor(
  var cameraState: CameraState = CameraState.UNKNOWN,
  var isMicrophoneEnabled: Boolean = true,
  var orientation: Orientation = Orientation.PORTRAIT_BOTTOM_EDGE,
  var isLandscapeEnabled: Boolean = false,
  var deviceOrientation: Orientation = Orientation.PORTRAIT_BOTTOM_EDGE,
  var activeDevice: SignalAudioManager.AudioDevice = SignalAudioManager.AudioDevice.NONE,
  var availableDevices: Set<SignalAudioManager.AudioDevice> = emptySet()
) {

  fun duplicate(): LocalDeviceState {
    return copy()
  }
}
