package org.archiver.model

data class TeleMessage3dPartyCall(
  val callId: Long,

  val direction: CallDirection,

  var phoneNumber: String? = null,

  var name: String? = null,

  var durationMs: Long? = null,

  var answerType: CallAnswerType? = null,

  var rtcMode: CallRtcMode = CallRtcMode.Voice,

  ) {

  fun type(): Int {
    val isOutgoing = direction == CallDirection.Outgoing
    return if (durationMs.let { it != null && it > 0 }) {
      if (isOutgoing) OUTGOING_TYPE else INCOMING_TYPE
    } else if (isOutgoing) {
      if (answerType == CallAnswerType.Missed) MISSED_CALL_OUTGOING_TYPE else REJECTED_TYPE_OUTGOING_TYPE
    } else {
      if (answerType == CallAnswerType.Missed) DISCARD_REASON_MISSED else DISCARD_REASON_REJECTED
    }
  }

  companion object {
    const val INCOMING_TYPE = 1
    const val OUTGOING_TYPE = 2
    const val DISCARD_REASON_MISSED = 3
    const val DISCARD_REASON_LINE_BUSY = 4
    const val DISCARD_REASON_REJECTED = 5
    const val MISSED_CALL_OUTGOING_TYPE = 8
    const val REJECTED_TYPE_OUTGOING_TYPE = 9
  }
}