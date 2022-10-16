package org.tm.archive.database;

interface RecipientIdDatabaseReference {
  void remapRecipient(RecipientId fromId, RecipientId toId);
}
