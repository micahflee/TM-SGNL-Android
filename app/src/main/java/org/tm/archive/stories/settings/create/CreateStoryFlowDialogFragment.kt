package org.tm.archive.stories.settings.create

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import org.tm.archive.R
import org.tm.archive.recipients.RecipientId
import org.tm.archive.stories.settings.select.BaseStoryRecipientSelectionFragment
import org.tm.archive.util.WindowUtil

class CreateStoryFlowDialogFragment : DialogFragment(R.layout.create_story_flow_dialog_fragment), BaseStoryRecipientSelectionFragment.Callback, CreateStoryWithViewersFragment.Callback {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.Signal_DayNight_Dialog_FullScreen)
  }

  override fun exitFlow() {
    dismissAllowingStateLoss()
  }

  override fun onDone(recipientId: RecipientId) {
    setFragmentResult(
      CreateStoryWithViewersFragment.REQUEST_KEY,
      Bundle().apply {
        putParcelable(CreateStoryWithViewersFragment.STORY_RECIPIENT, recipientId)
      }
    )
    dismissAllowingStateLoss()
  }

  override fun setStatusBarColor(color: Int) {
    WindowUtil.setStatusBarColor(requireDialog().window!!, color)
  }
}
