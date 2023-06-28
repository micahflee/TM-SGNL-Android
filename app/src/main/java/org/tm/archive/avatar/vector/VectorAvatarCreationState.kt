package org.tm.archive.avatar.vector

import org.tm.archive.avatar.Avatar
import org.tm.archive.avatar.AvatarColorItem
import org.tm.archive.avatar.Avatars

data class VectorAvatarCreationState(
  val currentAvatar: Avatar.Vector
) {
  fun colors(): List<AvatarColorItem> = Avatars.colors.map { AvatarColorItem(it, currentAvatar.color == it) }
}
