package org.tm.archive.components.emoji.parsing

import org.tm.archive.emoji.EmojiPage

data class EmojiDrawInfo(val page: EmojiPage, val index: Int, private val emoji: String, val rawEmoji: String?, val jumboSheet: String?)
