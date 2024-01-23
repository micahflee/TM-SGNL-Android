package org.tm.archive.mediasend

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.signal.core.util.getParcelableExtraCompat
import org.tm.archive.conversation.MessageSendType
import org.tm.archive.database.model.Mention
import org.tm.archive.database.model.StoryType
import org.tm.archive.database.model.databaseprotos.BodyRangeList
import org.tm.archive.recipients.RecipientId
import org.tm.archive.sms.MessageSender.PreUploadResult
import org.tm.archive.util.ParcelUtil

/**
 * A class that lets us nicely format data that we'll send back to [ConversationActivity].
 */
@Parcelize
class MediaSendActivityResult(
  val recipientId: RecipientId,
  val preUploadResults: List<PreUploadResult> = emptyList(),
  val nonUploadedMedia: List<Media> = emptyList(),
  val body: String,
  val messageSendType: MessageSendType,
  val isViewOnce: Boolean,
  val mentions: List<Mention>,
  @TypeParceler<BodyRangeList?, BodyRangeListParceler>() val bodyRanges: BodyRangeList?,
  val storyType: StoryType,
  val scheduledTime: Long = -1
) : Parcelable {

  val isPushPreUpload: Boolean
    get() = preUploadResults.isNotEmpty()

  init {
    require((preUploadResults.isNotEmpty() && nonUploadedMedia.isEmpty()) || (preUploadResults.isEmpty() && nonUploadedMedia.isNotEmpty()))
  }

  companion object {
    const val EXTRA_RESULT = "result"

    @JvmStatic
    fun fromData(data: Intent): MediaSendActivityResult {
      return data.getParcelableExtraCompat(EXTRA_RESULT, MediaSendActivityResult::class.java) ?: throw IllegalArgumentException()
    }
  }
}

object BodyRangeListParceler : Parceler<BodyRangeList?> {
  override fun create(parcel: Parcel): BodyRangeList? {
    val data: ByteArray? = ParcelUtil.readByteArray(parcel)
    return if (data != null) {
      BodyRangeList.ADAPTER.decode(data)
    } else {
      null
    }
  }

  override fun BodyRangeList?.write(parcel: Parcel, flags: Int) {
    ParcelUtil.writeByteArray(parcel, this?.encode())
  }
}
