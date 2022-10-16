package org.tm.archive.profiles.edit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

import org.signal.core.util.StreamUtil;
import org.signal.core.util.logging.Log;
import org.tm.archive.conversation.colors.AvatarColor;
import org.tm.archive.database.GroupDatabase;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.groups.GroupChangeException;
import org.tm.archive.groups.GroupId;
import org.tm.archive.groups.GroupManager;
import org.tm.archive.profiles.AvatarHelper;
import org.tm.archive.profiles.ProfileName;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.signal.core.util.concurrent.SimpleTask;

import java.io.IOException;
import java.util.Optional;

class EditGroupProfileRepository implements EditProfileRepository {

  private static final String TAG = Log.tag(EditGroupProfileRepository.class);

  private final Context context;
  private final GroupId groupId;

  EditGroupProfileRepository(@NonNull Context context, @NonNull GroupId groupId) {
    this.context = context.getApplicationContext();
    this.groupId = groupId;
  }

  @Override
  public void getCurrentAvatarColor(@NonNull Consumer<AvatarColor> avatarColorConsumer) {
    SimpleTask.run(() -> Recipient.resolved(getRecipientId()).getAvatarColor(), avatarColorConsumer::accept);
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

      return SignalDatabase.groups()
                           .getGroup(recipientId)
                           .map(groupRecord -> {
                              String title = groupRecord.getTitle();
                              return title == null ? "" : title;
                            })
                           .orElseGet(() -> recipient.getGroupName(context));
    }, nameConsumer::accept);
  }

  @Override
  public void getCurrentDescription(@NonNull Consumer<String> descriptionConsumer) {
    SimpleTask.run(() -> {
      RecipientId recipientId = getRecipientId();

      return SignalDatabase.groups()
                           .getGroup(recipientId)
                           .map(GroupDatabase.GroupRecord::getDescription)
                           .orElse("");
    }, descriptionConsumer::accept);
  }

  @Override
  public void uploadProfile(@NonNull ProfileName profileName,
                            @NonNull String displayName,
                            boolean displayNameChanged,
                            @NonNull String description,
                            boolean descriptionChanged,
                            @Nullable byte[] avatar,
                            boolean avatarChanged,
                            @NonNull Consumer<UploadResult> uploadResultConsumer)
  {
    SimpleTask.run(() -> {
      try {
        GroupManager.updateGroupDetails(context, groupId, avatar, avatarChanged, displayName, displayNameChanged, description, descriptionChanged);

        return UploadResult.SUCCESS;
      } catch (GroupChangeException | IOException e) {
        return UploadResult.ERROR_IO;
      }

    }, uploadResultConsumer::accept);
  }

  @Override
  public void getCurrentUsername(@NonNull Consumer<Optional<String>> callback) {
    callback.accept(Optional.empty());
  }

  @WorkerThread
  private RecipientId getRecipientId() {
    return SignalDatabase.recipients().getByGroupId(groupId)
                         .orElseThrow(() -> new AssertionError("Recipient ID for Group ID does not exist."));
  }
}
