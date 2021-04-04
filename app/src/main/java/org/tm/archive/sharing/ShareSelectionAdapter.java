package org.tm.archive.sharing;

import org.tm.archive.R;
import org.tm.archive.util.MappingAdapter;

class ShareSelectionAdapter extends MappingAdapter {
  ShareSelectionAdapter() {
    registerFactory(ShareSelectionMappingModel.class,
                    ShareSelectionViewHolder.createFactory(R.layout.share_contact_selection_item));
  }
}
