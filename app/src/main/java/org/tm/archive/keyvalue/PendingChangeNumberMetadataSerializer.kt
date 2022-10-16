package org.tm.archive.keyvalue

import org.signal.core.util.ByteSerializer
import org.tm.archive.database.model.databaseprotos.PendingChangeNumberMetadata

/**
 * Serialize [PendingChangeNumberMetadata]
 */
object PendingChangeNumberMetadataSerializer : ByteSerializer<PendingChangeNumberMetadata> {
  override fun serialize(data: PendingChangeNumberMetadata): ByteArray = data.toByteArray()
  override fun deserialize(data: ByteArray): PendingChangeNumberMetadata = PendingChangeNumberMetadata.parseFrom(data)
}
