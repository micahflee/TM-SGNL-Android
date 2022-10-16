package org.tm.archive.database;

interface ThreadIdDatabaseReference {
  void remapThread(long fromId, long toId);
}
