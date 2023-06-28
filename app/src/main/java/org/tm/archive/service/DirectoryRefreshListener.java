package org.tm.archive.service;


import android.content.Context;
import android.content.Intent;

import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.DirectoryRefreshJob;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.util.FeatureFlags;
import org.tm.archive.util.TextSecurePreferences;

import java.util.concurrent.TimeUnit;

public class DirectoryRefreshListener extends PersistentAlarmManagerListener {

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getDirectoryRefreshTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    if (scheduledTime != 0 && SignalStore.account().isRegistered()) {
      ApplicationDependencies.getJobManager().add(new DirectoryRefreshJob(true));
    }

    long newTime;

    if (SignalStore.misc().isCdsBlocked()) {
      newTime = Math.min(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(6),
                         SignalStore.misc().getCdsBlockedUtil());
    } else {
      newTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(FeatureFlags.cdsRefreshIntervalSeconds());
      TextSecurePreferences.setDirectoryRefreshTime(context, newTime);
    }

    TextSecurePreferences.setDirectoryRefreshTime(context, newTime);

    return newTime;
  }

  public static void schedule(Context context) {
    new DirectoryRefreshListener().onReceive(context, getScheduleIntent());
  }
}
