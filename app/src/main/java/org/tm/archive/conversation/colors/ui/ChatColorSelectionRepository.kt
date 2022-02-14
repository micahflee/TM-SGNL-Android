package org.tm.archive.conversation.colors.ui

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.conversation.colors.ChatColors
import org.tm.archive.conversation.colors.ChatColorsPalette
import org.tm.archive.database.SignalDatabase
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.wallpaper.ChatWallpaper

sealed class ChatColorSelectionRepository(context: Context) {

  protected val context: Context = context.applicationContext

  abstract fun getWallpaper(consumer: (ChatWallpaper?) -> Unit)
  abstract fun getChatColors(consumer: (ChatColors) -> Unit)
  abstract fun save(chatColors: ChatColors, onSaved: () -> Unit)

  fun duplicate(chatColors: ChatColors) {
    SignalExecutors.BOUNDED.execute {
      val duplicate = chatColors.withId(ChatColors.Id.NotSet)
      SignalDatabase.chatColors.saveChatColors(duplicate)
    }
  }

  fun getUsageCount(chatColorsId: ChatColors.Id, consumer: (Int) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      consumer(SignalDatabase.recipients.getColorUsageCount(chatColorsId))
    }
  }

  fun delete(chatColors: ChatColors, onDeleted: () -> Unit) {
    SignalExecutors.BOUNDED.execute {
      SignalDatabase.chatColors.deleteChatColors(chatColors)
      onDeleted()
    }
  }

  private class Global(context: Context) : ChatColorSelectionRepository(context) {
    override fun getWallpaper(consumer: (ChatWallpaper?) -> Unit) {
      consumer(SignalStore.wallpaper().wallpaper)
    }

    override fun getChatColors(consumer: (ChatColors) -> Unit) {
      if (SignalStore.chatColorsValues().hasChatColors) {
        consumer(requireNotNull(SignalStore.chatColorsValues().chatColors))
      } else {
        getWallpaper { wallpaper ->
          if (wallpaper != null) {
            consumer(wallpaper.autoChatColors)
          } else {
            consumer(ChatColorsPalette.Bubbles.default.withId(ChatColors.Id.Auto))
          }
        }
      }
    }

    override fun save(chatColors: ChatColors, onSaved: () -> Unit) {
      if (chatColors.id == ChatColors.Id.Auto) {
        SignalStore.chatColorsValues().chatColors = null
      } else {
        SignalStore.chatColorsValues().chatColors = chatColors
      }
      onSaved()
    }
  }

  private class Single(context: Context, private val recipientId: RecipientId) : ChatColorSelectionRepository(context) {
    override fun getWallpaper(consumer: (ChatWallpaper?) -> Unit) {
      SignalExecutors.BOUNDED.execute {
        val recipient = Recipient.resolved(recipientId)
        consumer(recipient.wallpaper)
      }
    }

    override fun getChatColors(consumer: (ChatColors) -> Unit) {
      SignalExecutors.BOUNDED.execute {
        val recipient = Recipient.resolved(recipientId)
        consumer(recipient.chatColors)
      }
    }

    override fun save(chatColors: ChatColors, onSaved: () -> Unit) {
      SignalExecutors.BOUNDED.execute {
        val recipientDatabase = SignalDatabase.recipients
        recipientDatabase.setColor(recipientId, chatColors)
        onSaved()
      }
    }
  }

  companion object {
    fun create(context: Context, recipientId: RecipientId?): ChatColorSelectionRepository {
      return if (recipientId != null) {
        Single(context, recipientId)
      } else {
        Global(context)
      }
    }
  }
}
