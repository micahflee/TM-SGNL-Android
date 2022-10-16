package org.tm.archive.stories.viewer.text

import android.graphics.Typeface
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.database.model.databaseprotos.StoryTextPost
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.fonts.TextFont
import org.tm.archive.fonts.TextToScript
import org.tm.archive.fonts.TypefaceCache
import org.tm.archive.util.Base64

class StoryTextPostRepository {
  fun getRecord(recordId: Long): Single<MmsMessageRecord> {
    return Single.fromCallable {
      SignalDatabase.mms.getMessageRecord(recordId) as MmsMessageRecord
    }.subscribeOn(Schedulers.io())
  }

  fun getTypeface(recordId: Long): Single<Typeface> {
    return getRecord(recordId).flatMap {
      val model = StoryTextPost.parseFrom(Base64.decode(it.body))
      val textFont = TextFont.fromStyle(model.style)
      val script = TextToScript.guessScript(model.body)

      TypefaceCache.get(ApplicationDependencies.getApplication(), textFont, script)
    }
  }
}
