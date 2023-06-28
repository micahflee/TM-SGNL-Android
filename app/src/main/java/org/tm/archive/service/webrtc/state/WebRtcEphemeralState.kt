package org.tm.archive.service.webrtc.state

import org.tm.archive.events.CallParticipant
import org.tm.archive.events.CallParticipantId

/**
 * The state of the call system which contains data which changes frequently.
 */
data class WebRtcEphemeralState(
  val localAudioLevel: CallParticipant.AudioLevel = CallParticipant.AudioLevel.LOWEST,
  val remoteAudioLevels: Map<CallParticipantId, CallParticipant.AudioLevel> = emptyMap()
)
