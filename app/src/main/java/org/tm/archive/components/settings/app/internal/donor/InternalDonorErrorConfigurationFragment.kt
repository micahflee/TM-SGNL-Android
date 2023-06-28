package org.tm.archive.components.settings.app.internal.donor

import androidx.fragment.app.viewModels
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.signal.core.util.concurrent.LifecycleDisposable
import org.signal.donations.StripeDeclineCode
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsFragment
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.app.subscription.errors.UnexpectedSubscriptionCancellation
import org.tm.archive.components.settings.configure
import org.tm.archive.util.adapter.mapping.MappingAdapter

class InternalDonorErrorConfigurationFragment : DSLSettingsFragment() {

  private val viewModel: InternalDonorErrorConfigurationViewModel by viewModels()
  private val lifecycleDisposable = LifecycleDisposable()

  override fun bindAdapter(adapter: MappingAdapter) {
    lifecycleDisposable += viewModel.state.observeOn(AndroidSchedulers.mainThread()).subscribe { state ->
      adapter.submitList(getConfiguration(state).toMappingModelList())
    }
  }

  private fun getConfiguration(state: InternalDonorErrorConfigurationState): DSLConfiguration {
    return configure {
      radioListPref(
        title = DSLSettingsText.from("Expired Badge"),
        selected = state.badges.indexOf(state.selectedBadge),
        listItems = state.badges.map { it.name }.toTypedArray(),
        onSelected = { viewModel.setSelectedBadge(it) }
      )

      radioListPref(
        title = DSLSettingsText.from("Cancellation Reason"),
        selected = UnexpectedSubscriptionCancellation.values().indexOf(state.selectedUnexpectedSubscriptionCancellation),
        listItems = UnexpectedSubscriptionCancellation.values().map { it.status }.toTypedArray(),
        onSelected = { viewModel.setSelectedUnexpectedSubscriptionCancellation(it) },
        isEnabled = state.selectedBadge == null || state.selectedBadge.isSubscription()
      )

      radioListPref(
        title = DSLSettingsText.from("Charge Failure"),
        selected = StripeDeclineCode.Code.values().indexOf(state.selectedStripeDeclineCode),
        listItems = StripeDeclineCode.Code.values().map { it.code }.toTypedArray(),
        onSelected = { viewModel.setStripeDeclineCode(it) },
        isEnabled = state.selectedBadge == null || state.selectedBadge.isSubscription()
      )

      primaryButton(
        text = DSLSettingsText.from("Save and Finish"),
        onClick = {
          lifecycleDisposable += viewModel.save().subscribe { requireActivity().finish() }
        }
      )

      secondaryButtonNoOutline(
        text = DSLSettingsText.from("Clear"),
        onClick = {
          lifecycleDisposable += viewModel.clearErrorState().subscribe()
        }
      )
    }
  }
}
