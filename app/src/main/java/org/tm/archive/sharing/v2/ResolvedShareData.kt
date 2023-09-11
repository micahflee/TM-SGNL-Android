package org.tm.archive.sharing.v2

import android.net.Uri
import org.tm.archive.sharing.MultiShareArgs
import java.lang.UnsupportedOperationException

sealed class ResolvedShareData {

  abstract val isMmsOrSmsSupported: Boolean

  abstract fun toMultiShareArgs(): MultiShareArgs

  data class Primitive(val text: CharSequence) : ResolvedShareData() {
    override val isMmsOrSmsSupported: Boolean = true

    override fun toMultiShareArgs(): MultiShareArgs {
      return MultiShareArgs.Builder(setOf()).withDraftText(text.toString()).build()
    }
  }

  data class ExternalUri(
    val uri: Uri,
    val mimeType: String,
    val text: CharSequence?,
    override val isMmsOrSmsSupported: Boolean
  ) : ResolvedShareData() {
    override fun toMultiShareArgs(): MultiShareArgs {
      return MultiShareArgs.Builder(setOf()).withDataUri(uri).withDataType(mimeType).withDraftText(text?.toString()).build()
    }
  }

  data class Media(
    val media: List<org.tm.archive.mediasend.Media>,
    override val isMmsOrSmsSupported: Boolean
  ) : ResolvedShareData() {
    override fun toMultiShareArgs(): MultiShareArgs {
      return MultiShareArgs.Builder(setOf()).withMedia(media).build()
    }
  }

  object Failure : ResolvedShareData() {
    override val isMmsOrSmsSupported: Boolean get() = throw UnsupportedOperationException()
    override fun toMultiShareArgs(): MultiShareArgs = throw UnsupportedOperationException()
  }
}
