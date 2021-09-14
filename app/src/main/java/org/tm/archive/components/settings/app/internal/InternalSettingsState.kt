package org.tm.archive.components.settings.app.internal

import org.tm.archive.emoji.EmojiFiles

data class InternalSettingsState(
  val seeMoreUserDetails: Boolean,
  val shakeToReport: Boolean,
  val gv2doNotCreateGv2Groups: Boolean,
  val gv2forceInvites: Boolean,
  val gv2ignoreServerChanges: Boolean,
  val gv2ignoreP2PChanges: Boolean,
  val disableAutoMigrationInitiation: Boolean,
  val disableAutoMigrationNotification: Boolean,
  val forceCensorship: Boolean,
  val callingServer: String,
  val useBuiltInEmojiSet: Boolean,
  val emojiVersion: EmojiFiles.Version?,
  val removeSenderKeyMinimium: Boolean,
  val delayResends: Boolean,
)
