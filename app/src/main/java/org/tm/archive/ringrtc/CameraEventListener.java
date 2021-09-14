package org.tm.archive.ringrtc;

import androidx.annotation.NonNull;

public interface CameraEventListener {
  void onFullyInitialized();
  void onCameraSwitchCompleted(@NonNull CameraState newCameraState);
}
