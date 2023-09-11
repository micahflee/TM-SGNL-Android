package org.tm.archive.components.reminder;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.tm.archive.R;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.util.PowerManagerCompat;
import org.tm.archive.util.TextSecurePreferences;

@SuppressLint("BatteryLife")
public class DozeReminder extends Reminder {

  @RequiresApi(api = 23)
  public DozeReminder(@NonNull final Context context) {
    super(R.string.DozeReminder_optimize_for_missing_play_services, R.string.DozeReminder_this_device_does_not_support_play_services_tap_to_disable_system_battery);

    setOkListener(v -> {
      TextSecurePreferences.setPromptedOptimizeDoze(context, true);
      PowerManagerCompat.requestIgnoreBatteryOptimizations(context);
    });

    setDismissListener(v -> TextSecurePreferences.setPromptedOptimizeDoze(context, true));
  }

  public static boolean isEligible(Context context) {
    return !SignalStore.account().isFcmEnabled()                   &&
           !TextSecurePreferences.hasPromptedOptimizeDoze(context) &&
           Build.VERSION.SDK_INT >= 23                             &&
           !((PowerManager)context.getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(context.getPackageName());
  }
}
