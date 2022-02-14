package org.tm.archive.payments.preferences.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import org.tm.archive.R;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.payments.preferences.PaymentsHomeAdapter;
import org.tm.archive.payments.preferences.model.IntroducingPayments;
import org.tm.archive.util.adapter.mapping.MappingViewHolder;
import org.tm.archive.util.views.LearnMoreTextView;

public class IntroducingPaymentViewHolder extends MappingViewHolder<IntroducingPayments> {

  private final PaymentsHomeAdapter.Callbacks callbacks;
  private final View                          activateButton;
  private final Group                         activatingGroup;
  private final View                          restoreButton;
  private final LearnMoreTextView             learnMoreView;

  public IntroducingPaymentViewHolder(@NonNull View itemView, @NonNull PaymentsHomeAdapter.Callbacks callbacks) {
    super(itemView);
    this.callbacks       = callbacks;
    this.activateButton  = findViewById(R.id.payment_preferences_splash_activate);
    this.activatingGroup = findViewById(R.id.payment_preferences_splash_activating_group);
    this.restoreButton   = findViewById(R.id.payment_preferences_splash_restore);
    this.learnMoreView   = findViewById(R.id.payment_preferences_splash_text_1);
  }

  @Override
  public void bind(@NonNull IntroducingPayments model) {
    if (model.isActivating()) {
      activateButton.setVisibility(View.INVISIBLE);
      activatingGroup.setVisibility(View.VISIBLE);
    } else {
      activateButton.setVisibility(View.VISIBLE);
      activatingGroup.setVisibility(View.GONE);
    }

    learnMoreView.setLearnMoreVisible(true);
    learnMoreView.setLink(getContext().getString(R.string.PaymentsHomeFragment__learn_more__activate_payments));

    activateButton.setOnClickListener(v -> callbacks.onActivatePayments());

    if (SignalStore.paymentsValues().hasPaymentsEntropy()) {
      restoreButton.setVisibility(View.GONE);
    } else {
      restoreButton.setVisibility(View.VISIBLE);
      restoreButton.setOnClickListener(v -> callbacks.onRestorePaymentsAccount());
    }
  }
}
