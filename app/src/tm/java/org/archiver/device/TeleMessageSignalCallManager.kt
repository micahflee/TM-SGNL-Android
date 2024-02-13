package org.archiver.device

import android.app.Application
import com.tm.logger.Log
import org.archiver.data.TeleMessageTable
import org.signal.ringrtc.CallId
import org.signal.ringrtc.CallManager.CallEvent
import org.signal.ringrtc.GroupCall
import org.signal.ringrtc.Remote
import org.tm.archive.database.SignalDatabase
import org.tm.archive.service.webrtc.SignalCallManager
import org.tm.archive.service.webrtc.state.CallInfoState
import org.tm.archive.service.webrtc.state.WebRtcServiceState

class TeleMessageSignalCallManager(
  application: Application,
) : SignalCallManager(application) {

  private var callInfoState: CallInfoState? = null

  override fun postStateUpdate(state: WebRtcServiceState) {
    val callId = state.callInfoState.getCallId()
    Log.d(TAG, "postStateUpdate - callId: $callId")
    if (callId != null)
      this.callInfoState = state.callInfoState
    super.postStateUpdate(state)
  }

  override fun onCallConcluded(remote: Remote?) {
    super.onCallConcluded(remote)
    onFinishCall()
  }

  override fun onEnded(groupCall: GroupCall, groupCallEndReason: GroupCall.GroupCallEndReason) {
    super.onEnded(groupCall, groupCallEndReason)
    onFinishCall()
  }

  private fun onFinishCall() {
    val callInfoState = callInfoState ?: return
    this.callInfoState = null
    val callConnectedTime = callInfoState.callConnectedTime.takeIf { it > 0 }
    val call = callInfoState.call() ?: return
    (SignalDatabase.messages as TeleMessageTable).onSubmitCall(call, callConnectedTime, callInfoState.callRecipient.isCallLink)
  }

  private fun CallInfoState.getCallId(): CallId? {
    val activePeer = activePeer
    val callRecipient = callRecipient
    if (!callRecipient.isGroup)
      return activePeer?.callId
    return groupCall?.peekInfo?.eraId?.let(CallId::fromEra)
  }

  private fun CallInfoState?.call() = this?.let { getCallId()?.let { SignalDatabase.calls.getCallById(it.longValue(), callRecipient.id) } }

  companion object {

    private const val TAG = "TeleMessageSignalCallManager"

  }
}
