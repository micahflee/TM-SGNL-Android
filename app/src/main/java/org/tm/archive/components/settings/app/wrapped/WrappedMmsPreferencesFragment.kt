package org.tm.archive.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.tm.archive.R
import org.tm.archive.preferences.MmsPreferencesFragment

class WrappedMmsPreferencesFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.setTitle(R.string.preferences__advanced_mms_access_point_names)
    return MmsPreferencesFragment()
  }
}
