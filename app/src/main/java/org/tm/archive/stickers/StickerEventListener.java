package org.tm.archive.stickers;

import androidx.annotation.NonNull;

import org.tm.archive.database.model.StickerRecord;

public interface StickerEventListener {
  void onStickerSelected(@NonNull StickerRecord sticker);

  void onStickerManagementClicked();
}
