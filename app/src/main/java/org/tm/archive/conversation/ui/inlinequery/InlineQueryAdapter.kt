package org.tm.archive.conversation.ui.inlinequery

import org.tm.archive.R
import org.tm.archive.util.adapter.mapping.AnyMappingModel
import org.tm.archive.util.adapter.mapping.MappingAdapter

class InlineQueryAdapter(listener: (AnyMappingModel) -> Unit) : MappingAdapter() {
  init {
    registerFactory(InlineQueryEmojiResult.Model::class.java, { InlineQueryEmojiResult.ViewHolder(it, listener) }, R.layout.inline_query_emoji_result)
  }
}
