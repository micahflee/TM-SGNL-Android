package org.tm.archive.contacts.paged

import android.content.Context
import android.database.Cursor
import org.signal.core.util.CursorUtil
import org.tm.archive.R
import org.tm.archive.contacts.ContactRepository
import org.tm.archive.contacts.paged.collections.ContactSearchIterator
import org.tm.archive.database.DistributionListDatabase
import org.tm.archive.database.GroupDatabase
import org.tm.archive.database.GroupDatabase.GroupRecord
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.ThreadDatabase
import org.tm.archive.database.model.DistributionListPrivacyMode
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.keyvalue.StorySend
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId

/**
 * Database boundary interface which allows us to safely unit test the data source without
 * having to deal with database access.
 */
open class ContactSearchPagedDataSourceRepository(
  private val context: Context
) {

  private val contactRepository = ContactRepository(context, context.getString(R.string.note_to_self))

  open fun getLatestStorySends(activeStoryCutoffDuration: Long): List<StorySend> {
    return SignalStore.storyValues()
      .getLatestActiveStorySendTimestamps(System.currentTimeMillis() - activeStoryCutoffDuration)
  }

  open fun querySignalContacts(query: String?, includeSelf: Boolean): Cursor? {
    return contactRepository.querySignalContacts(query ?: "", includeSelf)
  }

  open fun queryNonSignalContacts(query: String?): Cursor? {
    return contactRepository.queryNonSignalContacts(query ?: "")
  }

  open fun queryNonGroupContacts(query: String?, includeSelf: Boolean): Cursor? {
    return contactRepository.queryNonGroupContacts(query ?: "", includeSelf)
  }

  open fun getGroupSearchIterator(
    section: ContactSearchConfiguration.Section.Groups,
    query: String?
  ): ContactSearchIterator<GroupRecord> {
    return SignalDatabase.groups.queryGroups(
      GroupDatabase.GroupQuery.Builder()
        .withSearchQuery(query)
        .withInactiveGroups(section.includeInactive)
        .withMmsGroups(section.includeMms)
        .withV1Groups(section.includeV1)
        .withSortOrder(section.sortOrder)
        .build()
    )
  }

  open fun getRecents(section: ContactSearchConfiguration.Section.Recents): Cursor? {
    return SignalDatabase.threads.getRecentConversationList(
      section.limit,
      section.includeInactiveGroups,
      section.mode == ContactSearchConfiguration.Section.Recents.Mode.INDIVIDUALS,
      section.mode == ContactSearchConfiguration.Section.Recents.Mode.GROUPS,
      !section.includeGroupsV1,
      !section.includeSms,
      !section.includeSelf
    )
  }

  open fun getStories(query: String?): Cursor? {
    return SignalDatabase.distributionLists.getAllListsForContactSelectionUiCursor(query, myStoryContainsQuery(query ?: ""))
  }

  open fun getRecipientFromDistributionListCursor(cursor: Cursor): Recipient {
    return Recipient.resolved(RecipientId.from(CursorUtil.requireLong(cursor, DistributionListDatabase.RECIPIENT_ID)))
  }

  open fun getPrivacyModeFromDistributionListCursor(cursor: Cursor): DistributionListPrivacyMode {
    return DistributionListPrivacyMode.deserialize(CursorUtil.requireLong(cursor, DistributionListDatabase.PRIVACY_MODE))
  }

  open fun getRecipientFromThreadCursor(cursor: Cursor): Recipient {
    return Recipient.resolved(RecipientId.from(CursorUtil.requireLong(cursor, ThreadDatabase.RECIPIENT_ID)))
  }

  open fun getRecipientFromRecipientCursor(cursor: Cursor): Recipient {
    return Recipient.resolved(RecipientId.from(CursorUtil.requireLong(cursor, ContactRepository.ID_COLUMN)))
  }

  open fun getRecipientFromGroupRecord(groupRecord: GroupRecord): Recipient {
    return Recipient.resolved(groupRecord.recipientId)
  }

  open fun getDistributionListMembershipCount(recipient: Recipient): Int {
    return SignalDatabase.distributionLists.getMemberCount(recipient.requireDistributionListId())
  }

  open fun getGroupStories(): Set<ContactSearchData.Story> {
    return SignalDatabase.groups.groupsToDisplayAsStories.map {
      val recipient = Recipient.resolved(SignalDatabase.recipients.getOrInsertFromGroupId(it))
      ContactSearchData.Story(recipient, recipient.participantIds.size, DistributionListPrivacyMode.ALL)
    }.toSet()
  }

  open fun recipientNameContainsQuery(recipient: Recipient, query: String?): Boolean {
    return query.isNullOrBlank() || recipient.getDisplayName(context).contains(query)
  }

  open fun myStoryContainsQuery(query: String): Boolean {
    if (query.isEmpty()) {
      return true
    }

    val myStory = context.getString(R.string.Recipient_my_story)
    return myStory.contains(query)
  }
}
