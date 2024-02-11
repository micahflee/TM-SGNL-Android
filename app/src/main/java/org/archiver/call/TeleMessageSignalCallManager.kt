package org.archiver.call

import android.app.Application
import com.tm.logger.Log
import kotlinx.coroutines.Dispatchers
import org.archiver.ArchiveUtil
import org.archiver.model.CallAnswerType
import org.archiver.model.CallDirection
import org.archiver.model.CallRtcMode
import org.signal.ringrtc.CallManager.CallEvent
import org.signal.ringrtc.GroupCall
import org.signal.ringrtc.Remote
import org.tm.archive.events.WebRtcViewModel
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.service.webrtc.SignalCallManager
import org.tm.archive.service.webrtc.state.WebRtcServiceState
import kotlin.jvm.optionals.getOrNull

//*TM_SA*/add this class

class TeleMessageSignalCallManager(application: Application) : SignalCallManager(application) {

  private val context = application.applicationContext

  private val processor = CallProcessor(context, Dispatchers.IO)

  private var callConnectedTime = -1L

  companion object {
    private const val TAG = "TeleMessageSignalCallManager"
  }

  override fun postStateUpdate(state: WebRtcServiceState) {
    val activePeer = state.callInfoState.activePeer
    val callRecipient = state.callInfoState.callRecipient
    val callId : Long?
    val isVideoCall : Boolean

    callConnectedTime = state.callInfoState.callConnectedTime
    isVideoCall = state.getCallSetupState(activePeer?.callId).run { isEnableVideoOnCreate || isRemoteVideoOffer || isAcceptWithVideo }

    callId = if (!callRecipient.isGroup) {
      activePeer?.callId?.longValue()
    } else {
      state.callInfoState.groupCall?.localDeviceState?.demuxId
    }
    Log.d(TAG, "postStateUpdate - isVideoCall: $isVideoCall, isGroup: ${callRecipient.isGroup}, callId: $callId")
    if (!processor.isRunning() && callId != null) {
      onStartCall(callId, callRecipient, state.callInfoState.callState == WebRtcViewModel.State.CALL_OUTGOING)
    }
    processor.setRtcMode(CallRtcMode.fromIsVideo(isVideoCall))
    super.postStateUpdate(state)
  }

  private fun onStartCall(callId: Long, recipient: Recipient, isOutgoing: Boolean) {
    val recipientPhoneNumber  : String? = if (!recipient.isGroup) {
      recipient.e164.getOrNull()
    } else {//TODO: fix it for list after Moti fix recipient list call log in SDK
      val participantList = ArchiveUtil.getRecipientsListFromParticipantIds(recipient)
      participantList[0].e164.getOrNull()
    }
    val recipientName = recipient.getDisplayNameOrUsername(context)
    Log.d(TAG, "onStartCall -> callId: $callId, recipientName: $recipientName, recipientPhoneNumber: $recipientPhoneNumber")
    if (recipientName.isEmpty()) return
    processor.setAccountPhoneNumber(SignalStore.account().e164)
    if (recipientPhoneNumber != null) {
      processor.onBeginCall(callId, CallDirection.fromIsOutgoing(isOutgoing), recipientPhoneNumber, recipientName)
    }
  }


  override fun onCallEvent(remote: Remote?, event: CallEvent) {
    Log.d(TAG, "onCallEvent: $event")
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
    Log.d("call archiving", "onCallConcluded")
    onFinishCall()
    super.onCallConcluded(remote)
  }

  override fun onEnded(groupCall: GroupCall, groupCallEndReason: GroupCall.GroupCallEndReason) {
    Log.d("call archiving", "onEnded")
    onFinishCall()
    super.onEnded(groupCall, groupCallEndReason)
  }

  private fun onFinishCall() {
    val callConnectedTime = this.callConnectedTime.takeIf { it > 0 }
    this.callConnectedTime = -1
    val duration = callConnectedTime?.let { System.currentTimeMillis() - it } ?: 0
    processor.setDuration(duration)
    processor.onCallConcluded()
  }

  private fun isCallRecordingSupported(communicationType: CallRtcMode) = true

}
