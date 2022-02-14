package org.tm.archive.components.settings.app.notifications.manual

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import org.signal.core.util.DimensionUnit
import org.tm.archive.R
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsAdapter
import org.tm.archive.components.settings.DSLSettingsBottomSheetFragment
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.app.AppSettingsActivity
import org.tm.archive.components.settings.app.notifications.manual.models.NotificationProfileSelection
import org.tm.archive.components.settings.app.notifications.profiles.NotificationProfilesRepository
import org.tm.archive.components.settings.configure
import org.tm.archive.notifications.profiles.NotificationProfile
import org.tm.archive.notifications.profiles.NotificationProfiles
import org.tm.archive.util.BottomSheetUtil

/**
 * BottomSheetDialogFragment that allows a user to select a notification profile to manually enable/disable.
 */
class NotificationProfileSelectionFragment : DSLSettingsBottomSheetFragment() {

  private val viewModel: NotificationProfileSelectionViewModel by viewModels(
    factoryProducer = {
      NotificationProfileSelectionViewModel.Factory(NotificationProfilesRepository())
    }
  )

  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    NotificationProfileSelection.register(adapter)

    recyclerView.itemAnimator = null

    viewModel.state.observe(viewLifecycleOwner) {
      adapter.submitList(getConfiguration(it).toMappingModelList())
    }
  }

  private fun getConfiguration(state: NotificationProfileSelectionState): DSLConfiguration {
    val activeProfile: NotificationProfile? = NotificationProfiles.getActiveProfile(state.notificationProfiles)

    return configure {

      state.notificationProfiles.sortedDescending().forEach { profile ->
        customPref(
          NotificationProfileSelection.Entry(
            isOn = profile == activeProfile,
            summary = if (profile == activeProfile) DSLSettingsText.from(NotificationProfiles.getActiveProfileDescription(requireContext(), profile)) else DSLSettingsText.from(R.string.NotificationProfileDetails__off),
            notificationProfile = profile,
            isExpanded = profile.id == state.expandedId,
            timeSlotB = state.timeSlotB,
            onRowClick = viewModel::toggleEnabled,
            onTimeSlotAClick = viewModel::enableForOneHour,
            onTimeSlotBClick = viewModel::enableUntil,
            onToggleClick = viewModel::setExpanded,
            onViewSettingsClick = { navigateToSettings(it) }
          )
        )
        space(DimensionUnit.DP.toPixels(16f).toInt())
      }

      customPref(
        NotificationProfileSelection.New(
          onClick = {
            startActivity(AppSettingsActivity.createNotificationProfile(requireContext()))
            dismissAllowingStateLoss()
          }
        )
      )

      space(DimensionUnit.DP.toPixels(20f).toInt())
    }
  }

  private fun navigateToSettings(notificationProfile: NotificationProfile) {
    startActivity(AppSettingsActivity.notificationProfileDetails(requireContext(), notificationProfile.id))
    dismissAllowingStateLoss()
  }

  companion object {
    @JvmStatic
    fun show(fragmentManager: FragmentManager) {
      NotificationProfileSelectionFragment().show(fragmentManager, BottomSheetUtil.STANDARD_BOTTOM_SHEET_FRAGMENT_TAG)
    }
  }
}
