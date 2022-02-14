package org.tm.archive.components.settings.app.notifications.profiles.models

import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.SimpleColorFilter
import org.tm.archive.R
import org.tm.archive.components.settings.PreferenceModel
import org.tm.archive.conversation.colors.AvatarColor
import org.tm.archive.util.adapter.mapping.LayoutFactory
import org.tm.archive.util.adapter.mapping.MappingAdapter
import org.tm.archive.util.adapter.mapping.MappingViewHolder

/**
 * DSL custom preference for showing no profiles/empty state.
 */
object NoNotificationProfiles {

  fun register(adapter: MappingAdapter) {
    adapter.registerFactory(Model::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.notification_profiles_empty))
  }

  class Model(val onClick: () -> Unit) : PreferenceModel<Model>() {
    override fun areItemsTheSame(newItem: Model): Boolean = true
  }

  class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {

    private val icon: ImageView = findViewById(R.id.notification_profiles_empty_icon)
    private val button: View = findViewById(R.id.notification_profiles_empty_create_profile)

    override fun bind(model: Model) {
      icon.background.colorFilter = SimpleColorFilter(AvatarColor.A100.colorInt())
      button.setOnClickListener { model.onClick() }
    }
  }
}
