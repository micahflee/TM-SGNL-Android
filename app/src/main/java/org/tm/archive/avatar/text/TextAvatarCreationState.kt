package org.tm.archive.avatar.text

import org.tm.archive.avatar.Avatar
import org.tm.archive.avatar.AvatarColorItem
import org.tm.archive.avatar.Avatars

data class TextAvatarCreationState(
  val currentAvatar: Avatar.Text,
) {
  fun colors(): List<AvatarColorItem> = Avatars.colors.map { AvatarColorItem(it, currentAvatar.color == it) }
}
