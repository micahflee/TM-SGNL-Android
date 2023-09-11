/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.components.settings.app.internal.conversation.springboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class InternalConversationSpringboardViewModel : ViewModel() {
  val hasWallpaper = mutableStateOf(false)
}
