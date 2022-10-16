package org.tm.archive.stories.viewer.page

import android.view.ViewStub
import androidx.core.view.isVisible
import org.tm.archive.blurhash.BlurHash
import org.tm.archive.stories.StoryFirstTimeNavigationView
import org.tm.archive.util.views.Stub

/**
 * Specialized stub that allows for early arrival of the blurhash and callback.
 */
class StoryFirstNavigationStub(viewStub: ViewStub) : Stub<StoryFirstTimeNavigationView>(viewStub) {

  private var callback: StoryFirstTimeNavigationView.Callback? = null
  private var blurHash: BlurHash? = null

  fun setCallback(callback: StoryFirstTimeNavigationView.Callback) {
    if (resolved()) {
      get().callback = callback
    } else {
      this.callback = callback
    }
  }

  fun setBlurHash(blurHash: BlurHash?) {
    if (resolved()) {
      get().setBlurHash(blurHash)
    } else {
      this.blurHash = blurHash
    }
  }

  fun showIfAble(ableToShow: Boolean) {
    if (ableToShow) {
      get().show()
    }
  }

  fun isVisible(): Boolean {
    return resolved() && get().isVisible
  }

  fun hide() {
    if (resolved()) {
      get().hide()
    }
  }

  override fun get(): StoryFirstTimeNavigationView {
    val needsResolve = !resolved()
    val view = super.get()
    if (needsResolve) {
      view.setBlurHash(blurHash)
      view.callback = callback
      blurHash = null
      callback = null
    }

    return view
  }
}
