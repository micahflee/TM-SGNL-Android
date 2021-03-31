package org.tm.archive.groups.ui.addtogroup;

import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.groups.GroupChangeException;
import org.tm.archive.groups.GroupId;
import org.tm.archive.groups.GroupManager;
import org.tm.archive.groups.MembershipNotSuitableForV2Exception;
import org.tm.archive.groups.ui.GroupChangeErrorCallback;
import org.tm.archive.groups.ui.GroupChangeFailureReason;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;

import java.io.IOException;
import java.util.Collections;

final class AddToGroupRepository {

  private static final String TAG = Log.tag(AddToGroupRepository.class);

  private final Context context;

  AddToGroupRepository() {
    this.context = ApplicationDependencies.getApplication();
  }

  public void add(@NonNull RecipientId recipientId,
                  @NonNull Recipient groupRecipient,
                  @NonNull GroupChangeErrorCallback error,
                  @NonNull Runnable success)
  {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupId.Push pushGroupId = groupRecipient.requireGroupId().requirePush();

        GroupManager.addMembers(context, pushGroupId, Collections.singletonList(recipientId));

        success.run();
        } catch (GroupChangeException | MembershipNotSuitableForV2Exception | IOException e) {
        Log.w(TAG, e);
        error.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }
}
