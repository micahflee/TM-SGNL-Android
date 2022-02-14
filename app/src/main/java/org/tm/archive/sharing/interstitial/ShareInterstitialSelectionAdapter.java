package org.tm.archive.sharing.interstitial;

import org.tm.archive.R;
import org.tm.archive.util.adapter.mapping.MappingAdapter;
import org.tm.archive.util.viewholders.RecipientViewHolder;

class ShareInterstitialSelectionAdapter extends MappingAdapter {
  ShareInterstitialSelectionAdapter() {
    registerFactory(ShareInterstitialMappingModel.class, RecipientViewHolder.createFactory(R.layout.share_contact_selection_item, null));
  }
}
