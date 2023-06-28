package org.tm.archive.keyboard.sticker

import androidx.annotation.WorkerThread
import org.tm.archive.components.emoji.EmojiUtil
import org.tm.archive.database.EmojiSearchTable
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.StickerTable
import org.tm.archive.database.StickerTable.StickerRecordReader
import org.tm.archive.database.model.StickerRecord

private const val RECENT_LIMIT = 24
private const val EMOJI_SEARCH_RESULTS_LIMIT = 20

class StickerSearchRepository {

  private val emojiSearchTable: EmojiSearchTable = SignalDatabase.emojiSearch
  private val stickerTable: StickerTable = SignalDatabase.stickers

  @WorkerThread
  fun search(query: String): List<StickerRecord> {
    if (query.isEmpty()) {
      return StickerRecordReader(stickerTable.getRecentlyUsedStickers(RECENT_LIMIT)).readAll()
    }

    val maybeEmojiQuery: List<StickerRecord> = findStickersForEmoji(query)
    val searchResults: List<StickerRecord> = emojiSearchTable.query(query, EMOJI_SEARCH_RESULTS_LIMIT)
      .map { findStickersForEmoji(it) }
      .flatten()

    return maybeEmojiQuery + searchResults
  }

  @WorkerThread
  private fun findStickersForEmoji(emoji: String): List<StickerRecord> {
    val searchEmoji: String = EmojiUtil.getCanonicalRepresentation(emoji)

    return EmojiUtil.getAllRepresentations(searchEmoji)
      .filterNotNull()
      .map { candidate -> StickerRecordReader(stickerTable.getStickersByEmoji(candidate)).readAll() }
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
