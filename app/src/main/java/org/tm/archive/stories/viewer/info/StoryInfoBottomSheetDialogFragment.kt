package org.tm.archive.stories.viewer.info

import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import org.signal.core.util.concurrent.LifecycleDisposable
import org.tm.archive.R
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsAdapter
import org.tm.archive.components.settings.DSLSettingsBottomSheetFragment
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.configure
import org.tm.archive.util.fragments.findListener

/**
 * Bottom sheet which displays receipt information to the user for a given story.
 */
class StoryInfoBottomSheetDialogFragment : DSLSettingsBottomSheetFragment() {

  override val peekHeightPercentage: Float = 0.5f

  companion object {
    private const val STORY_ID = "args.story.id"

    fun create(storyId: Long): StoryInfoBottomSheetDialogFragment {
      return StoryInfoBottomSheetDialogFragment().apply {
        arguments = bundleOf(STORY_ID to storyId)
      }
    }
  }

  private val storyId: Long get() = requireArguments().getLong(STORY_ID)

  private val viewModel: StoryInfoViewModel by viewModels(factoryProducer = {
    StoryInfoViewModel.Factory(storyId)
  })

  private val lifecycleDisposable = LifecycleDisposable()

  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    StoryInfoHeader.register(adapter)
    StoryInfoRecipientRow.register(adapter)

    lifecycleDisposable.bindTo(viewLifecycleOwner)
    lifecycleDisposable += viewModel.state.subscribe { state ->
      if (state.isLoaded) {
        adapter.submitList(getConfiguration(state).toMappingModelList())
      }
    }
  }

  private fun getConfiguration(state: StoryInfoState): DSLConfiguration {
    return configure {
      customPref(
        StoryInfoHeader.Model(
          sentMillis = state.sentMillis,
          receivedMillis = state.receivedMillis,
          size = state.size
        )
      )

      val details = state.messageDetails!!

      if (state.isOutgoing) {
        renderSection(
          title = R.string.message_details_recipient_header__not_sent,
          recipients = details.notSent.map { StoryInfoRecipientRow.Model(it) }
        )

        renderSection(
          title = R.string.message_details_recipient_header__viewed,
          recipients = details.viewed.map { StoryInfoRecipientRow.Model(it) }
        )

        renderSection(
          title = R.string.message_details_recipient_header__read_by,
          recipients = details.read.map { StoryInfoRecipientRow.Model(it) }
        )

        renderSection(
          title = R.string.message_details_recipient_header__delivered_to,
          recipients = details.delivered.map { StoryInfoRecipientRow.Model(it) }
        )

        renderSection(
          title = R.string.message_details_recipient_header__sent_to,
          recipients = details.sent.map { StoryInfoRecipientRow.Model(it) }
        )

        renderSection(
          title = R.string.message_details_recipient_header__pending_send,
          recipients = details.pending.map { StoryInfoRecipientRow.Model(it) }
        )

        renderSection(
          title = R.string.message_details_recipient_header__skipped,
          recipients = details.skipped.map { StoryInfoRecipientRow.Model(it) }
        )
      } else {
        renderSection(
          title = R.string.message_details_recipient_header__sent_from,
          recipients = details.sent.map { StoryInfoRecipientRow.Model(it) }
        )
      }
    }
  }

  private fun DSLConfiguration.renderSection(@StringRes title: Int, recipients: List<StoryInfoRecipientRow.Model>) {
    if (recipients.isNotEmpty()) {
      sectionHeaderPref(
        title = DSLSettingsText.from(title)
      )

      recipients.forEach {
        customPref(it)
      }
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    findListener<OnInfoSheetDismissedListener>()?.onInfoSheetDismissed()
  }

  interface OnInfoSheetDismissedListener {
    fun onInfoSheetDismissed()
  }
}
