package org.tm.archive.search

import org.tm.archive.database.model.ThreadRecord

data class ThreadSearchResult(val results: List<ThreadRecord>, val query: String)
