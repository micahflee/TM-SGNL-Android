package org.tm.archive.database.model

import android.net.Uri
import org.signal.zkgroup.groups.GroupMasterKey
import org.signal.zkgroup.profiles.ProfileKeyCredential
import org.tm.archive.badges.models.Badge
import org.tm.archive.conversation.colors.AvatarColor
import org.tm.archive.conversation.colors.ChatColors
import org.tm.archive.database.IdentityDatabase.VerifiedStatus
import org.tm.archive.database.RecipientDatabase
import org.tm.archive.database.RecipientDatabase.InsightsBannerTier
import org.tm.archive.database.RecipientDatabase.MentionSetting
import org.tm.archive.database.RecipientDatabase.RegisteredState
import org.tm.archive.database.RecipientDatabase.UnidentifiedAccessMode
import org.tm.archive.database.RecipientDatabase.VibrateState
import org.tm.archive.groups.GroupId
import org.tm.archive.profiles.ProfileName
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.wallpaper.ChatWallpaper
import org.whispersystems.libsignal.util.guava.Optional
import org.whispersystems.signalservice.api.push.ACI
import org.whispersystems.signalservice.api.push.PNI

/**
 * Database model for [RecipientDatabase].
 */
data class RecipientRecord(
  val id: RecipientId,
  val aci: ACI?,
  val pni: PNI?,
  val username: String?,
  val e164: String?,
  val email: String?,
  val groupId: GroupId?,
  val groupType: RecipientDatabase.GroupType,
  val isBlocked: Boolean,
  val muteUntil: Long,
  val messageVibrateState: VibrateState,
  val callVibrateState: VibrateState,
  val messageRingtone: Uri?,
  val callRingtone: Uri?,
  private val defaultSubscriptionId: Int,
  val expireMessages: Int,
  val registered: RegisteredState,
  val profileKey: ByteArray?,
  val profileKeyCredential: ProfileKeyCredential?,
  val systemProfileName: ProfileName,
  val systemDisplayName: String?,
  val systemContactPhotoUri: String?,
  val systemPhoneLabel: String?,
  val systemContactUri: String?,
  @get:JvmName("getProfileName")
  val signalProfileName: ProfileName,
  @get:JvmName("getProfileAvatar")
  val signalProfileAvatar: String?,
  @get:JvmName("hasProfileImage")
  val hasProfileImage: Boolean,
  @get:JvmName("isProfileSharing")
  val profileSharing: Boolean,
  val lastProfileFetch: Long,
  val notificationChannel: String?,
  val unidentifiedAccessMode: UnidentifiedAccessMode,
  @get:JvmName("isForceSmsSelection")
  val forceSmsSelection: Boolean,
  val rawCapabilities: Long,
  val groupsV2Capability: Recipient.Capability,
  val groupsV1MigrationCapability: Recipient.Capability,
  val senderKeyCapability: Recipient.Capability,
  val announcementGroupCapability: Recipient.Capability,
  val changeNumberCapability: Recipient.Capability,
  val insightsBannerTier: InsightsBannerTier,
  val storageId: ByteArray?,
  val mentionSetting: MentionSetting,
  val wallpaper: ChatWallpaper?,
  val chatColors: ChatColors?,
  val avatarColor: AvatarColor,
  val about: String?,
  val aboutEmoji: String?,
  val syncExtras: SyncExtras,
  val extras: Recipient.Extras?,
  @get:JvmName("hasGroupsInCommon")
  val hasGroupsInCommon: Boolean,
  val badges: List<Badge>
) {

  fun getDefaultSubscriptionId(): Optional<Int> {
    return if (defaultSubscriptionId != -1) Optional.of(defaultSubscriptionId) else Optional.absent()
  }

  /**
   * A bundle of data that's only necessary when syncing to storage service, not for a
   * [Recipient].
   */
  data class SyncExtras(
    val storageProto: ByteArray?,
    val groupMasterKey: GroupMasterKey?,
    val identityKey: ByteArray?,
    val identityStatus: VerifiedStatus,
    val isArchived: Boolean,
    val isForcedUnread: Boolean
  )
}
