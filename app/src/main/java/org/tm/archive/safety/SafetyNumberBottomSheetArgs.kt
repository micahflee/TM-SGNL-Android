package org.tm.archive.safety

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.tm.archive.contacts.paged.ContactSearchKey
import org.tm.archive.database.model.MessageId
import org.tm.archive.recipients.RecipientId

/**
 * Fragment argument for `SafetyNumberBottomSheetFragment`
 */
@Parcelize
data class SafetyNumberBottomSheetArgs(
  val untrustedRecipients: List<RecipientId>,
  val destinations: List<ContactSearchKey.ParcelableRecipientSearchKey>,
  val messageId: MessageId? = null
) : Parcelable
