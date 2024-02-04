package org.archiver.model

import org.archiver.ArchiveConstants
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId

object Recipients {

  fun Recipient.toParticipants() : List<Recipient> {
    return participantIds
      .filter { it.isNotSelf() }
      .take(ArchiveConstants.MAX_MEMBER_NAMES)
      .map(Recipient::resolved)
  }

  fun Recipient.toParticipants(predicate: (id: RecipientId) -> Boolean) : List<Recipient> {
    return participantIds
      .filter { it.isNotSelf() && predicate(it) }
      .take(ArchiveConstants.MAX_MEMBER_NAMES)
      .map(Recipient::resolved)
  }

  fun Recipient.toParticipant(predicate: (id: RecipientId) -> Boolean) : Recipient? {
    return participantIds.firstOrNull { it.isNotSelf() && predicate(it) }?.let(Recipient::resolved)
  }

  fun RecipientId.isNotSelf() = !isSelf()

  fun RecipientId.isSelf() = ApplicationDependencies.getRecipientCache().selfId == this
}