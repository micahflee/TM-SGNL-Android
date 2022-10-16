package org.tm.archive.badges.gifts.flow

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import org.tm.archive.R

/**
 * Wraps the google pay button in a convenient frame layout.
 */
class GooglePayButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
  init {
    inflate(context, R.layout.donate_with_googlepay_button, this)
  }

  fun setOnGooglePayClickListener(action: () -> Unit) {
    getChildAt(0).setOnClickListener { action() }
  }
}
