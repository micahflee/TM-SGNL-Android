package org.tm.archive.search

import org.tm.archive.recipients.Recipient

data class ContactSearchResult(val results: List<Recipient>, val query: String)
