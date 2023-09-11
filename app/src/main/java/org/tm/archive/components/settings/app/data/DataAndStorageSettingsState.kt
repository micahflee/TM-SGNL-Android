package org.tm.archive.components.settings.app.data

import org.tm.archive.mms.SentMediaQuality
import org.tm.archive.webrtc.CallDataMode

data class DataAndStorageSettingsState(
  val totalStorageUse: Long,
  val mobileAutoDownloadValues: Set<String>,
  val wifiAutoDownloadValues: Set<String>,
  val roamingAutoDownloadValues: Set<String>,
  val callDataMode: CallDataMode,
  val isProxyEnabled: Boolean,
  val sentMediaQuality: SentMediaQuality
)
