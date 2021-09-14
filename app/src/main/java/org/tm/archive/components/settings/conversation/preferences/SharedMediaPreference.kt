package org.tm.archive.components.settings.conversation.preferences

import android.database.Cursor
import android.view.View
import org.tm.archive.R
import org.tm.archive.components.ThreadPhotoRailView
import org.tm.archive.components.settings.PreferenceModel
import org.tm.archive.database.MediaDatabase
import org.tm.archive.mms.GlideApp
import org.tm.archive.util.MappingAdapter
import org.tm.archive.util.MappingViewHolder
import org.tm.archive.util.ViewUtil

/**
 * Renders the shared media photo rail.
 */
object SharedMediaPreference {

  fun register(adapter: MappingAdapter) {
    adapter.registerFactory(Model::class.java, MappingAdapter.LayoutFactory(::ViewHolder, R.layout.conversation_settings_shared_media))
  }

  class Model(
    val mediaCursor: Cursor,
    val mediaIds: List<Long>,
    val onMediaRecordClick: (MediaDatabase.MediaRecord, Boolean) -> Unit
  ) : PreferenceModel<Model>() {
    override fun areItemsTheSame(newItem: Model): Boolean {
      return true
    }

    override fun areContentsTheSame(newItem: Model): Boolean {
      return super.areContentsTheSame(newItem) &&
        mediaIds == newItem.mediaIds
    }
  }

  private class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {

    private val rail: ThreadPhotoRailView = itemView.findViewById(R.id.rail_view)

    override fun bind(model: Model) {
      rail.setCursor(GlideApp.with(rail), model.mediaCursor)
      rail.setListener {
        model.onMediaRecordClick(it, ViewUtil.isLtr(rail))
      }
    }
  }
}
