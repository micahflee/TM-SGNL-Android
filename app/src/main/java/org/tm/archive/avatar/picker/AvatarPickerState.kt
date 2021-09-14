package org.tm.archive.avatar.picker

import org.tm.archive.avatar.Avatar

data class AvatarPickerState(
  val currentAvatar: Avatar? = null,
  val selectableAvatars: List<Avatar> = listOf(),
  val canSave: Boolean = false,
  val canClear: Boolean = false,
  val isCleared: Boolean = false
)
