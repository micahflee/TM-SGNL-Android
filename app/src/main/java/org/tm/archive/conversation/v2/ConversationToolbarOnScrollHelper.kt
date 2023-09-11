package org.tm.archive.conversation.v2

import android.app.Activity
import android.view.View
import androidx.annotation.ColorRes
import androidx.lifecycle.LifecycleOwner
import org.tm.archive.R
import org.tm.archive.util.Material3OnScrollHelper
import org.tm.archive.wallpaper.ChatWallpaper

/**
 * Scroll helper to manage the color state of the top bar and status bar.
 */
class ConversationToolbarOnScrollHelper(
  activity: Activity,
  toolbarBackground: View,
  private val wallpaperProvider: () -> ChatWallpaper?,
  lifecycleOwner: LifecycleOwner
) : Material3OnScrollHelper(
  activity = activity,
  views = listOf(toolbarBackground),
  lifecycleOwner = lifecycleOwner
) {
  override val activeColorSet: ColorSet
    get() = ColorSet(getActiveToolbarColor(wallpaperProvider() != null))

  override val inactiveColorSet: ColorSet
    get() = ColorSet(getInactiveToolbarColor(wallpaperProvider() != null))

  @ColorRes
  private fun getActiveToolbarColor(hasWallpaper: Boolean): Int {
    return if (hasWallpaper) R.color.conversation_toolbar_color_wallpaper_scrolled else R.color.signal_colorSurface2
  }

  @ColorRes
  private fun getInactiveToolbarColor(hasWallpaper: Boolean): Int {
    return if (hasWallpaper) R.color.conversation_toolbar_color_wallpaper else R.color.signal_colorBackground
  }
}
