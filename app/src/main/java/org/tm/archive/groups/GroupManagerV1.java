package org.tm.archive.groups;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import org.signal.core.util.logging.Log;
import org.tm.archive.attachments.Attachment;
import org.tm.archive.attachments.UriAttachment;
import org.tm.archive.database.AttachmentTable;
import org.tm.archive.database.GroupTable;
import org.tm.archive.database.RecipientTable;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.database.ThreadTable;
import org.tm.archive.groups.GroupManager.GroupActionResult;
import org.tm.archive.mms.MessageGroupContext;
import org.tm.archive.mms.OutgoingMessage;
import org.tm.archive.profiles.AvatarHelper;
import org.tm.archive.providers.BlobProvider;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.sms.MessageSender;
import org.tm.archive.util.MediaUtil;
import org.whispersystems.signalservice.internal.push.GroupContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import okio.ByteString;

final class GroupManagerV1 {

  private static final String TAG = Log.tag(GroupManagerV1.class);

  static @NonNull GroupActionResult createGroup(@NonNull Context          context,
                                                @NonNull Set<RecipientId> memberIds,
                                                @Nullable byte[]          avatarBytes,
                                                @Nullable String          name,
                                                          boolean         mms)
  {
    final GroupTable   groupDatabase = SignalDatabase.groups();
    final SecureRandom secureRandom  = new SecureRandom();
    final GroupId       groupId          = mms ? GroupId.createMms(secureRandom) : GroupId.createV1(secureRandom);
    final RecipientId   groupRecipientId = SignalDatabase.recipients().getOrInsertFromGroupId(groupId);
    final Recipient     groupRecipient   = Recipient.resolved(groupRecipientId);

    memberIds.add(Recipient.self().getId());

    if (groupId.isV1()) {
      GroupId.V1 groupIdV1 = groupId.requireV1();

      groupDatabase.create(groupIdV1, name, memberIds, null);

      try {
        AvatarHelper.setAvatar(context, groupRecipientId, avatarBytes != null ? new ByteArrayInputStream(avatarBytes) : null);
      } catch (IOException e) {
        Log.w(TAG, "Failed to save avatar!", e);
      }
      groupDatabase.onAvatarUpdated(groupIdV1, avatarBytes != null);
      SignalDatabase.recipients().setProfileSharing(groupRecipient.getId(), true);
      return sendGroupUpdate(context, groupIdV1, memberIds, name, avatarBytes, memberIds.size() - 1);
    } else {
      groupDatabase.create(groupId.requireMms(), name, memberIds);

      try {
        AvatarHelper.setAvatar(context, groupRecipientId, avatarBytes != null ? new ByteArrayInputStream(avatarBytes) : null);
      } catch (IOException e) {
        Log.w(TAG, "Failed to save avatar!", e);
      }
      groupDatabase.onAvatarUpdated(groupId, avatarBytes != null);

      long threadId = SignalDatabase.threads().getOrCreateThreadIdFor(groupRecipient, ThreadTable.DistributionTypes.CONVERSATION);
      return new GroupActionResult(groupRecipient, threadId, memberIds.size() - 1, Collections.emptyList());
    }
  }

  static GroupActionResult updateGroup(@NonNull  Context          context,
                                       @NonNull  GroupId          groupId,
                                       @NonNull  Set<RecipientId> memberAddresses,
                                       @Nullable byte[]           avatarBytes,
                                       @Nullable String           name,
                                                 int              newMemberCount)
  {
    final GroupTable  groupDatabase    = SignalDatabase.groups();
    final RecipientId groupRecipientId = SignalDatabase.recipients().getOrInsertFromGroupId(groupId);

    memberAddresses.add(Recipient.self().getId());
    groupDatabase.updateMembers(groupId, new LinkedList<>(memberAddresses));

    if (groupId.isPush()) {
      GroupId.V1 groupIdV1 = groupId.requireV1();

      groupDatabase.updateTitle(groupIdV1, name);
      groupDatabase.onAvatarUpdated(groupIdV1, avatarBytes != null);

      try {
        AvatarHelper.setAvatar(context, groupRecipientId, avatarBytes != null ? new ByteArrayInputStream(avatarBytes) : null);
      } catch (IOException e) {
        Log.w(TAG, "Failed to save avatar!", e);
      }
      return sendGroupUpdate(context, groupIdV1, memberAddresses, name, avatarBytes, newMemberCount);
    } else {
      Recipient   groupRecipient   = Recipient.resolved(groupRecipientId);
      long        threadId         = SignalDatabase.threads().getOrCreateThreadIdFor(groupRecipient);
      return new GroupActionResult(groupRecipient, threadId, newMemberCount, Collections.emptyList());
    }
  }

