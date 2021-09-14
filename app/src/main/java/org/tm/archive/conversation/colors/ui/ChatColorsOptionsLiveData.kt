package org.tm.archive.conversation.colors.ui

import androidx.lifecycle.LiveData
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.conversation.colors.ChatColors
import org.tm.archive.conversation.colors.ChatColorsPalette
import org.tm.archive.database.ChatColorsDatabase
import org.tm.archive.database.DatabaseFactory
import org.tm.archive.database.DatabaseObserver
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.util.concurrent.SerialMonoLifoExecutor
import java.util.concurrent.Executor

class ChatColorsOptionsLiveData : LiveData<List<ChatColors>>() {
  private val chatColorsDatabase: ChatColorsDatabase = DatabaseFactory.getChatColorsDatabase(ApplicationDependencies.getApplication())
  private val observer: DatabaseObserver.Observer = DatabaseObserver.Observer { refreshChatColors() }
  private val executor: Executor = SerialMonoLifoExecutor(SignalExecutors.BOUNDED)

  override fun onActive() {
    refreshChatColors()
    ApplicationDependencies.getDatabaseObserver().registerChatColorsObserver(observer)
  }

  override fun onInactive() {
    ApplicationDependencies.getDatabaseObserver().unregisterObserver(observer)
  }

  private fun refreshChatColors() {
    executor.execute {
      val options = mutableListOf<ChatColors>().apply {
        addAll(ChatColorsPalette.Bubbles.all)
        addAll(chatColorsDatabase.getSavedChatColors())
      }

      postValue(options)
    }
  }
}
