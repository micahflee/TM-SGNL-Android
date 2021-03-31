package org.tm.archive.logsubmit;

import android.content.Context;

import androidx.annotation.NonNull;

import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.util.TextSecurePreferences;
import org.tm.archive.util.Util;

final class LogSectionKeyPreferences implements LogSection {

  @Override
  public @NonNull String getTitle() {
    return "KEY PREFERENCES";
  }

  @Override
  public @NonNull CharSequence getContent(@NonNull Context context) {
    return new StringBuilder().append("Screen Lock          : ").append(TextSecurePreferences.isScreenLockEnabled(context)).append("\n")
                              .append("Screen Lock Timeout  : ").append(TextSecurePreferences.getScreenLockTimeout(context)).append("\n")
                              .append("Password Disabled    : ").append(TextSecurePreferences.isPasswordDisabled(context)).append("\n")
                              .append("WiFi SMS             : ").append(TextSecurePreferences.isWifiSmsEnabled(context)).append("\n")
                              .append("Default SMS          : ").append(Util.isDefaultSmsProvider(context)).append("\n")
                              .append("Prefer Contact Photos: ").append(SignalStore.settings().isPreferSystemContactPhotos()).append("\n")
                              .append("Call Bandwidth Mode  : ").append(SignalStore.settings().getCallBandwidthMode()).append("\n")
                              .append("Client Deprecated    : ").append(SignalStore.misc().isClientDeprecated()).append("\n");
  }
}
