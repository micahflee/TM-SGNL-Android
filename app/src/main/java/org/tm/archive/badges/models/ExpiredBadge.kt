package org.tm.archive.badges.models

import android.view.View
import org.tm.archive.R
import org.tm.archive.badges.BadgeImageView
import org.tm.archive.components.settings.PreferenceModel
import org.tm.archive.util.adapter.mapping.LayoutFactory
import org.tm.archive.util.adapter.mapping.MappingAdapter
import org.tm.archive.util.adapter.mapping.MappingViewHolder

object ExpiredBadge {

  class Model(val badge: Badge) : PreferenceModel<Model>() {
    override fun areItemsTheSame(newItem: Model): Boolean {
      return newItem.badge.id == badge.id
    }

    override fun areContentsTheSame(newItem: Model): Boolean {
      return super.areContentsTheSame(newItem) && newItem.badge == badge
    }
  }

  class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {

    private val badge: BadgeImageView = itemView.findViewById(R.id.expired_badge)

    override fun bind(model: Model) {
      badge.setBadge(model.badge)
    }
  }

  fun register(adapter: MappingAdapter) {
    adapter.registerFactory(Model::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.expired_badge_preference))
  }
}
