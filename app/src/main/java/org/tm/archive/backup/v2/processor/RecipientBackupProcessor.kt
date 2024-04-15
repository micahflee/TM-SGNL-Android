/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.backup.v2.processor

import org.signal.core.util.logging.Log
import org.tm.archive.backup.v2.BackupState
import org.tm.archive.backup.v2.ExportState
import org.tm.archive.backup.v2.database.BackupRecipient
import org.tm.archive.backup.v2.database.getAllForBackup
import org.tm.archive.backup.v2.database.getContactsForBackup
import org.tm.archive.backup.v2.database.getGroupsForBackup
import org.tm.archive.backup.v2.database.restoreRecipientFromBackup
import org.tm.archive.backup.v2.proto.Frame
import org.tm.archive.backup.v2.proto.ReleaseNotes
import org.tm.archive.backup.v2.stream.BackupFrameEmitter
import org.tm.archive.database.SignalDatabase
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient

typealias BackupRecipient = org.tm.archive.backup.v2.proto.Recipient

object RecipientBackupProcessor {

  val TAG = Log.tag(RecipientBackupProcessor::class.java)

  fun export(state: ExportState, emitter: BackupFrameEmitter) {
    val selfId = Recipient.self().id.toLong()
    val releaseChannelId = SignalStore.releaseChannelValues().releaseChannelRecipientId
    if (releaseChannelId != null) {
      emitter.emit(
        Frame(
          recipient = BackupRecipient(
            id = releaseChannelId.toLong(),
            releaseNotes = ReleaseNotes()
          )
        )
      )
    }

    SignalDatabase.recipients.getContactsForBackup(selfId).use { reader ->
      for (backupRecipient in reader) {
        if (backupRecipient != null) {
          state.recipientIds.add(backupRecipient.id)
          emitter.emit(Frame(recipient = backupRecipient))
        }
      }
    }

    SignalDatabase.recipients.getGroupsForBackup().use { reader ->
      for (backupRecipient in reader) {
        state.recipientIds.add(backupRecipient.id)
        emitter.emit(Frame(recipient = backupRecipient))
      }
    }

    SignalDatabase.distributionLists.getAllForBackup().forEach {
      state.recipientIds.add(it.id)
      emitter.emit(Frame(recipient = it))
    }
  }

  fun import(recipient: BackupRecipient, backupState: BackupState) {
    val newId = SignalDatabase.recipients.restoreRecipientFromBackup(recipient, backupState)
    if (newId != null) {
      backupState.backupToLocalRecipientId[recipient.id] = newId
    }
  }
}
