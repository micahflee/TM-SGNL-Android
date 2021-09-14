package org.tm.archive.util;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.RemoteConfigRefreshJob;
import org.tm.archive.keyvalue.SignalStore;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VersionTracker {

  private static final String TAG = Log.tag(VersionTracker.class);

  public static int getLastSeenVersion(@NonNull Context context) {
    return TextSecurePreferences.getLastVersionCode(context);
  }

  public static void updateLastSeenVersion(@NonNull Context context) {
    try {
      int currentVersionCode = Util.getCanonicalVersionCode();
      int lastVersionCode    = TextSecurePreferences.getLastVersionCode(context);

      if (currentVersionCode != lastVersionCode) {
        Log.i(TAG, "Upgraded from " + lastVersionCode + " to " + currentVersionCode);
        SignalStore.misc().clearClientDeprecated();
        TextSecurePreferences.setLastVersionCode(context, currentVersionCode);
        ApplicationDependencies.getJobManager().add(new RemoteConfigRefreshJob());
        LocalMetrics.getInstance().clear();
      }
    } catch (IOException ioe) {
      throw new AssertionError(ioe);
    }
  }

  public static long getDaysSinceFirstInstalled(Context context) {
    try {
      long installTimestamp = context.getPackageManager()
                                     .getPackageInfo(context.getPackageName(), 0)
                                     .firstInstallTime;

      return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - installTimestamp);
    } catch (PackageManager.NameNotFoundException e) {
      Log.w(TAG, e);
      return 0;
    }
  }
}
