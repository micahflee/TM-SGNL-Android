package org.archiver.model

import com.tm.androidcopysdk.model.IArchiveType


enum class SignalArchiveType(override val key: String): IArchiveType {
  AppMessage("Signal message"),
  Sms("SMS"),
  ;

  companion object {
    fun coreValues(): Array<IArchiveType> = values().map { it }.toTypedArray()
  }
}