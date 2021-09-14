package org.tm.archive.keyboard.emoji

import org.tm.archive.components.emoji.EmojiPageModel
import org.tm.archive.components.emoji.EmojiPageViewGridAdapter
import org.tm.archive.components.emoji.RecentEmojiPageModel
import org.tm.archive.components.emoji.parsing.EmojiTree
import org.tm.archive.emoji.EmojiCategory
import org.tm.archive.emoji.EmojiSource
import org.tm.archive.util.MappingModel

fun EmojiPageModel.toMappingModels(): List<MappingModel<*>> {
  val emojiTree: EmojiTree = EmojiSource.latest.emojiTree

  return displayEmoji.map {
    val isTextEmoji = EmojiCategory.EMOTICONS.key == key || (RecentEmojiPageModel.KEY == key && emojiTree.getEmoji(it.value, 0, it.value.length) == null)

    if (isTextEmoji) {
      EmojiPageViewGridAdapter.EmojiTextModel(key, it)
    } else {
      EmojiPageViewGridAdapter.EmojiModel(key, it)
    }
  }
}
