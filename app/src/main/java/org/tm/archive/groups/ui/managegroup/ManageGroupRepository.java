package org.tm.archive.groups.ui.managegroup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

import com.annimon.stream.Stream;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.signal.storageservice.protos.groups.local.DecryptedGroup;
import org.tm.archive.ContactSelectionListFragment;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.database.GroupDatabase;
import org.tm.archive.database.RecipientDatabase;
import org.tm.archive.database.ThreadDatabase;
import org.tm.archive.groups.GroupAccessControl;
import org.tm.archive.groups.GroupChangeException;
import org.tm.archive.groups.GroupId;
import org.tm.archive.groups.GroupManager;
import org.tm.archive.groups.GroupProtoUtil;
import org.tm.archive.groups.MembershipNotSuitableForV2Exception;
import org.tm.archive.groups.SelectionLimits;
import org.tm.archive.groups.ui.GroupChangeErrorCallback;
import org.tm.archive.groups.ui.GroupChangeFailureReason;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.recipients.RecipientUtil;
import org.tm.archive.util.AsynchronousCallback;
import org.tm.archive.util.FeatureFlags;
import org.tm.archive.util.concurrent.SimpleTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

final class ManageGroupRepository {

  private static final String TAG = Log.tag(ManageGroupRepository.class);

  private final Context context;

  ManageGroupRepository(@NonNull Context context) {
    this.context = context;
  }

  void getGroupState(@NonNull GroupId groupId, @NonNull Consumer<GroupStateResult> onGroupStateLoaded) {
    SignalExecutors.BOUNDED.execute(() -> onGroupStateLoaded.accept(getGroupState(groupId)));
  }

  void getGroupCapacity(@NonNull GroupId groupId, @NonNull Consumer<GroupCapacityResult> onGroupCapacityLoaded) {
    SimpleTask.run(SignalExecutors.BOUNDED, () -> {
      GroupDatabase.GroupRecord groupRecord = DatabaseFactory.getGroupDatabase(context).getGroup(groupId).get();
      if (groupRecord.isV2Group()) {
        DecryptedGroup    decryptedGroup = groupRecord.requireV2GroupProperties().getDecryptedGroup();
        List<RecipientId> pendingMembers = Stream.of(decryptedGroup.getPendingMembersList())
                                                 .map(member -> GroupProtoUtil.uuidByteStringToRecipientId(member.getUuid()))
                                                 .toList();
        List<RecipientId> members        = new LinkedList<>(groupRecord.getMembers());

        members.addAll(pendingMembers);

        return new GroupCapacityResult(members, FeatureFlags.groupLimits());
      } else {
        return new GroupCapacityResult(groupRecord.getMembers(), FeatureFlags.groupLimits());
      }
    }, onGroupCapacityLoaded::accept);
  }

  @WorkerThread
  private GroupStateResult getGroupState(@NonNull GroupId groupId) {
    ThreadDatabase threadDatabase = DatabaseFactory.getThreadDatabase(context);
    Recipient      groupRecipient = Recipient.externalGroupExact(context, groupId);
    long           threadId       = threadDatabase.getThreadIdFor(groupRecipient);

    return new GroupStateResult(threadId, groupRecipient);
  }

  void setExpiration(@NonNull GroupId groupId, int newExpirationTime, @NonNull GroupChangeErrorCallback error) {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupManager.updateGroupTimer(context, groupId.requirePush(), newExpirationTime);
      } catch (GroupChangeException | IOException e) {
        Log.w(TAG, e);
        error.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }

  void applyMembershipRightsChange(@NonNull GroupId groupId, @NonNull GroupAccessControl newRights, @NonNull GroupChangeErrorCallback error) {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupManager.applyMembershipAdditionRightsChange(context, groupId.requireV2(), newRights);
      } catch (GroupChangeException | IOException e) {
        Log.w(TAG, e);
        error.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }

  void applyAttributesRightsChange(@NonNull GroupId groupId, @NonNull GroupAccessControl newRights, @NonNull GroupChangeErrorCallback error) {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupManager.applyAttributesRightsChange(context, groupId.requireV2(), newRights);
      } catch (GroupChangeException | IOException e) {
        Log.w(TAG, e);
        error.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }

