package org.tm.archive.conversation.colors.ui

import org.tm.archive.conversation.colors.ChatColors
import org.tm.archive.conversation.colors.ChatColorsPalette
import org.tm.archive.util.adapter.mapping.MappingModelList
import org.tm.archive.wallpaper.ChatWallpaper

data class ChatColorSelectionState(
  val wallpaper: ChatWallpaper? = null,
  val chatColors: ChatColors? = null,
  private val chatColorOptions: List<ChatColors> = listOf()
) {

  val chatColorModels: MappingModelList

  init {
    val models: List<ChatColorMappingModel> = chatColorOptions.map { chatColors ->
      ChatColorMappingModel(
        chatColors,
        chatColors == this.chatColors,
        false
      )
    }.toList()

    val defaultModel: ChatColorMappingModel = if (wallpaper != null) {
      ChatColorMappingModel(
        wallpaper.autoChatColors,
        chatColors?.id == ChatColors.Id.Auto,
        true
      )
    } else {
      ChatColorMappingModel(
        ChatColorsPalette.Bubbles.default.withId(ChatColors.Id.Auto),
        chatColors?.id == ChatColors.Id.Auto,
        true
      )
    }

    chatColorModels = MappingModelList().apply {
      add(defaultModel)
      addAll(models)
      add(CustomColorMappingModel())
    }
  }
}
