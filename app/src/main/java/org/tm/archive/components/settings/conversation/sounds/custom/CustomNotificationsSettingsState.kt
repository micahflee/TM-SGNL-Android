package org.tm.archive.components.settings.conversation.sounds.custom

import android.net.Uri
import org.tm.archive.database.RecipientDatabase

data class CustomNotificationsSettingsState(
  val isInitialLoadComplete: Boolean = false,
  val hasCustomNotifications: Boolean = false,
  val controlsEnabled: Boolean = false,
  val messageVibrateState: RecipientDatabase.VibrateState = RecipientDatabase.VibrateState.DEFAULT,
  val messageVibrateEnabled: Boolean = false,
  val messageSound: Uri? = null,
  val callVibrateState: RecipientDatabase.VibrateState = RecipientDatabase.VibrateState.DEFAULT,
  val callSound: Uri? = null,
  val showCallingOptions: Boolean = false,
)
