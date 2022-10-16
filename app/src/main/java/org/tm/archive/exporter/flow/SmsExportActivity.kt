package org.tm.archive.exporter.flow

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import org.tm.archive.R
import org.tm.archive.components.FragmentWrapperActivity
import org.tm.archive.util.WindowUtil

class SmsExportActivity : FragmentWrapperActivity() {

  override fun onResume() {
    super.onResume()
    WindowUtil.setLightStatusBarFromTheme(this)
  }

  override fun getFragment(): Fragment {
    return NavHostFragment.create(R.navigation.sms_export)
  }

  companion object {
    fun createIntent(context: Context): Intent = Intent(context, SmsExportActivity::class.java)
  }
}
