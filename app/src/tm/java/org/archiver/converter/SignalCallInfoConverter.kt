package org.archiver.converter

import com.tm.androidcopysdk.model.ArchiveCallInfo
import com.tm.androidcopysdk.model.ArchiveMessageType
import com.tm.androidcopysdk.model.CallAnswerType
import com.tm.androidcopysdk.model.CallRtcMode
import org.tm.archive.database.CallTable
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.MmsMessageRecord

class SignalCallInfoConverter {

  fun convert(message: MessageRecord, type: ArchiveMessageType, startedAt: Long?): ArchiveCallInfo? {
    if (type != ArchiveMessageType.Call && type != ArchiveMessageType.Unknown)
      return null
    val durationMs = startedAt?.takeIf { it > 0 }?.let { System.currentTimeMillis() - it } ?: 0
    val call = (message as? MmsMessageRecord)?.call
    return ArchiveCallInfo(
      id = call?.callId?.toString(),
      startedAt = startedAt,
      durationMs = durationMs / 1000,
      answerType = message.answerType(call),
      rtcMode = message.rtcMode(call)
    )
  }

  private fun MessageRecord.rtcMode(call: CallTable.Call?): CallRtcMode = call.rtcMode()
    ?: CallRtcMode.fromIsVideo(isGroupCall || isIncomingVideoCall || isOutgoingVideoCall || isMissedVideoCall)

  private fun CallTable.Call?.rtcMode(): CallRtcMode? =
    this?.let { CallRtcMode.fromIsVideo(type == CallTable.Type.VIDEO_CALL || type == CallTable.Type.GROUP_CALL || type == CallTable.Type.AD_HOC_CALL) }

  private fun MessageRecord.answerType(call: CallTable.Call?): CallAnswerType? {
    val callAnswerType = call.answerType()
    if (callAnswerType != null)
      return callAnswerType
    if (isMissedAudioCall || isMissedVideoCall)
      return CallAnswerType.Missed
    if (isJoined)
      return CallAnswerType.Answered
    return null
  }

  private fun CallTable.Call?.answerType(): CallAnswerType? {
    if (this == null)
      return null
    return when (event) {
      CallTable.Event.ONGOING -> null
      CallTable.Event.ACCEPTED -> CallAnswerType.Answered
      CallTable.Event.NOT_ACCEPTED -> CallAnswerType.Missed
      CallTable.Event.MISSED,
      CallTable.Event.MISSED_NOTIFICATION_PROFILE -> CallAnswerType.Missed

      CallTable.Event.DELETE -> null
      CallTable.Event.GENERIC_GROUP_CALL -> CallAnswerType.Missed
      CallTable.Event.JOINED -> CallAnswerType.Answered
      CallTable.Event.RINGING -> null
      CallTable.Event.DECLINED -> CallAnswerType.Reject
      CallTable.Event.OUTGOING_RING -> null
    }
  }
}