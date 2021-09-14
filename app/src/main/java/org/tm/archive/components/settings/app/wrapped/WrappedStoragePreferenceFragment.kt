package org.tm.archive.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.tm.archive.preferences.StoragePreferenceFragment

class WrappedStoragePreferenceFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    return StoragePreferenceFragment()
  }
}
