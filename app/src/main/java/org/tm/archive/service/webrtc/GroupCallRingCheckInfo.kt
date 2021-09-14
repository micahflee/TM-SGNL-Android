package org.tm.archive.service.webrtc

import org.signal.ringrtc.CallManager
import org.tm.archive.groups.GroupId
import org.tm.archive.recipients.RecipientId
import java.util.UUID

data class GroupCallRingCheckInfo(
  val recipientId: RecipientId,
  val groupId: GroupId.V2,
  val ringId: Long,
  val ringerUuid: UUID,
  val ringUpdate: CallManager.RingUpdate
)
