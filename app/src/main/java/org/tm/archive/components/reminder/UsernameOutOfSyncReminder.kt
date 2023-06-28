package org.tm.archive.components.reminder

import android.content.Context
import org.tm.archive.R
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.util.FeatureFlags

/**
 * Displays a reminder message when the local username gets out of sync with
 * what the server thinks our username is.
 */
class UsernameOutOfSyncReminder(context: Context) : Reminder(
  null,
  context.getString(R.string.UsernameOutOfSyncReminder__something_went_wrong)
) {

  init {
    addAction(
      Action(
        context.getString(R.string.UsernameOutOfSyncReminder__fix_now),
        R.id.reminder_action_fix_username
      )
    )
  }

  override fun isDismissable(): Boolean {
    return false
  }

  companion object {
    @JvmStatic
    fun isEligible(): Boolean {
      return FeatureFlags.usernames() && SignalStore.phoneNumberPrivacy().isUsernameOutOfSync
    }
  }
}
