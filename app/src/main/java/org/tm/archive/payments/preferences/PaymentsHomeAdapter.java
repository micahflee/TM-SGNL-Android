package org.tm.archive.payments.preferences;

import androidx.annotation.NonNull;

import org.tm.archive.R;
import org.tm.archive.components.settings.SettingHeader;
import org.tm.archive.payments.preferences.model.InProgress;
import org.tm.archive.payments.preferences.model.InfoCard;
import org.tm.archive.payments.preferences.model.IntroducingPayments;
import org.tm.archive.payments.preferences.model.NoRecentActivity;
import org.tm.archive.payments.preferences.model.PaymentItem;
import org.tm.archive.payments.preferences.model.SeeAll;
import org.tm.archive.payments.preferences.viewholder.InProgressViewHolder;
import org.tm.archive.payments.preferences.viewholder.InfoCardViewHolder;
import org.tm.archive.payments.preferences.viewholder.IntroducingPaymentViewHolder;
import org.tm.archive.payments.preferences.viewholder.NoRecentActivityViewHolder;
import org.tm.archive.payments.preferences.viewholder.PaymentItemViewHolder;
import org.tm.archive.payments.preferences.viewholder.SeeAllViewHolder;
import org.tm.archive.util.adapter.mapping.MappingAdapter;

public class PaymentsHomeAdapter extends MappingAdapter {

  public PaymentsHomeAdapter(@NonNull Callbacks callbacks) {
    registerFactory(IntroducingPayments.class, p -> new IntroducingPaymentViewHolder(p, callbacks), R.layout.payments_home_introducing_payments_item);
    registerFactory(NoRecentActivity.class, NoRecentActivityViewHolder::new, R.layout.payments_home_no_recent_activity_item);
    registerFactory(InProgress.class, InProgressViewHolder::new, R.layout.payments_home_in_progress);
    registerFactory(PaymentItem.class, p -> new PaymentItemViewHolder(p, callbacks), R.layout.payments_home_payment_item);
    registerFactory(SettingHeader.Item.class, SettingHeader.ViewHolder::new, R.layout.base_settings_header_item);
    registerFactory(SeeAll.class, p -> new SeeAllViewHolder(p, callbacks), R.layout.payments_home_see_all_item);
    registerFactory(InfoCard.class, p -> new InfoCardViewHolder(p, callbacks), R.layout.payment_info_card);
  }

  public interface Callbacks {
    default void onActivatePayments() {}
    default void onRestorePaymentsAccount() {}
    default void onSeeAll(@NonNull PaymentType paymentType) {}
    default void onPaymentItem(@NonNull PaymentItem model) {}
    default void onInfoCardDismissed(InfoCard.Type type) {}
    default void onViewRecoveryPhrase() {}
    default void onUpdatePin() {}
  }
}
