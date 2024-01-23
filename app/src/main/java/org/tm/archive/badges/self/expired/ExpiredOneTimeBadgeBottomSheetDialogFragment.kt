package org.tm.archive.badges.self.expired

import androidx.fragment.app.FragmentManager
import org.signal.core.util.DimensionUnit
import org.signal.core.util.logging.Log
import org.tm.archive.R
import org.tm.archive.badges.models.Badge
import org.tm.archive.badges.models.ExpiredBadge
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsAdapter
import org.tm.archive.components.settings.DSLSettingsBottomSheetFragment
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.app.AppSettingsActivity
import org.tm.archive.components.settings.app.subscription.errors.UnexpectedSubscriptionCancellation
import org.tm.archive.components.settings.configure
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.util.BottomSheetUtil
import org.whispersystems.signalservice.api.subscriptions.ActiveSubscription

/**
 * Bottom sheet displaying a fading badge with a notice and action for becoming a subscriber again.
 */
class ExpiredOneTimeBadgeBottomSheetDialogFragment : DSLSettingsBottomSheetFragment(
  peekHeightPercentage = 1f
) {
  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    ExpiredBadge.register(adapter)

    adapter.submitList(getConfiguration().toMappingModelList())
  }

  private fun getConfiguration(): DSLConfiguration {
    val args = ExpiredOneTimeBadgeBottomSheetDialogFragmentArgs.fromBundle(requireArguments())
    val badge: Badge = args.badge
    val isLikelyASustainer = SignalStore.donationsValues().isLikelyASustainer()

    Log.d(TAG, "Displaying Expired Badge Fragment with bundle: ${requireArguments()}", true)

    return configure {
      customPref(ExpiredBadge.Model(badge))

      sectionHeaderPref(
        DSLSettingsText.from(
          if (badge.isBoost()) {
            R.string.ExpiredBadgeBottomSheetDialogFragment__boost_badge_expired
          } else {
            R.string.ExpiredBadgeBottomSheetDialogFragment__monthly_donation_cancelled
          },
          DSLSettingsText.CenterModifier
        )
      )

      space(DimensionUnit.DP.toPixels(4f).toInt())

      noPadTextPref(
        DSLSettingsText.from(
          getString(R.string.ExpiredBadgeBottomSheetDialogFragment__your_boost_badge_has_expired_and),
          DSLSettingsText.CenterModifier
        )
      )

      space(DimensionUnit.DP.toPixels(16f).toInt())

      noPadTextPref(
        DSLSettingsText.from(
          if (isLikelyASustainer) {
            R.string.ExpiredBadgeBottomSheetDialogFragment__you_can_reactivate
          } else {
            R.string.ExpiredBadgeBottomSheetDialogFragment__you_can_keep
          },
          DSLSettingsText.CenterModifier
        )
      )

      space(DimensionUnit.DP.toPixels(92f).toInt())

      primaryButton(
        text = DSLSettingsText.from(
          if (isLikelyASustainer) {
            R.string.ExpiredBadgeBottomSheetDialogFragment__add_a_boost
          } else {
            R.string.ExpiredBadgeBottomSheetDialogFragment__become_a_sustainer
          }
        ),
        onClick = {
          dismiss()
          if (isLikelyASustainer) {
            requireActivity().startActivity(AppSettingsActivity.boost(requireContext()))
          } else {
            requireActivity().startActivity(AppSettingsActivity.subscriptions(requireContext()))
          }
        }
      )

      secondaryButtonNoOutline(
        text = DSLSettingsText.from(R.string.ExpiredBadgeBottomSheetDialogFragment__not_now),
        onClick = {
          dismiss()
        }
      )
    }
  }

  companion object {
    private val TAG = Log.tag(ExpiredOneTimeBadgeBottomSheetDialogFragment::class.java)

    @JvmStatic
    fun show(
      badge: Badge,
      cancellationReason: UnexpectedSubscriptionCancellation?,
      chargeFailure: ActiveSubscription.ChargeFailure?,
      fragmentManager: FragmentManager
    ) {
      val args = ExpiredOneTimeBadgeBottomSheetDialogFragmentArgs.Builder(badge, cancellationReason?.status, chargeFailure?.code).build()
      val fragment = ExpiredOneTimeBadgeBottomSheetDialogFragment()
      fragment.arguments = args.toBundle()

      fragment.show(fragmentManager, BottomSheetUtil.STANDARD_BOTTOM_SHEET_FRAGMENT_TAG)
    }
  }
}
