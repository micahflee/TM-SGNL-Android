package org.tm.archive.webrtc

import org.tm.archive.components.webrtc.CallParticipantsState

data class CallParticipantsViewState(
  val callParticipantsState: CallParticipantsState,
  val isPortrait: Boolean,
  val isLandscapeEnabled: Boolean
)
