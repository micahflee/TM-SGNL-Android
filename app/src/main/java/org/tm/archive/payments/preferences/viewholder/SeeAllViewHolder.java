package org.tm.archive.payments.preferences.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import org.tm.archive.R;
import org.tm.archive.payments.preferences.PaymentsHomeAdapter;
import org.tm.archive.payments.preferences.model.SeeAll;
import org.tm.archive.util.MappingViewHolder;

public class SeeAllViewHolder extends MappingViewHolder<SeeAll> {

  private final PaymentsHomeAdapter.Callbacks callbacks;
  private final View                          seeAllButton;

  public SeeAllViewHolder(@NonNull View itemView, PaymentsHomeAdapter.Callbacks callbacks) {
    super(itemView);
    this.callbacks = callbacks;
    this.seeAllButton = itemView.findViewById(R.id.payments_home_see_all_item_button);
  }

  @Override
  public void bind(@NonNull SeeAll model) {
    seeAllButton.setOnClickListener(v -> callbacks.onSeeAll(model.getPaymentType()));
  }
}
