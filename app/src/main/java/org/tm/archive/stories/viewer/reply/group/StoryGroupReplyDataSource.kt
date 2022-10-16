package org.tm.archive.stories.viewer.reply.group

import org.signal.paging.PagedDataSource
import org.tm.archive.conversation.ConversationMessage
import org.tm.archive.database.MmsDatabase
import org.tm.archive.database.MmsSmsColumns
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MessageId
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.dependencies.ApplicationDependencies

class StoryGroupReplyDataSource(private val parentStoryId: Long) : PagedDataSource<MessageId, ReplyBody> {
  override fun size(): Int {
    return SignalDatabase.mms.getNumberOfStoryReplies(parentStoryId)
  }

  override fun load(start: Int, length: Int, cancellationSignal: PagedDataSource.CancellationSignal): MutableList<ReplyBody> {
    val results: MutableList<ReplyBody> = ArrayList(length)
    SignalDatabase.mms.getStoryReplies(parentStoryId).use { cursor ->
      cursor.moveToPosition(start - 1)
      val reader = MmsDatabase.Reader(cursor)
      while (cursor.moveToNext() && cursor.position < start + length) {
        results.add(readRowFromRecord(reader.current as MmsMessageRecord))
      }
    }

    return results
  }

  override fun load(key: MessageId): ReplyBody {
    return readRowFromRecord(SignalDatabase.mms.getMessageRecord(key.id) as MmsMessageRecord)
  }

  override fun getKey(data: ReplyBody): MessageId {
    return data.key
  }

  private fun readRowFromRecord(record: MmsMessageRecord): ReplyBody {
    return when {
      record.isRemoteDelete -> ReplyBody.RemoteDelete(record)
      MmsSmsColumns.Types.isStoryReaction(record.type) -> ReplyBody.Reaction(record)
      else -> ReplyBody.Text(
        ConversationMessage.ConversationMessageFactory.createWithUnresolvedData(ApplicationDependencies.getApplication(), record)
      )
    }
  }
}
