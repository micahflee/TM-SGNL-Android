package org.tm.archive.components.settings.models

import android.view.View
import com.google.android.material.button.MaterialButton
import org.tm.archive.R
import org.tm.archive.components.settings.DSLSettingsIcon
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.PreferenceModel
import org.tm.archive.util.adapter.mapping.LayoutFactory
import org.tm.archive.util.adapter.mapping.MappingAdapter
import org.tm.archive.util.adapter.mapping.MappingViewHolder

object Button {

  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(Model.Primary::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.dsl_button_primary))
    mappingAdapter.registerFactory(Model.Tonal::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.dsl_button_tonal))
    mappingAdapter.registerFactory(Model.SecondaryNoOutline::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.dsl_button_secondary))
  }

  sealed class Model<T : Model<T>>(
    title: DSLSettingsText?,
    icon: DSLSettingsIcon?,
    isEnabled: Boolean,
    val onClick: () -> Unit
  ) : PreferenceModel<T>(
    title = title,
    icon = icon,
    isEnabled = isEnabled
  ) {
    class Primary(
      title: DSLSettingsText?,
      icon: DSLSettingsIcon?,
      isEnabled: Boolean,
      onClick: () -> Unit
    ) : Model<Primary>(title, icon, isEnabled, onClick)

    class Tonal(
      title: DSLSettingsText?,
      icon: DSLSettingsIcon?,
      isEnabled: Boolean,
      onClick: () -> Unit
    ) : Model<Tonal>(title, icon, isEnabled, onClick)

    class SecondaryNoOutline(
      title: DSLSettingsText?,
      icon: DSLSettingsIcon?,
      isEnabled: Boolean,
      onClick: () -> Unit
    ) : Model<SecondaryNoOutline>(title, icon, isEnabled, onClick)
  }

  class ViewHolder<T : Model<T>>(itemView: View) : MappingViewHolder<T>(itemView) {

    private val button: MaterialButton = itemView.findViewById(R.id.button)

    override fun bind(model: T) {
      button.text = model.title?.resolve(context)
      button.setOnClickListener {
        model.onClick()
      }
      button.icon = model.icon?.resolve(context)
      button.isEnabled = model.isEnabled
    }
  }
}
