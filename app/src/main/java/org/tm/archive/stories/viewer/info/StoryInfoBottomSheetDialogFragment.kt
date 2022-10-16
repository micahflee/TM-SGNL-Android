package org.tm.archive.stories.viewer.info

import android.content.DialogInterface
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import org.tm.archive.R
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsAdapter
import org.tm.archive.components.settings.DSLSettingsBottomSheetFragment
import org.tm.archive.components.settings.configure
import org.tm.archive.util.LifecycleDisposable
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

      state.sections.map { (section, recipients) ->
        renderSection(section, recipients)
      }
    }
  }

  private fun DSLConfiguration.renderSection(sectionKey: StoryInfoState.SectionKey, recipients: List<StoryInfoRecipientRow.Model>) {
    sectionHeaderPref(
      title = when (sectionKey) {
        StoryInfoState.SectionKey.FAILED -> R.string.StoryInfoBottomSheetDialogFragment__failed
        StoryInfoState.SectionKey.SENT_TO -> R.string.StoryInfoBottomSheetDialogFragment__sent_to
        StoryInfoState.SectionKey.SENT_FROM -> R.string.StoryInfoBottomSheetDialogFragment__sent_from
      }
    )

    recipients.forEach {
      customPref(it)
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
