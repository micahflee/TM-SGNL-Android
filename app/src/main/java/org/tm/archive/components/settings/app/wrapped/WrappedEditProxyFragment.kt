package org.tm.archive.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.tm.archive.R
import org.tm.archive.preferences.EditProxyFragment

class WrappedEditProxyFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.setTitle(R.string.preferences_use_proxy)
    return EditProxyFragment()
  }
}
