package org.tm.archive.groups.ui.chooseadmin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.tm.archive.groups.GroupChangeException;
import org.tm.archive.groups.GroupId;
import org.tm.archive.groups.GroupManager;
import org.tm.archive.groups.ui.GroupChangeFailureReason;
import org.tm.archive.groups.ui.GroupChangeResult;
import org.tm.archive.recipients.RecipientId;

import java.io.IOException;
import java.util.List;

public final class ChooseNewAdminRepository {
  private final Application context;

  ChooseNewAdminRepository(@NonNull Application context) {
    this.context = context;
  }

  @WorkerThread
  @NonNull GroupChangeResult updateAdminsAndLeave(@NonNull GroupId.V2 groupId, @NonNull List<RecipientId> newAdminIds) {
    try {
      GroupManager.addMemberAdminsAndLeaveGroup(context, groupId, newAdminIds);
      return GroupChangeResult.SUCCESS;
    } catch (GroupChangeException | IOException e) {
      return GroupChangeResult.failure(GroupChangeFailureReason.fromException(e));
    }
  }
}
