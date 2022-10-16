package org.tm.archive.keyboard.emoji

import org.tm.archive.components.emoji.EmojiEventListener
import org.tm.archive.keyboard.emoji.search.EmojiSearchFragment

interface EmojiKeyboardCallback :
  EmojiEventListener,
  EmojiKeyboardPageFragment.Callback,
  EmojiSearchFragment.Callback
