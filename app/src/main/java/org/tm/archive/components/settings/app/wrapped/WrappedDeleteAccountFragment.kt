package org.tm.archive.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.tm.archive.R
import org.tm.archive.delete.DeleteAccountFragment

class WrappedDeleteAccountFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.setTitle(R.string.preferences__delete_account)
    return DeleteAccountFragment()
  }
}
