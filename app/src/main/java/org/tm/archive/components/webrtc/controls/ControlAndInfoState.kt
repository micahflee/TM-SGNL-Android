/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.components.webrtc.controls

import androidx.compose.runtime.Immutable
import org.tm.archive.database.CallLinkTable

@Immutable
data class ControlAndInfoState(
  val callLink: CallLinkTable.CallLink? = null,
  val resetScrollState: Long = 0
) {
  fun isSelfAdmin(): Boolean {
    return callLink?.credentials?.adminPassBytes != null
  }
}
