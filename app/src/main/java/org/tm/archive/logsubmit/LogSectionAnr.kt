/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.logsubmit

import android.content.Context
import org.tm.archive.database.LogDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogSectionAnr : LogSection {

  companion object {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz", Locale.US)
  }

  override fun getTitle(): String = "ANR"

  override fun getContent(context: Context): CharSequence {
    val anrs = LogDatabase.getInstance(ApplicationDependencies.getApplication()).anrs.getAll()

    return if (anrs.isEmpty()) {
      "None"
    } else {
      "\n" + anrs.joinToString(separator = "\n\n") {
        val date = dateFormat.format(Date(it.createdAt))
        "------------- $date -------------\n${it.threadDump}"
      }
    }
  }
}
