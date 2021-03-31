package org.tm.archive.push;

import android.content.Context;

import org.tm.archive.crypto.SecurityEvent;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;

public class SecurityEventListener implements SignalServiceMessageSender.EventListener {

  private static final String TAG = SecurityEventListener.class.getSimpleName();

  private final Context context;

  public SecurityEventListener(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override
  public void onSecurityEvent(SignalServiceAddress textSecureAddress) {
    SecurityEvent.broadcastSecurityUpdateEvent(context);
  }
}
