package org.tm.archive.groups.ui.invitesandrequests.invite;

import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.SignalExecutors;
import org.tm.archive.groups.GroupChangeBusyException;
import org.tm.archive.groups.GroupChangeFailedException;
import org.tm.archive.groups.GroupId;
import org.tm.archive.groups.GroupInsufficientRightsException;
import org.tm.archive.groups.GroupManager;
import org.tm.archive.groups.GroupNotAMemberException;
import org.tm.archive.groups.v2.GroupInviteLinkUrl;
import org.tm.archive.util.AsynchronousCallback;

import java.io.IOException;

final class GroupLinkInviteRepository {

  private final Context    context;
  private final GroupId.V2 groupId;

  GroupLinkInviteRepository(@NonNull Context context, @NonNull GroupId.V2 groupId) {
    this.context = context;
    this.groupId = groupId;
  }

  void enableGroupInviteLink(boolean requireMemberApproval, @NonNull AsynchronousCallback.WorkerThread<GroupInviteLinkUrl, EnableInviteLinkError> callback) {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupInviteLinkUrl groupInviteLinkUrl = GroupManager.setGroupLinkEnabledState(context,
                                                                                      groupId,
                                                                                      requireMemberApproval ? GroupManager.GroupLinkState.ENABLED_WITH_APPROVAL
                                                                                                            : GroupManager.GroupLinkState.ENABLED);

        if (groupInviteLinkUrl == null) {
          throw new AssertionError();
        }

        callback.onComplete(groupInviteLinkUrl);
      } catch (IOException e) {
        callback.onError(EnableInviteLinkError.NETWORK_ERROR);
      } catch (GroupChangeBusyException e) {
        callback.onError(EnableInviteLinkError.BUSY);
      } catch (GroupChangeFailedException e) {
        callback.onError(EnableInviteLinkError.FAILED);
      } catch (GroupInsufficientRightsException e) {
        callback.onError(EnableInviteLinkError.INSUFFICIENT_RIGHTS);
      } catch (GroupNotAMemberException e) {
        callback.onError(EnableInviteLinkError.NOT_IN_GROUP);
      }
    });
  }
}
