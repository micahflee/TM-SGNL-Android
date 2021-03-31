package org.tm.archive.lock.v2;

import android.content.Context;

import androidx.annotation.NonNull;

import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.util.TextSecurePreferences;

public final class RegistrationLockUtil {

  private RegistrationLockUtil() {}

  public static boolean userHasRegistrationLock(@NonNull Context context) {
    return TextSecurePreferences.isV1RegistrationLockEnabled(context) || SignalStore.kbsValues().isV2RegistrationLockEnabled();
  }
}
