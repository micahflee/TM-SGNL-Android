package org.tm.archive.keyboard.emoji.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tm.archive.components.emoji.EmojiPageModel
import org.tm.archive.components.emoji.RecentEmojiPageModel
import org.tm.archive.keyboard.emoji.toMappingModels
import org.tm.archive.util.adapter.mapping.MappingModel
import org.tm.archive.util.livedata.LiveDataUtil

class EmojiSearchViewModel(private val repository: EmojiSearchRepository) : ViewModel() {

  private val pageModel = MutableLiveData<EmojiPageModel>()

  val emojiList: LiveData<EmojiSearchResults> = LiveDataUtil.mapAsync(pageModel) { page ->
    EmojiSearchResults(page.toMappingModels(), page.key == RecentEmojiPageModel.KEY)
  }

  init {
    onQueryChanged("")
  }

  fun onQueryChanged(query: String) {
    repository.submitQuery(query = query, includeRecents = true, consumer = pageModel::postValue)
  }

  data class EmojiSearchResults(val emojiList: List<MappingModel<*>>, val isRecents: Boolean)

  class Factory(private val repository: EmojiSearchRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return requireNotNull(modelClass.cast(EmojiSearchViewModel(repository)))
    }
  }
}
