package org.tm.archive.registration

interface VerifyProcessor {
  fun hasResult(): Boolean
  fun isServerSentError(): Boolean
}
