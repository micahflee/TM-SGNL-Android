package org.tm.archive.keyboard.sticker

import android.content.Context
import androidx.annotation.WorkerThread
import org.tm.archive.components.emoji.EmojiUtil
import org.tm.archive.database.DatabaseFactory
import org.tm.archive.database.EmojiSearchDatabase
import org.tm.archive.database.StickerDatabase
import org.tm.archive.database.StickerDatabase.StickerRecordReader
import org.tm.archive.database.model.StickerRecord

private const val RECENT_LIMIT = 24
private const val EMOJI_SEARCH_RESULTS_LIMIT = 20

class StickerSearchRepository(context: Context) {

  private val emojiSearchDatabase: EmojiSearchDatabase = DatabaseFactory.getEmojiSearchDatabase(context)
  private val stickerDatabase: StickerDatabase = DatabaseFactory.getStickerDatabase(context)

  @WorkerThread
  fun search(query: String): List<StickerRecord> {
    if (query.isEmpty()) {
      return StickerRecordReader(stickerDatabase.getRecentlyUsedStickers(RECENT_LIMIT)).readAll()
    }

    val maybeEmojiQuery: List<StickerRecord> = findStickersForEmoji(query)
    val searchResults: List<StickerRecord> = emojiSearchDatabase.query(query, EMOJI_SEARCH_RESULTS_LIMIT)
      .map { findStickersForEmoji(it) }
      .flatten()

    return maybeEmojiQuery + searchResults
  }

  @WorkerThread
  private fun findStickersForEmoji(emoji: String): List<StickerRecord> {
    val searchEmoji: String = EmojiUtil.getCanonicalRepresentation(emoji)

    return EmojiUtil.getAllRepresentations(searchEmoji)
      .filterNotNull()
      .map { candidate -> StickerRecordReader(stickerDatabase.getStickersByEmoji(candidate)).readAll() }
      .flatten()
  }
}

private fun StickerRecordReader.readAll(): List<StickerRecord> {
  val stickers: MutableList<StickerRecord> = mutableListOf()
  use { reader ->
    var record: StickerRecord? = reader.next
    while (record != null) {
      stickers.add(record)
      record = reader.next
    }
  }
  return stickers
}
