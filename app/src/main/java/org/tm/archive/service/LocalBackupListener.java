package org.tm.archive.service;


import android.content.Context;

import androidx.annotation.NonNull;

import org.tm.archive.jobs.LocalBackupJob;
import org.tm.archive.keyvalue.SettingsValues;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.util.JavaTimeExtensionsKt;
import org.tm.archive.util.TextSecurePreferences;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LocalBackupListener extends PersistentAlarmManagerListener {

  private static final int BACKUP_JITTER_WINDOW_SECONDS = Math.toIntExact(TimeUnit.MINUTES.toSeconds(10));

  @Override
  protected boolean shouldScheduleExact() {
    return true;
  }

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getNextBackupTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    if (SignalStore.settings().isBackupEnabled()) {
      LocalBackupJob.enqueue(false);
    }

    return setNextBackupTimeToIntervalFromNow(context);
  }

  public static void schedule(Context context) {
    if (SignalStore.settings().isBackupEnabled()) {
      new LocalBackupListener().onReceive(context, getScheduleIntent());
    }
  }

  public static long setNextBackupTimeToIntervalFromNow(@NonNull Context context) {
    LocalDateTime now    = LocalDateTime.now();
    int           hour   = SignalStore.settings().getBackupHour();
    int           minute = SignalStore.settings().getBackupMinute();
    LocalDateTime next   = now.withHour(hour).withMinute(minute).withSecond(0);

    int jitter = (new Random().nextInt(BACKUP_JITTER_WINDOW_SECONDS)) - (BACKUP_JITTER_WINDOW_SECONDS / 2);

    next.plusSeconds(jitter);

    if (now.isAfter(next)) {
      next = next.plusDays(1);
    }

    long nextTime = JavaTimeExtensionsKt.toMillis(next);

    TextSecurePreferences.setNextBackupTime(context, nextTime);

    return nextTime;
  }
}
