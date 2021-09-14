package org.tm.archive.components.settings.app

import org.tm.archive.recipients.Recipient

data class AppSettingsState(val self: Recipient, val unreadPaymentsCount: Int)
