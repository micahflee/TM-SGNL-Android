package org.tm.archive.stories.viewer.reply.direct

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.tm.archive.R
import org.tm.archive.components.KeyboardEntryDialogFragment
import org.tm.archive.components.emoji.EmojiEventListener
import org.tm.archive.components.emoji.MediaKeyboard
import org.tm.archive.database.model.MediaMmsMessageRecord
import org.tm.archive.keyboard.KeyboardPage
import org.tm.archive.keyboard.KeyboardPagerViewModel
import org.tm.archive.keyboard.emoji.EmojiKeyboardPageFragment
import org.tm.archive.keyboard.emoji.search.EmojiSearchFragment
import org.tm.archive.reactions.any.ReactWithAnyEmojiBottomSheetDialogFragment
import org.tm.archive.recipients.RecipientId
import org.tm.archive.stories.viewer.page.StoryViewerPageViewModel
import org.tm.archive.stories.viewer.reply.composer.StoryReactionBar
import org.tm.archive.stories.viewer.reply.composer.StoryReplyComposer
import org.tm.archive.util.FragmentDialogs.displayInDialogAboveAnchor
import org.tm.archive.util.LifecycleDisposable
import org.tm.archive.util.ViewUtil

/**
 * Dialog displayed when the user decides to send a private reply to a story.
 */
class StoryDirectReplyDialogFragment :
  KeyboardEntryDialogFragment(R.layout.stories_reply_to_story_fragment),
  EmojiKeyboardPageFragment.Callback,
  EmojiEventListener,
  EmojiSearchFragment.Callback,
  ReactWithAnyEmojiBottomSheetDialogFragment.Callback {

  private val lifecycleDisposable = LifecycleDisposable()
  private var isRequestingReactWithAny = false
  private var isReactClosingAfterSend = false

  override val themeResId: Int = R.style.Theme_Signal_RoundedBottomSheet_Stories

  private val viewModel: StoryDirectReplyViewModel by viewModels(
    factoryProducer = {
      StoryDirectReplyViewModel.Factory(storyId, recipientId, StoryDirectReplyRepository(requireContext()))
    }
  )

  private val keyboardPagerViewModel: KeyboardPagerViewModel by viewModels(
    ownerProducer = { requireActivity() }
  )

  private val storyViewerPageViewModel: StoryViewerPageViewModel by viewModels(
    ownerProducer = { requireParentFragment() }
  )

  private lateinit var composer: StoryReplyComposer

  private val storyId: Long
    get() = requireArguments().getLong(ARG_STORY_ID)

  private val recipientId: RecipientId?
    get() = requireArguments().getParcelable(ARG_RECIPIENT_ID)

  override val withDim: Boolean = true

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    lifecycleDisposable.bindTo(viewLifecycleOwner)

    composer = view.findViewById(R.id.input)
    composer.callback = object : StoryReplyComposer.Callback {
      override fun onSendActionClicked() {
        lifecycleDisposable += viewModel.sendReply(composer.consumeInput().first)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe {
            Toast.makeText(requireContext(), R.string.StoryDirectReplyDialogFragment__sending_reply, Toast.LENGTH_LONG).show()
            dismissAllowingStateLoss()
          }
      }

      override fun onPickReactionClicked() {
        displayInDialogAboveAnchor(composer.reactionButton, R.layout.stories_reaction_bar_layout) { dialog, view ->
          view.findViewById<StoryReactionBar>(R.id.reaction_bar).apply {
            callback = object : StoryReactionBar.Callback {
              override fun onTouchOutsideOfReactionBar() {
                dialog.dismiss()
              }

              override fun onReactionSelected(emoji: String) {
                dialog.dismiss()
                sendReaction(emoji)
              }

              override fun onOpenReactionPicker() {
                dialog.dismiss()
                isRequestingReactWithAny = true
                ReactWithAnyEmojiBottomSheetDialogFragment.createForStory().show(childFragmentManager, null)
              }
            }
            animateIn()
          }
        }
      }

      override fun onInitializeEmojiDrawer(mediaKeyboard: MediaKeyboard) {
        keyboardPagerViewModel.setOnlyPage(KeyboardPage.EMOJI)
        mediaKeyboard.setFragmentManager(childFragmentManager)
      }
    }

    viewModel.state.observe(viewLifecycleOwner) { state ->
      if (state.groupDirectReplyRecipient != null) {
        composer.displayPrivacyChrome(state.groupDirectReplyRecipient)
      } else if (state.storyRecord != null) {
        composer.displayPrivacyChrome(state.storyRecord.recipient)
      }

      if (state.storyRecord != null) {
        composer.setQuote(state.storyRecord as MediaMmsMessageRecord)
      }
    }
  }

  override fun onResume() {
    super.onResume()

    ViewUtil.focusAndShowKeyboard(composer.input)
  }

  override fun onPause() {
    super.onPause()

    ViewUtil.hideKeyboard(requireContext(), composer.input)
  }

  override fun openEmojiSearch() {
    composer.openEmojiSearch()
  }

  override fun onKeyboardHidden() {
    if (!composer.isRequestingEmojiDrawer && !isRequestingReactWithAny) {
      super.onKeyboardHidden()
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    storyViewerPageViewModel.setIsDisplayingDirectReplyDialog(false)
  }

  companion object {
    const val REQUEST_EMOJI = "request.code.emoji"

    private const val ARG_STORY_ID = "arg.story.id"
    private const val ARG_RECIPIENT_ID = "arg.recipient.id"

    fun create(storyId: Long, recipientId: RecipientId? = null): DialogFragment {
      return StoryDirectReplyDialogFragment().apply {
        arguments = Bundle().apply {
          putLong(ARG_STORY_ID, storyId)
          putParcelable(ARG_RECIPIENT_ID, recipientId)
        }
      }
    }
  }

  override fun onEmojiSelected(emoji: String?) {
    composer.onEmojiSelected(emoji)
  }

  override fun closeEmojiSearch() {
    composer.closeEmojiSearch()
  }

  override fun onKeyEvent(keyEvent: KeyEvent?) = Unit

  override fun onReactWithAnyEmojiDialogDismissed() {
    isRequestingReactWithAny = false
    if (!isReactClosingAfterSend) {
      ViewUtil.focusAndShowKeyboard(composer.input)
    }
  }

  override fun onReactWithAnyEmojiSelected(emoji: String) {
    sendReaction(emoji)
    isReactClosingAfterSend = true
  }

  private fun sendReaction(emoji: String) {
    lifecycleDisposable += viewModel.sendReaction(emoji)
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe {
        setFragmentResult(
          REQUEST_EMOJI,
          Bundle().apply {
            putString(REQUEST_EMOJI, emoji)
          }
        )
        dismissAllowingStateLoss()
      }
  }
}
