package org.archiver.device

import android.app.Application
import com.tm.androidcopysdk.Models.CallAnswerType
import com.tm.androidcopysdk.Models.CallRtcMode
import com.tm.androidcopysdk.Models.Direction
import com.tm.androidcopysdk.device.CallProcessor
import kotlinx.coroutines.Dispatchers
import org.signal.ringrtc.CallManager.CallEvent
import org.signal.ringrtc.Remote
import org.tm.archive.events.WebRtcViewModel
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.ringrtc.RemotePeer
import org.tm.archive.service.webrtc.SignalCallManager
import org.tm.archive.service.webrtc.state.WebRtcServiceState
import kotlin.jvm.optionals.getOrNull

class TeleMessageSignalCallManager(application: Application) : SignalCallManager(application) {

  private val context = application.applicationContext

  private val processor = CallProcessor(context, Dispatchers.IO)

  private var callConnectedTime = -1L

  override fun postStateUpdate(state: WebRtcServiceState) {
    val activePeer = state.callInfoState.activePeer
    val callRecipient = state.callInfoState.callRecipient
    val setupState = state.getCallSetupState(activePeer?.callId)
    callConnectedTime = state.callInfoState.callConnectedTime
    if (!processor.isRunning() && activePeer?.callId != null) {
      onStartCall(activePeer, callRecipient, state.callInfoState.callState == WebRtcViewModel.State.CALL_OUTGOING)
    }
//    else
//      Log.d(javaClass.simpleName, "postStateUpdate ${setupState.isAcceptWithVideo} ${setupState.isRemoteVideoOffer} ${setupState.isEnableVideoOnCreate}")
    val isVideoCall = setupState.run { isEnableVideoOnCreate || isRemoteVideoOffer || isAcceptWithVideo }
    processor.setRtcMode(CallRtcMode.fromIsVideo(isVideoCall))
    super.postStateUpdate(state)
  }

  private fun onStartCall(remotePeer: RemotePeer, recipient: Recipient, isOutgoing: Boolean) {
    val callId = remotePeer.callId.longValue()
    val recipientPhoneNumber = recipient.e164.getOrNull() ?: return
    val recipientName = recipient.getDisplayNameOrUsername(context)
    processor.setAccountPhoneNumber(SignalStore.account().e164)
    processor.onBeginCall(callId, Direction.fromIsOutgoing(isOutgoing), recipientPhoneNumber, recipientName)
  }

  override fun onCallEvent(remote: Remote?, event: CallEvent) {
    super.onCallEvent(remote, event)
    when (event) {
      CallEvent.ENDED_LOCAL_HANGUP -> processor.setAnswerType(CallAnswerType.Missed)
      CallEvent.ENDED_REMOTE_HANGUP,
      CallEvent.ENDED_REMOTE_HANGUP_NEED_PERMISSION,
      CallEvent.ENDED_REMOTE_HANGUP_ACCEPTED,
      CallEvent.ENDED_REMOTE_HANGUP_DECLINED,
      CallEvent.ENDED_REMOTE_HANGUP_BUSY -> processor.setAnswerType(CallAnswerType.Reject)
      CallEvent.ENDED_REMOTE_BUSY,
      CallEvent.ENDED_REMOTE_GLARE,
      CallEvent.ENDED_REMOTE_RECALL,
      CallEvent.ENDED_TIMEOUT,
      CallEvent.ENDED_INTERNAL_FAILURE,
      CallEvent.ENDED_SIGNALING_FAILURE,
      CallEvent.ENDED_GLARE_HANDLING_FAILURE,
      CallEvent.ENDED_CONNECTION_FAILURE,
      CallEvent.ENDED_APP_DROPPED_CALL -> processor.setAnswerType(CallAnswerType.Missed)
      else -> return
    }
  }

  override fun onCallConcluded(remote: Remote?) {
    val callConnectedTime = this.callConnectedTime.takeIf { it > 0 }
    this.callConnectedTime = -1
    val duration = callConnectedTime?.let { System.currentTimeMillis() - it } ?: 0
    processor.setDuration(duration)
    processor.onCallConcluded()
    super.onCallConcluded(remote)
  }

}
