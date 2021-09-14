package org.tm.archive.components.emoji.parsing;


import androidx.annotation.NonNull;

import org.tm.archive.emoji.EmojiPage;

public class EmojiDrawInfo {

  private final EmojiPage page;
  private final int       index;

  public EmojiDrawInfo(final @NonNull EmojiPage page, final int index) {
    this.page  = page;
    this.index = index;
  }

  public @NonNull EmojiPage getPage() {
    return page;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public @NonNull String toString() {
    return "DrawInfo{" +
        "page=" + page +
        ", index=" + index +
        '}';
  }
}