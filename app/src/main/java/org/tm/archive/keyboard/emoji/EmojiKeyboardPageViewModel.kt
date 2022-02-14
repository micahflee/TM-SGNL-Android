package org.tm.archive.keyboard.emoji

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tm.archive.R
import org.tm.archive.components.emoji.EmojiPageModel
import org.tm.archive.components.emoji.EmojiPageViewGridAdapter.EmojiHeader
import org.tm.archive.components.emoji.RecentEmojiPageModel
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.emoji.EmojiCategory
import org.tm.archive.keyboard.emoji.EmojiKeyboardPageCategoryMappingModel.EmojiCategoryMappingModel
import org.tm.archive.util.DefaultValueLiveData
import org.tm.archive.util.TextSecurePreferences
import org.tm.archive.util.adapter.mapping.MappingModelList
import org.tm.archive.util.livedata.LiveDataUtil

class EmojiKeyboardPageViewModel(repository: EmojiKeyboardPageRepository) : ViewModel() {

  private val internalSelectedKey = DefaultValueLiveData<String>(getStartingTab())

  val selectedKey: LiveData<String>
    get() = internalSelectedKey

  val allEmojiModels: MutableLiveData<List<EmojiPageModel>> = MutableLiveData()
  val pages: LiveData<MappingModelList>
  val categories: LiveData<MappingModelList>

  init {
    repository.getEmoji(allEmojiModels::postValue)

    pages = LiveDataUtil.mapAsync(allEmojiModels) { models ->
      val list = MappingModelList()
      models.forEach { pageModel ->
        list += if (RecentEmojiPageModel.KEY == pageModel.key) {
          EmojiHeader(pageModel.key, R.string.ReactWithAnyEmojiBottomSheetDialogFragment__recently_used)
        } else {
          val category = EmojiCategory.forKey(pageModel.key)
          EmojiHeader(pageModel.key, category.getCategoryLabel())
        }

        list += pageModel.toMappingModels()
      }

      list
    }

    categories = LiveDataUtil.combineLatest(allEmojiModels, internalSelectedKey) { models, selectedKey ->
      val list = MappingModelList()
      list += models.map { m ->
        if (RecentEmojiPageModel.KEY == m.key) {
          EmojiKeyboardPageCategoryMappingModel.RecentsMappingModel(m.key == selectedKey)
        } else {
          val category = EmojiCategory.forKey(m.key)
          EmojiCategoryMappingModel(category, category.key == selectedKey)
        }
      }
      list
    }
  }

  fun onKeySelected(key: String) {
    internalSelectedKey.value = key
  }

  fun addToRecents(emoji: String) {
    RecentEmojiPageModel(ApplicationDependencies.getApplication(), TextSecurePreferences.RECENT_STORAGE_KEY).onCodePointSelected(emoji)
  }

  companion object {
    fun getStartingTab(): String {
      return if (RecentEmojiPageModel.hasRecents(ApplicationDependencies.getApplication(), TextSecurePreferences.RECENT_STORAGE_KEY)) {
        RecentEmojiPageModel.KEY
      } else {
        EmojiCategory.PEOPLE.key
      }
    }
  }

  class Factory(context: Context) : ViewModelProvider.Factory {

    private val repository = EmojiKeyboardPageRepository(context)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      return requireNotNull(modelClass.cast(EmojiKeyboardPageViewModel(repository)))
    }
  }
}
