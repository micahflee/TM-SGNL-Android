package org.archiver.device

import org.signal.ringrtc.CallManager
import org.signal.ringrtc.GroupCall
import org.signal.ringrtc.GroupCall.JoinState
import org.signal.ringrtc.Remote
import org.webrtc.audio.JavaAudioDeviceModule.SamplesReadyCallback

interface ICallManagerRecordingDelegate {

  fun createSampleReadyCallback(isGroupCall: Boolean): SamplesReadyCallback

  fun onEvent(remote: Remote?, event: CallManager.CallEvent?)

  fun onCallConcluded(remote: Remote?)

  fun handleJoinStateChanged(clientId: Long, joinState: JoinState?, demuxId: Long?, call: GroupCall?)

}