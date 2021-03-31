package org.tm.archive.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.PushNotificationReceiveJob;

public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    ApplicationDependencies.getJobManager().add(new PushNotificationReceiveJob());
  }
}
