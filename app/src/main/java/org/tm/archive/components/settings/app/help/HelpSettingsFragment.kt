package org.tm.archive.components.settings.app.help

import androidx.navigation.Navigation
import org.tm.archive.BuildConfig
import org.tm.archive.R
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsAdapter
import org.tm.archive.components.settings.DSLSettingsFragment
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.configure
import org.tm.archive.util.navigation.safeNavigate

class HelpSettingsFragment : DSLSettingsFragment(R.string.preferences__help) {

  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    adapter.submitList(getConfiguration().toMappingModelList())
  }

  fun getConfiguration(): DSLConfiguration {
    return configure {
      externalLinkPref(
        title = DSLSettingsText.from(R.string.HelpSettingsFragment__support_center),
        linkId = R.string.support_center_url
      )

      clickPref(
        title = DSLSettingsText.from(R.string.HelpSettingsFragment__contact_us),
        onClick = {
          Navigation.findNavController(requireView()).safeNavigate(R.id.action_helpSettingsFragment_to_helpFragment)
        }
      )

      dividerPref()

      textPref(
        title = DSLSettingsText.from(R.string.HelpSettingsFragment__version),
        summary = DSLSettingsText.from(BuildConfig.VERSION_NAME)
      )

      clickPref(
        title = DSLSettingsText.from(R.string.HelpSettingsFragment__debug_log),
        onClick = {
          Navigation.findNavController(requireView()).safeNavigate(R.id.action_helpSettingsFragment_to_submitDebugLogActivity)
        }
      )

      externalLinkPref(
        title = DSLSettingsText.from(R.string.HelpSettingsFragment__terms_amp_privacy_policy),
        linkId = R.string.terms_and_privacy_policy_url
      )

      textPref(
        summary = DSLSettingsText.from(
          StringBuilder().apply {
            append(getString(R.string.HelpFragment__copyright_signal_messenger))
            append("\n")
            append(getString(R.string.HelpFragment__licenced_under_the_gplv3))
          }
        )
      )
    }
  }
}