  static GroupActionResult updateGroup(@NonNull  Context     context,
                                       @NonNull  GroupId.Mms groupId,
                                       @Nullable byte[]      avatarBytes,
                                       @Nullable String      name)
  {
    GroupTable  groupDatabase    = SignalDatabase.groups();
    RecipientId groupRecipientId = SignalDatabase.recipients().getOrInsertFromGroupId(groupId);
    Recipient     groupRecipient   = Recipient.resolved(groupRecipientId);
    long          threadId         = SignalDatabase.threads().getOrCreateThreadIdFor(groupRecipient);

    groupDatabase.updateTitle(groupId, name);
    groupDatabase.onAvatarUpdated(groupId, avatarBytes != null);

    try {
      AvatarHelper.setAvatar(context, groupRecipientId, avatarBytes != null ? new ByteArrayInputStream(avatarBytes) : null);
    } catch (IOException e) {
      Log.w(TAG, "Failed to save avatar!", e);
    }

    return new GroupActionResult(groupRecipient, threadId, 0, Collections.emptyList());
  }

  private static GroupActionResult sendGroupUpdate(@NonNull Context context,
                                                   @NonNull GroupId.V1 groupId,
                                                   @NonNull Set<RecipientId> members,
                                                   @Nullable String groupName,
                                                   @Nullable byte[] avatar,
                                                   int newMemberCount)
  {
    Attachment  avatarAttachment = null;
    RecipientId groupRecipientId = SignalDatabase.recipients().getOrInsertFromGroupId(groupId);
    Recipient   groupRecipient   = Recipient.resolved(groupRecipientId);

    List<GroupContext.Member> uuidMembers = new ArrayList<>(members.size());
    List<String>              e164Members = new ArrayList<>(members.size());

    for (RecipientId member : members) {
      Recipient recipient = Recipient.resolved(member);
      if (recipient.hasE164()) {
        e164Members.add(recipient.requireE164());
      }
    }

    GroupContext.Builder groupContextBuilder = new GroupContext.Builder()
                                                               .id(ByteString.of(groupId.getDecodedId()))
                                                               .type(GroupContext.Type.UPDATE)
                                                               .membersE164(e164Members)
                                                               .members(uuidMembers);

    if (groupName != null) groupContextBuilder.name(groupName);

    GroupContext groupContext = groupContextBuilder.build();

    if (avatar != null) {
      Uri avatarUri = BlobProvider.getInstance().forData(avatar).createForSingleUseInMemory();
      avatarAttachment = new UriAttachment(avatarUri, MediaUtil.IMAGE_PNG, AttachmentTable.TRANSFER_PROGRESS_DONE, avatar.length, null, false, false, false, false, null, null, null, null, null);
    }

    OutgoingMessage outgoingMessage = OutgoingMessage.groupUpdateMessage(groupRecipient,
                                                                         new MessageGroupContext(groupContext),
                                                                         avatarAttachment != null ? Collections.singletonList(avatarAttachment) : Collections.emptyList(),
                                                                         System.currentTimeMillis(),
                                                                         0,
                                                                         false,
                                                                         null,
                                                                         Collections.emptyList(),
                                                                         Collections.emptyList(),
                                                                         Collections.emptyList());

    long                      threadId        = MessageSender.send(context, outgoingMessage, -1, MessageSender.SendType.SIGNAL, null, null);

    return new GroupActionResult(groupRecipient, threadId, newMemberCount, Collections.emptyList());
  }

  @WorkerThread
  static boolean leaveGroup(@NonNull Context context, @NonNull GroupId.V1 groupId) {
    return false;
  }

  @WorkerThread
  static boolean silentLeaveGroup(@NonNull Context context, @NonNull GroupId.V1 groupId) {
    return false;
  }

  @WorkerThread
  static void updateGroupTimer(@NonNull Context context, @NonNull GroupId.V1 groupId, int expirationTime) {
    RecipientTable recipientTable = SignalDatabase.recipients();
    ThreadTable    threadTable    = SignalDatabase.threads();
    Recipient      recipient      = Recipient.externalGroupExact(groupId);
    long           threadId       = threadTable.getOrCreateThreadIdFor(recipient);

    recipientTable.setExpireMessages(recipient.getId(), expirationTime);
    OutgoingMessage outgoingMessage = OutgoingMessage.expirationUpdateMessage(recipient, System.currentTimeMillis(), expirationTime * 1000L);
    MessageSender.send(context, outgoingMessage, threadId, MessageSender.SendType.SIGNAL, null, null);
  }

  @WorkerThread
  private static Optional<OutgoingMessage> createGroupLeaveMessage(@NonNull Context context,
                                                                   @NonNull GroupId.V1 groupId,
                                                                   @NonNull Recipient groupRecipient)
  {
    GroupTable groupDatabase = SignalDatabase.groups();

    if (!groupDatabase.isActive(groupId)) {
      Log.w(TAG, "Group has already been left.");
      return Optional.empty();
    }

    return Optional.empty();
  }
}
