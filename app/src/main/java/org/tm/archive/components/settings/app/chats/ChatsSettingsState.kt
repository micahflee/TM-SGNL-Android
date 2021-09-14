package org.tm.archive.components.settings.app.chats

data class ChatsSettingsState(
  val generateLinkPreviews: Boolean,
  val useAddressBook: Boolean,
  val useSystemEmoji: Boolean,
  val enterKeySends: Boolean,
  val chatBackupsEnabled: Boolean
)
