package org.tm.archive.keyboard.emoji

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.components.emoji.EmojiPageModel
import org.tm.archive.components.emoji.RecentEmojiPageModel
import org.tm.archive.emoji.EmojiSource.Companion.latest
import org.tm.archive.util.TextSecurePreferences
import java.util.function.Consumer

class EmojiKeyboardPageRepository(private val context: Context) {
  fun getEmoji(consumer: Consumer<List<EmojiPageModel>>) {
    SignalExecutors.BOUNDED.execute {
      val list = mutableListOf<EmojiPageModel>()
      list += RecentEmojiPageModel(context, TextSecurePreferences.RECENT_STORAGE_KEY)
      list += latest.displayPages
      consumer.accept(list)
    }
  }
}
