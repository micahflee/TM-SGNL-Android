package org.tm.archive.components.settings.conversation

import android.content.Context
import android.database.Cursor
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.Log
import org.signal.storageservice.protos.groups.local.DecryptedGroup
import org.signal.storageservice.protos.groups.local.DecryptedPendingMember
import org.tm.archive.contacts.sync.DirectoryHelper
import org.tm.archive.database.GroupDatabase
import org.tm.archive.database.MediaDatabase
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.IdentityRecord
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.groups.GroupId
import org.tm.archive.groups.GroupManager
import org.tm.archive.groups.GroupProtoUtil
import org.tm.archive.groups.LiveGroup
import org.tm.archive.groups.ui.GroupChangeFailureReason
import org.tm.archive.jobs.RetrieveProfileJob
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.recipients.RecipientUtil
import org.tm.archive.util.FeatureFlags
import org.whispersystems.libsignal.util.guava.Optional
import java.io.IOException
import java.util.concurrent.TimeUnit

private val TAG = Log.tag(ConversationSettingsRepository::class.java)

class ConversationSettingsRepository(
  private val context: Context
) {

  @WorkerThread
  fun getThreadMedia(threadId: Long): Optional<Cursor> {
    return if (threadId <= 0) {
      Optional.absent()
    } else {
      Optional.of(SignalDatabase.media.getGalleryMediaForThread(threadId, MediaDatabase.Sorting.Newest))
    }
  }

  fun getThreadId(recipientId: RecipientId, consumer: (Long) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      consumer(SignalDatabase.threads.getThreadIdIfExistsFor(recipientId))
    }
  }

  fun getThreadId(groupId: GroupId, consumer: (Long) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val recipientId = Recipient.externalGroupExact(context, groupId).id
      consumer(SignalDatabase.threads.getThreadIdIfExistsFor(recipientId))
    }
  }

  fun isInternalRecipientDetailsEnabled(): Boolean = SignalStore.internalValues().recipientDetails()

  fun hasGroups(consumer: (Boolean) -> Unit) {
    SignalExecutors.BOUNDED.execute { consumer(SignalDatabase.groups.activeGroupCount > 0) }
  }

  fun getIdentity(recipientId: RecipientId, consumer: (IdentityRecord?) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      consumer(ApplicationDependencies.getProtocolStore().aci().identities().getIdentityRecord(recipientId).orNull())
    }
  }

  fun getGroupsInCommon(recipientId: RecipientId, consumer: (List<Recipient>) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      consumer(
        SignalDatabase
          .groups
          .getPushGroupsContainingMember(recipientId)
          .asSequence()
          .filter { it.members.contains(Recipient.self().id) }
          .map(GroupDatabase.GroupRecord::getRecipientId)
          .map(Recipient::resolved)
          .sortedBy { gr -> gr.getDisplayName(context) }
          .toList()
      )
    }
  }

  fun getGroupMembership(recipientId: RecipientId, consumer: (List<RecipientId>) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val groupDatabase = SignalDatabase.groups
      val groupRecords = groupDatabase.getPushGroupsContainingMember(recipientId)
      val groupRecipients = ArrayList<RecipientId>(groupRecords.size)
      for (groupRecord in groupRecords) {
        groupRecipients.add(groupRecord.recipientId)
      }
      consumer(groupRecipients)
    }
  }

  fun refreshRecipient(recipientId: RecipientId) {
    SignalExecutors.UNBOUNDED.execute {
      try {
        DirectoryHelper.refreshDirectoryFor(context, Recipient.resolved(recipientId), false)
      } catch (e: IOException) {
        Log.w(TAG, "Failed to refresh user after adding to contacts.")
      }
    }
  }

  fun setMuteUntil(recipientId: RecipientId, until: Long) {
    SignalExecutors.BOUNDED.execute {
      SignalDatabase.recipients.setMuted(recipientId, until)
    }
  }

  fun getGroupCapacity(groupId: GroupId, consumer: (GroupCapacityResult) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val groupRecord: GroupDatabase.GroupRecord = SignalDatabase.groups.getGroup(groupId).get()
      consumer(
        if (groupRecord.isV2Group) {
          val decryptedGroup: DecryptedGroup = groupRecord.requireV2GroupProperties().decryptedGroup
          val pendingMembers: List<RecipientId> = decryptedGroup.pendingMembersList
            .map(DecryptedPendingMember::getUuid)
            .map(GroupProtoUtil::uuidByteStringToRecipientId)

          val members = mutableListOf<RecipientId>()

          members.addAll(groupRecord.members)
          members.addAll(pendingMembers)

          GroupCapacityResult(Recipient.self().id, members, FeatureFlags.groupLimits(), groupRecord.isAnnouncementGroup)
        } else {
          GroupCapacityResult(Recipient.self().id, groupRecord.members, FeatureFlags.groupLimits(), false)
        }
      )
    }
  }

  fun addMembers(groupId: GroupId, selected: List<RecipientId>, consumer: (GroupAddMembersResult) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val record: GroupDatabase.GroupRecord = SignalDatabase.groups.getGroup(groupId).get()

      if (record.isAnnouncementGroup) {
        val needsResolve = selected
          .map { Recipient.resolved(it) }
          .filter { it.announcementGroupCapability != Recipient.Capability.SUPPORTED && !it.isSelf }
          .map { it.id }
          .toSet()

        ApplicationDependencies.getJobManager().runSynchronously(RetrieveProfileJob(needsResolve), TimeUnit.SECONDS.toMillis(10))

        val updatedWithCapabilities = needsResolve.map { Recipient.resolved(it) }

        if (updatedWithCapabilities.any { it.announcementGroupCapability != Recipient.Capability.SUPPORTED }) {
          consumer(GroupAddMembersResult.Failure(GroupChangeFailureReason.NOT_ANNOUNCEMENT_CAPABLE))
          return@execute
        }
      }

      consumer(
        try {
          val groupActionResult = GroupManager.addMembers(context, groupId.requirePush(), selected)
          GroupAddMembersResult.Success(groupActionResult.addedMemberCount, Recipient.resolvedList(groupActionResult.invitedMembers))
        } catch (e: Exception) {
          GroupAddMembersResult.Failure(GroupChangeFailureReason.fromException(e))
        }
      )
    }
  }

  fun setMuteUntil(groupId: GroupId, until: Long) {
    SignalExecutors.BOUNDED.execute {
      val recipientId = Recipient.externalGroupExact(context, groupId).id
      SignalDatabase.recipients.setMuted(recipientId, until)
    }
  }

  fun block(recipientId: RecipientId) {
    SignalExecutors.BOUNDED.execute {
      val recipient = Recipient.resolved(recipientId)
      if (recipient.isGroup) {
        RecipientUtil.block(context, recipient)
      } else {
        RecipientUtil.blockNonGroup(context, recipient)
      }
    }
  }

  fun unblock(recipientId: RecipientId) {
    SignalExecutors.BOUNDED.execute {
      val recipient = Recipient.resolved(recipientId)
      RecipientUtil.unblock(context, recipient)
    }
  }

  fun block(groupId: GroupId) {
    SignalExecutors.BOUNDED.execute {
      val recipient = Recipient.externalGroupExact(context, groupId)
      RecipientUtil.block(context, recipient)
    }
  }

  fun unblock(groupId: GroupId) {
    SignalExecutors.BOUNDED.execute {
      val recipient = Recipient.externalGroupExact(context, groupId)
      RecipientUtil.unblock(context, recipient)
    }
  }

  @WorkerThread
  fun isMessageRequestAccepted(recipient: Recipient): Boolean {
    return RecipientUtil.isMessageRequestAccepted(context, recipient)
  }

  fun getMembershipCountDescription(liveGroup: LiveGroup): LiveData<String> {
    return liveGroup.getMembershipCountDescription(context.resources)
  }

  fun getExternalPossiblyMigratedGroupRecipientId(groupId: GroupId, consumer: (RecipientId) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      consumer(Recipient.externalPossiblyMigratedGroup(context, groupId).id)
    }
  }
}
