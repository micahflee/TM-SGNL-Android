package org.tm.archive.wallpaper;

import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tm.archive.R;
import org.tm.archive.util.MappingAdapter;

class ChatWallpaperSelectionAdapter extends MappingAdapter {
  ChatWallpaperSelectionAdapter(@Nullable ChatWallpaperViewHolder.EventListener eventListener, @NonNull DisplayMetrics windowDisplayMetrics) {
    registerFactory(ChatWallpaperSelectionMappingModel.class, ChatWallpaperViewHolder.createFactory(R.layout.chat_wallpaper_selection_fragment_adapter_item, eventListener, windowDisplayMetrics));
  }
}
