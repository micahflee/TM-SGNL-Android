package org.tm.archive.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.tm.archive.R
import org.tm.archive.preferences.BackupsPreferenceFragment

class WrappedBackupsPreferenceFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.setTitle(R.string.BackupsPreferenceFragment__chat_backups)
    return BackupsPreferenceFragment()
  }
}
