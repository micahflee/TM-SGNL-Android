package org.tm.archive.keyboard

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import org.tm.archive.R
import org.tm.archive.util.adapter.mapping.MappingModel
import org.tm.archive.util.adapter.mapping.MappingViewHolder
import java.util.function.Consumer

interface KeyboardPageCategoryIconMappingModel<T : KeyboardPageCategoryIconMappingModel<T>> : MappingModel<T> {
  val key: String
  val selected: Boolean

  fun getIcon(context: Context): Drawable
}

class KeyboardPageCategoryIconViewHolder<T : KeyboardPageCategoryIconMappingModel<T>>(itemView: View, private val onPageSelected: Consumer<String>) : MappingViewHolder<T>(itemView) {

  private val iconView: AppCompatImageView = itemView.findViewById(R.id.category_icon)
  private val iconSelected: View = itemView.findViewById(R.id.category_icon_selected)

  override fun bind(model: T) {
    itemView.setOnClickListener {
      onPageSelected.accept(model.key)
    }

    iconView.setImageDrawable(model.getIcon(context))
    iconView.isSelected = model.selected
    iconSelected.isSelected = model.selected
  }
}
