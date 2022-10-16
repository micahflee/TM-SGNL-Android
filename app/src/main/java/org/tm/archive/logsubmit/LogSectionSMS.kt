package org.tm.archive.logsubmit

import android.content.Context
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.util.Util

/**
 * Prints off the current SMS settings
 */

class LogSectionSMS : LogSection {
  override fun getTitle(): String = "SMS"

  override fun getContent(context: Context): CharSequence {
    val isDefaultSMS = Util.isDefaultSmsProvider(context)
    val settings = SignalStore.settings()
    val output = StringBuilder()

    output.append("Default SMS          : ${isDefaultSMS}\n")
    output.append("SMS delivery reports : ${settings.isSmsDeliveryReportsEnabled}\n")
    output.append("WiFi SMS             : ${settings.isWifiCallingCompatibilityModeEnabled}\n")

    return output
  }
}
