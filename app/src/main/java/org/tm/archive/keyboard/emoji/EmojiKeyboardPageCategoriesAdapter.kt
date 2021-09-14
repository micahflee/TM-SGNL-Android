package org.tm.archive.keyboard.emoji

import org.tm.archive.R
import org.tm.archive.keyboard.KeyboardPageCategoryIconViewHolder
import org.tm.archive.util.MappingAdapter
import java.util.function.Consumer

class EmojiKeyboardPageCategoriesAdapter(private val onPageSelected: Consumer<String>) : MappingAdapter() {
  init {
    registerFactory(EmojiKeyboardPageCategoryMappingModel.RecentsMappingModel::class.java, LayoutFactory({ v -> KeyboardPageCategoryIconViewHolder<EmojiKeyboardPageCategoryMappingModel.RecentsMappingModel>(v, onPageSelected) }, R.layout.keyboard_pager_category_icon))
    registerFactory(EmojiKeyboardPageCategoryMappingModel.EmojiCategoryMappingModel::class.java, LayoutFactory({ v -> KeyboardPageCategoryIconViewHolder<EmojiKeyboardPageCategoryMappingModel.EmojiCategoryMappingModel>(v, onPageSelected) }, R.layout.keyboard_pager_category_icon))
  }
}
