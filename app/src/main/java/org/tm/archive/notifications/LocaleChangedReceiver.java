package org.tm.archive.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.tm.archive.jobs.EmojiSearchIndexDownloadJob;

public class LocaleChangedReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    NotificationChannels.getInstance().onLocaleChanged();
    EmojiSearchIndexDownloadJob.scheduleImmediately();
  }
}
