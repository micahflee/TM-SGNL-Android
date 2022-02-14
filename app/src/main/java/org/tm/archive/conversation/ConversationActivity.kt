package org.tm.archive.conversation

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import org.tm.archive.PassphraseRequiredActivity
import org.tm.archive.R
import org.tm.archive.components.HidingLinearLayout
import org.tm.archive.components.reminder.ReminderView
import org.tm.archive.recipients.Recipient
import org.tm.archive.util.DynamicNoActionBarTheme
import org.tm.archive.util.DynamicTheme
import org.tm.archive.util.concurrent.ListenableFuture
import org.tm.archive.util.views.Stub

open class ConversationActivity : PassphraseRequiredActivity(), ConversationParentFragment.Callback {

  private lateinit var fragment: ConversationParentFragment

  private val dynamicTheme: DynamicTheme = DynamicNoActionBarTheme()
  override fun onPreCreate() {
    dynamicTheme.onCreate(this)
  }

  override fun onCreate(savedInstanceState: Bundle?, ready: Boolean) {
    setContentView(R.layout.conversation_parent_fragment_container)

    fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as ConversationParentFragment
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    fragment.onNewIntent(intent)
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    return fragment.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev)
  }

  override fun onResume() {
    super.onResume()
    dynamicTheme.onResume(this)
  }

  override fun onInitializeToolbar(toolbar: Toolbar) {
    toolbar.navigationIcon = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_left_24)
    toolbar.setNavigationOnClickListener { finish() }
  }

  fun saveDraft(): ListenableFuture<Long> {
    return fragment.saveDraft()
  }

  fun getRecipient(): Recipient {
    return fragment.recipient
  }

  fun getTitleView(): View {
    return fragment.titleView
  }

  fun getComposeText(): View {
    return fragment.composeText
  }

  fun getQuickAttachmentToggle(): HidingLinearLayout {
    return fragment.quickAttachmentToggle
  }

  fun getReminderView(): Stub<ReminderView> {
    return fragment.reminderView
  }
}
