package org.tm.archive.conversation.ui.inlinequery

import android.view.View
import org.tm.archive.R
import org.tm.archive.components.emoji.EmojiImageView
import org.tm.archive.util.adapter.mapping.AnyMappingModel
import org.tm.archive.util.adapter.mapping.MappingModel
import org.tm.archive.util.adapter.mapping.MappingViewHolder

/**
 * Used to render inline emoji search results in a [org.tm.archive.util.adapter.mapping.MappingAdapter]
 */
object InlineQueryEmojiResult {

  class Model(val canonicalEmoji: String, val preferredEmoji: String, val keywordSearch: Boolean) : MappingModel<Model> {
    override fun areItemsTheSame(newItem: Model): Boolean {
      return canonicalEmoji == newItem.canonicalEmoji
    }

    override fun areContentsTheSame(newItem: Model): Boolean {
      return preferredEmoji == newItem.preferredEmoji
    }
  }

  class ViewHolder(itemView: View, private val listener: (AnyMappingModel) -> Unit) : MappingViewHolder<Model>(itemView) {

    private val emoji: EmojiImageView = findViewById(R.id.inline_query_emoji_image)

    override fun bind(model: Model) {
      itemView.setOnClickListener { listener(model) }
      emoji.setImageEmoji(model.preferredEmoji)
    }
  }
}
