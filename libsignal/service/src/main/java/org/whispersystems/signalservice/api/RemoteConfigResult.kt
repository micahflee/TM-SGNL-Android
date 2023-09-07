package org.whispersystems.signalservice.api

data class RemoteConfigResult(
  val config: Map<String, Any>,
  val serverEpochTimeSeconds: Long
)
