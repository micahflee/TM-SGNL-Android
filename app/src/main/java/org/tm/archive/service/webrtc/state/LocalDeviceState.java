package org.tm.archive.service.webrtc.state;

import androidx.annotation.NonNull;

import org.tm.archive.components.sensors.Orientation;
import org.tm.archive.ringrtc.CameraState;

/**
 * Local device specific state.
 */
public final class LocalDeviceState {
  CameraState cameraState;
  boolean     microphoneEnabled;
  boolean     bluetoothAvailable;
  Orientation orientation;

  LocalDeviceState() {
    this(CameraState.UNKNOWN, true, false, Orientation.PORTRAIT_BOTTOM_EDGE);
  }

  LocalDeviceState(@NonNull LocalDeviceState toCopy) {
    this(toCopy.cameraState, toCopy.microphoneEnabled, toCopy.bluetoothAvailable, toCopy.orientation);
  }

  LocalDeviceState(@NonNull CameraState cameraState, boolean microphoneEnabled, boolean bluetoothAvailable, @NonNull Orientation orientation) {
    this.cameraState        = cameraState;
    this.microphoneEnabled  = microphoneEnabled;
    this.bluetoothAvailable = bluetoothAvailable;
    this.orientation        = orientation;
  }

  public @NonNull CameraState getCameraState() {
    return cameraState;
  }

  public boolean isMicrophoneEnabled() {
    return microphoneEnabled;
  }

  public boolean isBluetoothAvailable() {
    return bluetoothAvailable;
  }

  public @NonNull Orientation getOrientation() {
    return orientation;
  }
}
