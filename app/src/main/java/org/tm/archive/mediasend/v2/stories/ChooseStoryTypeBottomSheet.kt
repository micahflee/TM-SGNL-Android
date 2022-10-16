package org.tm.archive.mediasend.v2.stories

import org.signal.core.util.DimensionUnit
import org.tm.archive.R
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsAdapter
import org.tm.archive.components.settings.DSLSettingsBottomSheetFragment
import org.tm.archive.components.settings.DSLSettingsIcon
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.configure
import org.tm.archive.components.settings.conversation.preferences.LargeIconClickPreference
import org.tm.archive.util.fragments.requireListener

class ChooseStoryTypeBottomSheet : DSLSettingsBottomSheetFragment(
  layoutId = R.layout.dsl_settings_bottom_sheet_no_handle
) {
  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    LargeIconClickPreference.register(adapter)
    adapter.submitList(getConfiguration().toMappingModelList())
  }

  private fun getConfiguration(): DSLConfiguration {
    return configure {
      textPref(
        title = DSLSettingsText.from(
          stringId = R.string.ChooseStoryTypeBottomSheet__choose_your_story_type,
          DSLSettingsText.CenterModifier, DSLSettingsText.Body1BoldModifier, DSLSettingsText.BoldModifier
        )
      )

      customPref(
        LargeIconClickPreference.Model(
          title = DSLSettingsText.from(
            stringId = R.string.ChooseStoryTypeBottomSheet__new_private_story
          ),
          summary = DSLSettingsText.from(
            stringId = R.string.ChooseStoryTypeBottomSheet__visible_only_to
          ),
          icon = DSLSettingsIcon.from(
            R.drawable.ic_plus_24,
            R.color.signal_icon_tint_primary,
            R.drawable.circle_tintable,
            R.color.signal_button_secondary_ripple,
            DimensionUnit.DP.toPixels(8f).toInt()
          ),
          onClick = {
            dismissAllowingStateLoss()
            requireListener<Callback>().onNewStoryClicked()
          }
        )
      )

      customPref(
        LargeIconClickPreference.Model(
          title = DSLSettingsText.from(
            stringId = R.string.ChooseStoryTypeBottomSheet__group_story
          ),
          summary = DSLSettingsText.from(
            stringId = R.string.ChooseStoryTypeBottomSheet__share_to_an_existing_group
          ),
          icon = DSLSettingsIcon.from(
            R.drawable.ic_group_outline_24,
            R.color.signal_icon_tint_primary,
            R.drawable.circle_tintable,
            R.color.signal_button_secondary_ripple,
            DimensionUnit.DP.toPixels(8f).toInt()
          ),
          onClick = {
            dismissAllowingStateLoss()
            requireListener<Callback>().onGroupStoryClicked()
          }
        )
      )

      space(DimensionUnit.DP.toPixels(32f).toInt())
    }
  }

  interface Callback {
    fun onNewStoryClicked()
    fun onGroupStoryClicked()
  }
}
