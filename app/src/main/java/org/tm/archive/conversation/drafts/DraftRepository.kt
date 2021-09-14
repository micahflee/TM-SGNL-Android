package org.tm.archive.conversation.drafts

import android.content.Context
import android.net.Uri
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.database.DraftDatabase
import org.tm.archive.providers.BlobProvider

class DraftRepository(private val context: Context) {
  fun deleteVoiceNoteDraft(draft: DraftDatabase.Draft) {
    deleteBlob(Uri.parse(draft.value).buildUpon().clearQuery().build())
  }

  fun deleteBlob(uri: Uri) {
    SignalExecutors.BOUNDED.execute {
      BlobProvider.getInstance().delete(context, uri)
    }
  }
}
