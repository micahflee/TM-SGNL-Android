package org.archiver.converter

import com.tm.androidcopysdk.model.ArchiveCallInfo
import com.tm.androidcopysdk.model.ArchiveMessageType
import com.tm.androidcopysdk.model.CallAnswerType
import com.tm.androidcopysdk.model.CallRtcMode
import com.tm.androidcopysdk.model.Direction
import org.tm.archive.database.CallTable
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.events.WebRtcViewModel
import org.tm.archive.service.webrtc.state.CallInfoState

class SignalCallInfoConverter {

  fun convert(message: MessageRecord, type: ArchiveMessageType, callInfo: CallInfoState?): ArchiveCallInfo? {
    val callConnectedTime = callInfo?.callConnectedTime?.takeIf { it > 0 }
    if (type != ArchiveMessageType.Call && type != ArchiveMessageType.Unknown)
      return null
    val durationMs = callConnectedTime?.let { System.currentTimeMillis() - it } ?: 0
    val call = (message as? MmsMessageRecord)?.call
    return ArchiveCallInfo(
      id = call?.callId?.toString(),
      startedAt = callConnectedTime,
      durationMs = durationMs / 1000,
      answerType = getAnswerType(call, callInfo),
      rtcMode = message.rtcMode(call)
    )
  }

  private fun CallTable.Call?.isAccepted() = this?.event == CallTable.Event.ACCEPTED

  private fun getAnswerType(call: CallTable.Call?, callInfo: CallInfoState?): CallAnswerType? {
    if (call == null || callInfo == null)
      return null
    if (call.isAccepted())
      return CallAnswerType.Answered
    if (callInfo.callState == WebRtcViewModel.State.RECIPIENT_UNAVAILABLE && call.event == CallTable.Event.ONGOING)
      return CallAnswerType.Missed
    if (callInfo.callState in REJECTED_STATES)
      return CallAnswerType.Reject
    return CallAnswerType.Missed
  }

  private fun WebRtcViewModel.State.answerType(): CallAnswerType {
    return when (this) {
      WebRtcViewModel.State.IDLE,
      WebRtcViewModel.State.CALL_PRE_JOIN -> CallAnswerType.Missed
      WebRtcViewModel.State.CALL_INCOMING -> CallAnswerType.Reject
      WebRtcViewModel.State.CALL_OUTGOING -> CallAnswerType.Missed
      WebRtcViewModel.State.CALL_CONNECTED -> CallAnswerType.Answered
      WebRtcViewModel.State.CALL_RINGING -> CallAnswerType.Missed

      WebRtcViewModel.State.CALL_BUSY -> CallAnswerType.Reject

      WebRtcViewModel.State.CALL_DISCONNECTED,
      WebRtcViewModel.State.CALL_DISCONNECTED_GLARE -> CallAnswerType.Missed

      WebRtcViewModel.State.CALL_NEEDS_PERMISSION -> CallAnswerType.Missed
      WebRtcViewModel.State.CALL_RECONNECTING -> CallAnswerType.Missed
      WebRtcViewModel.State.NETWORK_FAILURE -> CallAnswerType.Missed
      WebRtcViewModel.State.RECIPIENT_UNAVAILABLE -> CallAnswerType.Reject
      WebRtcViewModel.State.NO_SUCH_USER -> CallAnswerType.Reject
      WebRtcViewModel.State.UNTRUSTED_IDENTITY -> CallAnswerType.Reject
      WebRtcViewModel.State.CALL_ACCEPTED_ELSEWHERE -> CallAnswerType.Answered
      WebRtcViewModel.State.CALL_DECLINED_ELSEWHERE -> CallAnswerType.Reject
      WebRtcViewModel.State.CALL_ONGOING_ELSEWHERE -> CallAnswerType.Missed
    }
  }

  private fun MessageRecord.rtcMode(call: CallTable.Call?): CallRtcMode = call.rtcMode()
    ?: CallRtcMode.fromIsVideo(isGroupCall || isIncomingVideoCall || isOutgoingVideoCall || isMissedVideoCall)

  private fun CallTable.Call?.rtcMode(): CallRtcMode? =
    this?.let { CallRtcMode.fromIsVideo(type == CallTable.Type.VIDEO_CALL || type == CallTable.Type.GROUP_CALL || type == CallTable.Type.AD_HOC_CALL) }

  companion object {

    private val REJECTED_STATES = arrayOf(
      WebRtcViewModel.State.CALL_INCOMING,
      WebRtcViewModel.State.CALL_BUSY,
      WebRtcViewModel.State.RECIPIENT_UNAVAILABLE,
      WebRtcViewModel.State.UNTRUSTED_IDENTITY,
      WebRtcViewModel.State.CALL_DECLINED_ELSEWHERE,
    )
  }
}