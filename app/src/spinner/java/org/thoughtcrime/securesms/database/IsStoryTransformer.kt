package org.tm.archive.database

import android.database.Cursor
import org.signal.core.util.requireInt
import org.signal.spinner.ColumnTransformer
import org.tm.archive.database.model.StoryType.Companion.fromCode

object IsStoryTransformer : ColumnTransformer {
  override fun matches(tableName: String?, columnName: String): Boolean {
    return columnName == MmsDatabase.STORY_TYPE && (tableName == null || tableName == MmsDatabase.TABLE_NAME)
  }

  override fun transform(tableName: String?, columnName: String, cursor: Cursor): String {
    val storyType = fromCode(cursor.requireInt(MmsDatabase.STORY_TYPE))
    return "${cursor.requireInt(MmsDatabase.STORY_TYPE)}<br><br>$storyType"
  }
}
