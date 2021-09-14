package org.tm.archive.components.settings.conversation

import org.tm.archive.groups.ui.GroupChangeFailureReason
import org.tm.archive.recipients.Recipient

sealed class GroupAddMembersResult {
  class Success(
    val numberOfMembersAdded: Int,
    val newMembersInvited: List<Recipient>
  ) : GroupAddMembersResult()

  class Failure(
    val reason: GroupChangeFailureReason
  ) : GroupAddMembersResult()
}
