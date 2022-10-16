package org.tm.archive.main

import android.widget.ImageView
import org.tm.archive.components.Material3SearchToolbar
import org.tm.archive.util.views.Stub

interface SearchBinder {
  fun getSearchAction(): ImageView

  fun getSearchToolbar(): Stub<Material3SearchToolbar>

  fun onSearchOpened()

  fun onSearchClosed()
}
