package org.tm.archive.components.reminder

import android.content.Context
import org.tm.archive.R
import org.tm.archive.keyvalue.AccountValues.UsernameSyncState
import org.tm.archive.keyvalue.SignalStore

/**
 * Displays a reminder message when the local username gets out of sync with
 * what the server thinks our username is.
 */
class UsernameOutOfSyncReminder : Reminder(NO_RESOURCE) {

  init {
    val action = if (SignalStore.account().usernameSyncState == UsernameSyncState.USERNAME_AND_LINK_CORRUPTED) {
      R.id.reminder_action_fix_username_and_link
    } else {
      R.id.reminder_action_fix_username_link
    }

    addAction(
      Action(
        R.string.UsernameOutOfSyncReminder__fix_now,
        action
      )
    )
  }

  override fun getText(context: Context): CharSequence {
    return if (SignalStore.account().usernameSyncState == UsernameSyncState.USERNAME_AND_LINK_CORRUPTED) {
      context.getString(R.string.UsernameOutOfSyncReminder__username_and_link_corrupt)
    } else {
      context.getString(R.string.UsernameOutOfSyncReminder__link_corrupt)
    }
  }

  override fun isDismissable(): Boolean {
    return false
  }

  companion object {
    @JvmStatic
    fun isEligible(): Boolean {
      return when (SignalStore.account().usernameSyncState) {
        UsernameSyncState.USERNAME_AND_LINK_CORRUPTED -> true
        UsernameSyncState.LINK_CORRUPTED -> true
        UsernameSyncState.IN_SYNC -> false
      }
    }
  }
}
