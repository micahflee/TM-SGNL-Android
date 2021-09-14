package org.tm.archive.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.tm.archive.R
import org.tm.archive.help.HelpFragment

class WrappedHelpFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.title = getString(R.string.preferences__help)

    val fragment = HelpFragment()
    fragment.arguments = arguments

    return fragment
  }
}