  public void getRecipient(@NonNull GroupId groupId, @NonNull Consumer<Recipient> recipientCallback) {
    SimpleTask.run(SignalExecutors.BOUNDED,
                   () -> Recipient.externalGroupExact(context, groupId),
                   recipientCallback::accept);
  }

  void setMuteUntil(@NonNull GroupId groupId, long until) {
    SignalExecutors.BOUNDED.execute(() -> {
      RecipientId recipientId = Recipient.externalGroupExact(context, groupId).getId();
      DatabaseFactory.getRecipientDatabase(context).setMuted(recipientId, until);
    });
  }

  void addMembers(@NonNull GroupId groupId,
                  @NonNull List<RecipientId> selected,
                  @NonNull AsynchronousCallback.WorkerThread<ManageGroupViewModel.AddMembersResult, GroupChangeFailureReason> callback)
  {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupManager.GroupActionResult groupActionResult = GroupManager.addMembers(context, groupId.requirePush(), selected);
        callback.onComplete(new ManageGroupViewModel.AddMembersResult(groupActionResult.getAddedMemberCount(), Recipient.resolvedList(groupActionResult.getInvitedMembers())));
      } catch (GroupChangeException | MembershipNotSuitableForV2Exception | IOException e) {
        Log.w(TAG, e);
        callback.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }

  void blockAndLeaveGroup(@NonNull GroupId groupId, @NonNull GroupChangeErrorCallback error, @NonNull Runnable onSuccess) {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        RecipientUtil.block(context, Recipient.externalGroupExact(context, groupId));
        onSuccess.run();
      } catch (GroupChangeException | IOException e) {
        Log.w(TAG, e);
        error.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }

  void setMentionSetting(@NonNull GroupId groupId, RecipientDatabase.MentionSetting mentionSetting) {
    SignalExecutors.BOUNDED.execute(() -> {
      RecipientId recipientId = Recipient.externalGroupExact(context, groupId).getId();
      DatabaseFactory.getRecipientDatabase(context).setMentionSetting(recipientId, mentionSetting);
    });
  }

  static final class GroupStateResult {

    private final long      threadId;
    private final Recipient recipient;

    private GroupStateResult(long threadId,
                             Recipient recipient)
    {
      this.threadId  = threadId;
      this.recipient = recipient;
    }

    long getThreadId() {
      return threadId;
    }

    Recipient getRecipient() {
      return recipient;
    }
  }

  static final class GroupCapacityResult {
    private final List<RecipientId> members;
    private final SelectionLimits selectionLimits;

    GroupCapacityResult(@NonNull List<RecipientId> members, @NonNull SelectionLimits selectionLimits) {
      this.members         = members;
      this.selectionLimits = selectionLimits;
    }

    public @NonNull List<RecipientId> getMembers() {
      return members;
    }

    public int getSelectionLimit() {
      if (!selectionLimits.hasHardLimit()) {
        return ContactSelectionListFragment.NO_LIMIT;
      }

      boolean containsSelf = members.indexOf(Recipient.self().getId()) != -1;

      return selectionLimits.getHardLimit() - (containsSelf ? 1 : 0);
    }

    public int getSelectionWarning() {
      if (!selectionLimits.hasRecommendedLimit()) {
        return ContactSelectionListFragment.NO_LIMIT;
      }

      boolean containsSelf = members.indexOf(Recipient.self().getId()) != -1;

      return selectionLimits.getRecommendedLimit() - (containsSelf ? 1 : 0);
    }

    public int getRemainingCapacity() {
      return selectionLimits.getHardLimit() - members.size();
    }

    public @NonNull List<RecipientId> getMembersWithoutSelf() {
      ArrayList<RecipientId> recipientIds = new ArrayList<>(members.size());
      RecipientId            selfId       = Recipient.self().getId();

      for (RecipientId recipientId : members) {
        if (!recipientId.equals(selfId)) {
          recipientIds.add(recipientId);
        }
      }

      return recipientIds;
    }
  }

}
