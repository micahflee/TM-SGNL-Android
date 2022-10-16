package org.tm.archive.stories.my

import androidx.fragment.app.Fragment
import org.tm.archive.components.FragmentWrapperActivity

class MyStoriesActivity : FragmentWrapperActivity() {
  override fun getFragment(): Fragment {
    return MyStoriesFragment()
  }
}
