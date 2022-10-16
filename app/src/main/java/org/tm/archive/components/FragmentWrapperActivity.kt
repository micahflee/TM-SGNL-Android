package org.tm.archive.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.tm.archive.PassphraseRequiredActivity
import org.tm.archive.R
import org.tm.archive.util.DynamicNoActionBarTheme
import org.tm.archive.util.DynamicTheme

/**
 * Activity that wraps a given fragment
 */
abstract class FragmentWrapperActivity : PassphraseRequiredActivity() {

  protected open val dynamicTheme: DynamicTheme = DynamicNoActionBarTheme()
  protected open val contentViewId: Int = R.layout.fragment_container

  override fun onCreate(savedInstanceState: Bundle?, ready: Boolean) {
    super.onCreate(savedInstanceState, ready)
    setContentView(contentViewId)
    dynamicTheme.onCreate(this)

    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, getFragment())
        .commit()
    }
  }

  abstract fun getFragment(): Fragment

  override fun onResume() {
    super.onResume()
    dynamicTheme.onResume(this)
  }
}
