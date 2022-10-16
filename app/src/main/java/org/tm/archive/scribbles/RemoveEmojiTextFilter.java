package org.tm.archive.scribbles;

import androidx.annotation.NonNull;

import org.signal.imageeditor.core.HiddenEditText;
import org.tm.archive.components.emoji.EmojiUtil;

class RemoveEmojiTextFilter implements HiddenEditText.TextFilter {
  @Override
  public String filter(@NonNull String text) {
    return EmojiUtil.stripEmoji(text);
  }
}
