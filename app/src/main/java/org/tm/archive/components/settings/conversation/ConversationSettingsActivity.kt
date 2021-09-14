package org.tm.archive.components.settings.conversation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import org.tm.archive.R
import org.tm.archive.components.settings.DSLSettingsActivity
import org.tm.archive.groups.GroupId
import org.tm.archive.groups.ParcelableGroupId
import org.tm.archive.recipients.RecipientId
import org.tm.archive.util.DynamicConversationSettingsTheme
import org.tm.archive.util.DynamicTheme

class ConversationSettingsActivity : DSLSettingsActivity(), ConversationSettingsFragment.Callback {

  override val dynamicTheme: DynamicTheme = DynamicConversationSettingsTheme()

  override fun onCreate(savedInstanceState: Bundle?, ready: Boolean) {
    ActivityCompat.postponeEnterTransition(this)
    super.onCreate(savedInstanceState, ready)
  }

  override fun onContentWillRender() {
    ActivityCompat.startPostponedEnterTransition(this)
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(0, R.anim.slide_fade_to_bottom)
  }

  companion object {

    @JvmStatic
    fun createTransitionBundle(context: Context, avatar: View, windowContent: View): Bundle? {
      return if (context is Activity) {
        ActivityOptionsCompat.makeSceneTransitionAnimation(
          context,
          Pair.create(avatar, "avatar"),
          Pair.create(windowContent, "window_content")
        ).toBundle()
      } else {
        null
      }
    }

    @JvmStatic
    fun createTransitionBundle(context: Context, avatar: View): Bundle? {
      return if (context is Activity) {
        ActivityOptionsCompat.makeSceneTransitionAnimation(
          context,
          avatar,
          "avatar",
        ).toBundle()
      } else {
        null
      }
    }

    @JvmStatic
    fun forGroup(context: Context, groupId: GroupId): Intent {
      val startBundle = ConversationSettingsFragmentArgs.Builder(null, ParcelableGroupId.from(groupId))
        .build()
        .toBundle()

      return getIntent(context)
        .putExtra(ARG_START_BUNDLE, startBundle)
    }

    @JvmStatic
    fun forRecipient(context: Context, recipientId: RecipientId): Intent {
      val startBundle = ConversationSettingsFragmentArgs.Builder(recipientId, null)
        .build()
        .toBundle()

      return getIntent(context)
        .putExtra(ARG_START_BUNDLE, startBundle)
    }

    private fun getIntent(context: Context): Intent {
      return Intent(context, ConversationSettingsActivity::class.java)
        .putExtra(ARG_NAV_GRAPH, R.navigation.conversation_settings)
    }
  }
}
