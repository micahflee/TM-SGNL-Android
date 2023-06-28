package org.tm.archive.testing

import org.signal.libsignal.zkgroup.groups.GroupMasterKey
import org.signal.storageservice.protos.groups.Member
import org.signal.storageservice.protos.groups.local.DecryptedGroup
import org.signal.storageservice.protos.groups.local.DecryptedMember
import org.tm.archive.database.SignalDatabase
import org.tm.archive.groups.GroupId
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.whispersystems.signalservice.api.push.ServiceId
import kotlin.random.Random

/**
 * Helper methods for creating groups for message processing tests et al.
 */
object GroupTestingUtils {
  fun member(serviceId: ServiceId, revision: Int = 0, role: Member.Role = Member.Role.ADMINISTRATOR): DecryptedMember {
    return DecryptedMember.newBuilder()
      .setUuid(serviceId.toByteString())
      .setJoinedAtRevision(revision)
      .setRole(role)
      .build()
  }

  fun insertGroup(revision: Int = 0, vararg members: DecryptedMember): TestGroupInfo {
    val groupMasterKey = GroupMasterKey(Random.nextBytes(GroupMasterKey.SIZE))
    val decryptedGroupState = DecryptedGroup.newBuilder()
      .addAllMembers(members.toList())
      .setRevision(revision)
      .setTitle(MessageContentFuzzer.string())
      .build()

    val groupId = SignalDatabase.groups.create(groupMasterKey, decryptedGroupState)!!
    val groupRecipientId = SignalDatabase.recipients.getOrInsertFromGroupId(groupId)
    SignalDatabase.recipients.setProfileSharing(groupRecipientId, true)

    return TestGroupInfo(groupId, groupMasterKey, groupRecipientId)
  }

  fun RecipientId.asMember(): DecryptedMember {
    return Recipient.resolved(this).asMember()
  }

  fun Recipient.asMember(): DecryptedMember {
    return member(serviceId = requireServiceId())
  }

  data class TestGroupInfo(val groupId: GroupId.V2, val masterKey: GroupMasterKey, val recipientId: RecipientId)
}
