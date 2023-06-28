package org.tm.archive.stories.viewer.post

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import org.signal.core.util.DimensionUnit
import org.tm.archive.R
import org.tm.archive.linkpreview.LinkPreview
import org.tm.archive.stories.StoryTextPostView
import org.tm.archive.util.CommunicationActions
import org.tm.archive.util.FragmentDialogs.displayInDialogAboveAnchor

/**
 * Render logic for story text posts
 */
class StoryTextLoader(
  private val fragment: StoryPostFragment,
  private val text: StoryTextPostView,
  private val state: StoryPostState.TextPost,
  private val callback: StoryPostFragment.Callback
) {

  fun load() {
    text.bindFromStoryTextPost(state.storyTextPost!!, state.bodyRanges)
    text.bindLinkPreview(state.linkPreview, state.storyTextPost.body.isBlank())
    text.postAdjustLinkPreviewTranslationY()

    if (state.linkPreview != null) {
      text.setLinkPreviewClickListener {
        showLinkPreviewTooltip(it, state.linkPreview)
      }
    } else {
      text.setLinkPreviewClickListener(null)
    }

    if (state.typeface != null) {
      text.setTypeface(state.typeface)
    }

    if (state.typeface != null && state.loadState == StoryPostState.LoadState.LOADED) {
      callback.onContentReady()
    }
  }

  private fun showLinkPreviewTooltip(view: View, linkPreview: LinkPreview) {
    callback.setIsDisplayingLinkPreviewTooltip(true)

    val contentView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.stories_link_popup, null, false)

    contentView.findViewById<TextView>(R.id.url).text = linkPreview.url
    contentView.setOnClickListener {
      CommunicationActions.openBrowserLink(fragment.requireContext(), linkPreview.url)
    }

    contentView.measure(
      View.MeasureSpec.makeMeasureSpec(DimensionUnit.DP.toPixels(275f).toInt(), View.MeasureSpec.EXACTLY),
      0
    )

    contentView.layout(0, 0, contentView.measuredWidth, contentView.measuredHeight)

    fragment.displayInDialogAboveAnchor(view, contentView, windowDim = 0f, onDismiss = {
      val activity = fragment.activity
      if (activity != null) {
        callback.setIsDisplayingLinkPreviewTooltip(false)
      }
    })
  }
}
