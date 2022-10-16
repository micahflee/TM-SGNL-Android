package org.tm.archive.stories.viewer.text

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.signal.core.util.DimensionUnit
import org.tm.archive.R
import org.tm.archive.linkpreview.LinkPreview
import org.tm.archive.mediapreview.MediaPreviewFragment
import org.tm.archive.stories.StoryTextPostView
import org.tm.archive.stories.viewer.page.StoryPost
import org.tm.archive.util.CommunicationActions
import org.tm.archive.util.FragmentDialogs.displayInDialogAboveAnchor
import org.tm.archive.util.fragments.requireListener

class StoryTextPostPreviewFragment : Fragment(R.layout.stories_text_post_preview_fragment) {

  companion object {
    private const val STORY_ID = "STORY_ID"

    fun create(content: StoryPost.Content.TextContent): Fragment {
      return StoryTextPostPreviewFragment().apply {
        arguments = Bundle().apply {
          putParcelable(MediaPreviewFragment.DATA_URI, content.uri)
          putLong(STORY_ID, content.recordId)
        }
      }
    }
  }

  private val viewModel: StoryTextPostViewModel by viewModels(
    factoryProducer = {
      StoryTextPostViewModel.Factory(requireArguments().getLong(STORY_ID), StoryTextPostRepository())
    }
  )

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val storyTextPostView: StoryTextPostView = view.findViewById(R.id.story_text_post)

    viewModel.state.observe(viewLifecycleOwner) { state ->
      when (state.loadState) {
        StoryTextPostState.LoadState.INIT -> Unit
        StoryTextPostState.LoadState.LOADED -> {
          storyTextPostView.bindFromStoryTextPost(state.storyTextPost!!)
          storyTextPostView.bindLinkPreview(state.linkPreview, state.storyTextPost.body.isBlank())
          storyTextPostView.postAdjustLinkPreviewTranslationY()

          if (state.linkPreview != null) {
            storyTextPostView.setLinkPreviewClickListener {
              showLinkPreviewTooltip(it, state.linkPreview)
            }
          } else {
            storyTextPostView.setLinkPreviewClickListener(null)
          }
        }
        StoryTextPostState.LoadState.FAILED -> {
          requireListener<MediaPreviewFragment.Events>().mediaNotAvailable()
        }
      }

      if (state.typeface != null) {
        storyTextPostView.setTypeface(state.typeface)
      }

      if (state.typeface != null && state.loadState == StoryTextPostState.LoadState.LOADED) {
        requireListener<MediaPreviewFragment.Events>().onMediaReady()
      }
    }
  }

  @SuppressLint("AlertDialogBuilderUsage")
  private fun showLinkPreviewTooltip(view: View, linkPreview: LinkPreview) {
    requireListener<Callback>().setIsDisplayingLinkPreviewTooltip(true)

    val contentView = LayoutInflater.from(requireContext()).inflate(R.layout.stories_link_popup, null, false)

    contentView.findViewById<TextView>(R.id.url).text = linkPreview.url
    contentView.setOnClickListener {
      CommunicationActions.openBrowserLink(requireContext(), linkPreview.url)
    }

    contentView.measure(
      View.MeasureSpec.makeMeasureSpec(DimensionUnit.DP.toPixels(275f).toInt(), View.MeasureSpec.EXACTLY),
      0
    )

    contentView.layout(0, 0, contentView.measuredWidth, contentView.measuredHeight)

    displayInDialogAboveAnchor(view, contentView, windowDim = 0f)
  }

  interface Callback {
    fun setIsDisplayingLinkPreviewTooltip(isDisplayingLinkPreviewTooltip: Boolean)
  }
}
