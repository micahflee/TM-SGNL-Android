package org.tm.archive.components.reminder;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import org.tm.archive.R;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.util.CommunicationActions;
import org.tm.archive.util.PlayStoreUtil;

import java.util.List;

/**
 * Showed when a build has fully expired (either via the compile-time constant, or remote
 * deprecation).
 */
public class ExpiredBuildReminder extends Reminder {

  public ExpiredBuildReminder(final Context context) {
    super(R.string.ExpiredBuildReminder_this_version_of_signal_has_expired);
    addAction(new Action(R.string.ExpiredBuildReminder_update_now, R.id.reminder_action_update_now));
  }

  @Override
  public boolean isDismissable() {
    return false;
  }

  @Override
  public @NonNull Importance getImportance() {
    return Importance.TERMINAL;
  }

  public static boolean isEligible() {
    return SignalStore.misc().isClientDeprecated();
  }
}
