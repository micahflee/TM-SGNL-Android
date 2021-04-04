package org.tm.archive.util;

import android.content.Context;

import androidx.annotation.NonNull;

import org.tm.archive.push.SignalServiceNetworkAccess;

public final class CensorshipUtil {

  private CensorshipUtil() {}

  public static boolean isCensored(@NonNull Context context) {
    return new SignalServiceNetworkAccess(context).isCensored(context);
  }
}
