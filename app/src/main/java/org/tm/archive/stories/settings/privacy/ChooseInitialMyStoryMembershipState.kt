package org.tm.archive.stories.settings.privacy

import org.tm.archive.recipients.RecipientId
import org.tm.archive.stories.settings.my.MyStoryPrivacyState

data class ChooseInitialMyStoryMembershipState(val recipientId: RecipientId? = null, val privacyState: MyStoryPrivacyState = MyStoryPrivacyState())
