package org.tm.archive.database;

import net.zetetic.database.sqlcipher.SQLiteDatabase;

/**
 * Simple interface for common methods across our various
 * {@link net.zetetic.database.sqlcipher.SQLiteOpenHelper}s.
 */
public interface SignalDatabaseOpenHelper {
  SQLiteDatabase getSqlCipherDatabase();
  String getDatabaseName();
}
