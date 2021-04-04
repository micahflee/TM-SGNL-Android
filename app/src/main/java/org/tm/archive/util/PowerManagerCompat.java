package org.tm.archive.util;

import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;

public class PowerManagerCompat {

  public static boolean isDeviceIdleMode(@NonNull PowerManager powerManager) {
    if (Build.VERSION.SDK_INT >= 23) {
      return powerManager.isDeviceIdleMode();
    }
    return false;
  }
}
