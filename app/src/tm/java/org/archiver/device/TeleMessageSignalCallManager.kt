package org.archiver.device

import android.app.Application
import com.tm.androidcopysdk.api.IFiler
import org.archiver.data.TeleMessageTable
import org.archiver.model.SignalFiler
import org.signal.core.util.logging.Log
import org.signal.ringrtc.CallId
import org.signal.ringrtc.GroupCall
import org.signal.ringrtc.Remote
import org.tm.archive.database.CallTable
import org.tm.archive.database.SignalDatabase
import org.tm.archive.service.webrtc.SignalCallManager
import org.tm.archive.service.webrtc.state.CallInfoState
import org.tm.archive.service.webrtc.state.WebRtcServiceState
import java.io.File

class TeleMessageSignalCallManager(
  application: Application,

  private val filer: IFiler,

  private val calls: CallTable,
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
    val call = callInfoState.call() ?: return
    val isAdHocCall = callInfoState.callRecipient.isCallLink
    if (isAdHocCall) {
			deleteRecordedFiles(call.callId)
      return
    }
    (SignalDatabase.messages as TeleMessageTable).onSubmitCall(call, callInfoState)
  }

  private fun CallInfoState.getCallId(): CallId? {
    val activePeer = activePeer
    val callRecipient = callRecipient
    if (!callRecipient.isGroup)
      return activePeer?.callId
    return groupCall?.peekInfo?.eraId?.let(CallId::fromEra)
  }

  private fun CallInfoState?.call() = this?.let { getCallId()?.let { calls.getCallById(it.longValue(), callRecipient.id) } }

	private fun deleteRecordedFiles(callId: Long) {
		(filer as SignalFiler).getRecordedFile(callId.toString())?.sourcePath?.let(::File)?.run { if (exists()) delete() }
	}

  companion object {

    private const val TAG = "TeleMessageSignalCallManager"

  }
}
