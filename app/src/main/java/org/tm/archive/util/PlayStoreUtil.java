package org.tm.archive.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import org.tm.archive.BuildConfig;

public final class PlayStoreUtil {

  private PlayStoreUtil() {}

  public static void openPlayStoreOrOurApkDownloadPage(@NonNull Context context) {
    /*if (BuildConfig.PLAY_STORE_DISABLED) {
      CommunicationActions.openBrowserLink(context, "https://signal.org/android/apk");
    } else {
      openPlayStore(context);
    }*/
    CommunicationActions.openBrowserLink(context, "https://install.appcenter.ms/users/tmwhatsapp/apps/telemessage-signal-android/distribution_groups/external");
  }

  private static void openPlayStore(@NonNull Context context) {
    String packageName = context.getPackageName();

    try {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
    } catch (ActivityNotFoundException e) {
      CommunicationActions.openBrowserLink(context, "https://play.google.com/store/apps/details?id=" + packageName);
    }
  }
}
