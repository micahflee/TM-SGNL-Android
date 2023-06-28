package org.tm.archive.components.settings.conversation

import org.tm.archive.util.DynamicNoActionBarTheme
import org.tm.archive.util.DynamicTheme

class CallInfoActivity : ConversationSettingsActivity(), ConversationSettingsFragment.Callback {

  override val dynamicTheme: DynamicTheme = DynamicNoActionBarTheme()
}
