package org.archiver.model

enum class CallDirection {
  Incoming,
  Outgoing

  ;

  companion object {

    fun fromIsOutgoing(isOutgoing: Boolean) = if (isOutgoing) Outgoing else Incoming

  }
}