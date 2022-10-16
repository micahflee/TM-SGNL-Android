package org.tm.archive.components.settings.app.subscription.receipts.list

import android.view.LayoutInflater
import android.view.ViewGroup
import org.tm.archive.R
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.SectionHeaderPreference
import org.tm.archive.components.settings.SectionHeaderPreferenceViewHolder
import org.tm.archive.components.settings.TextPreference
import org.tm.archive.components.settings.TextPreferenceViewHolder
import org.tm.archive.util.StickyHeaderDecoration
import org.tm.archive.util.adapter.mapping.LayoutFactory
import org.tm.archive.util.adapter.mapping.MappingAdapter
import org.tm.archive.util.toLocalDateTime

class DonationReceiptListAdapter(onModelClick: (DonationReceiptListItem.Model) -> Unit) : MappingAdapter(), StickyHeaderDecoration.StickyHeaderAdapter<SectionHeaderPreferenceViewHolder> {

  init {
    registerFactory(TextPreference::class.java, LayoutFactory({ TextPreferenceViewHolder(it) }, R.layout.dsl_preference_item))
    DonationReceiptListItem.register(this, onModelClick)
  }

  override fun getHeaderId(position: Int): Long {
    return when (val item = getItem(position)) {
      is DonationReceiptListItem.Model -> item.record.timestamp.toLocalDateTime().year.toLong()
      else -> StickyHeaderDecoration.StickyHeaderAdapter.NO_HEADER_ID
    }
  }

  override fun onCreateHeaderViewHolder(parent: ViewGroup?, position: Int, type: Int): SectionHeaderPreferenceViewHolder {
    return SectionHeaderPreferenceViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.dsl_section_header, parent, false))
  }

  override fun onBindHeaderViewHolder(viewHolder: SectionHeaderPreferenceViewHolder?, position: Int, type: Int) {
    viewHolder?.bind(SectionHeaderPreference(DSLSettingsText.from(getHeaderId(position).toString())))
  }
}
