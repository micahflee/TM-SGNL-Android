package org.tm.archive.profiles.edit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

import org.signal.core.util.StreamUtil;
import org.signal.core.util.logging.Log;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.groups.GroupChangeException;
import org.tm.archive.groups.GroupId;
import org.tm.archive.groups.GroupManager;
import org.tm.archive.profiles.AvatarHelper;
import org.tm.archive.profiles.ProfileName;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.concurrent.SimpleTask;
import org.whispersystems.libsignal.util.guava.Optional;

import java.io.IOException;

class EditGroupProfileRepository implements EditProfileRepository {

  private static final String TAG = Log.tag(EditGroupProfileRepository.class);

  private final Context context;
  private final GroupId groupId;

  EditGroupProfileRepository(@NonNull Context context, @NonNull GroupId groupId) {
    this.context = context.getApplicationContext();
    this.groupId = groupId;
  }

  @Override
  public void getCurrentProfileName(@NonNull Consumer<ProfileName> profileNameConsumer) {
    profileNameConsumer.accept(ProfileName.EMPTY);
  }

  @Override
  public void getCurrentAvatar(@NonNull Consumer<byte[]> avatarConsumer) {
    SimpleTask.run(() -> {
      final RecipientId recipientId = getRecipientId();

      if (AvatarHelper.hasAvatar(context, recipientId)) {
        try {
          return StreamUtil.readFully(AvatarHelper.getAvatar(context, recipientId));
        } catch (IOException e) {
          Log.w(TAG, e);
          return null;
        }
      } else {
        return null;
      }
    }, avatarConsumer::accept);
  }

  @Override
  public void getCurrentDisplayName(@NonNull Consumer<String> displayNameConsumer) {
    SimpleTask.run(() -> Recipient.resolved(getRecipientId()).getDisplayName(context), displayNameConsumer::accept);
  }

  @Override
  public void getCurrentName(@NonNull Consumer<String> nameConsumer) {
    SimpleTask.run(() -> {
      RecipientId recipientId = getRecipientId();
      Recipient   recipient   = Recipient.resolved(recipientId);

      return DatabaseFactory.getGroupDatabase(context)
                            .getGroup(recipientId)
                            .transform(groupRecord -> {
                              String title = groupRecord.getTitle();
                              return title == null ? "" : title;
                            })
                            .or(() -> recipient.getName(context));
    }, nameConsumer::accept);
  }

  @Override
  public void uploadProfile(@NonNull ProfileName profileName,
                            @NonNull String displayName,
                            boolean displayNameChanged,
                            @Nullable byte[] avatar,
                            boolean avatarChanged,
                            @NonNull Consumer<UploadResult> uploadResultConsumer)
  {
    SimpleTask.run(() -> {
      try {
        GroupManager.updateGroupDetails(context, groupId, avatar, avatarChanged, displayName, displayNameChanged);

        return UploadResult.SUCCESS;
      } catch (GroupChangeException | IOException e) {
        return UploadResult.ERROR_IO;
      }

    }, uploadResultConsumer::accept);
  }

  @Override
  public void getCurrentUsername(@NonNull Consumer<Optional<String>> callback) {
    callback.accept(Optional.absent());
  }

  @WorkerThread
  private RecipientId getRecipientId() {
    return DatabaseFactory.getRecipientDatabase(context).getByGroupId(groupId)
                          .or(() -> {
                            throw new AssertionError("Recipient ID for Group ID does not exist.");
                          });
  }
}
