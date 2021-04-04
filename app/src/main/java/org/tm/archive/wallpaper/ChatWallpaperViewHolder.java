package org.tm.archive.wallpaper;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.tm.archive.R;
import org.tm.archive.util.DisplayMetricsUtil;
import org.tm.archive.util.MappingAdapter;
import org.tm.archive.util.MappingViewHolder;

class ChatWallpaperViewHolder extends MappingViewHolder<ChatWallpaperSelectionMappingModel> {

  private final ImageView     preview;
  private final View          dimmer;
  private final EventListener eventListener;

  public ChatWallpaperViewHolder(@NonNull View itemView, @Nullable EventListener eventListener, @Nullable DisplayMetrics windowDisplayMetrics) {
    super(itemView);
    this.preview       = itemView.findViewById(R.id.chat_wallpaper_preview);
    this.dimmer        = itemView.findViewById(R.id.chat_wallpaper_dim);
    this.eventListener = eventListener;

    if (windowDisplayMetrics != null) {
      DisplayMetricsUtil.forceAspectRatioToScreenByAdjustingHeight(windowDisplayMetrics, itemView);
    }
  }

  @Override
  public void bind(@NonNull ChatWallpaperSelectionMappingModel model) {
    model.loadInto(preview);

    ChatWallpaperDimLevelUtil.applyDimLevelForNightMode(dimmer, model.getWallpaper());

    if (eventListener != null) {
      preview.setOnClickListener(unused -> {
        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
          eventListener.onModelClick(model);
        }
      });
    }
  }

  public static @NonNull MappingAdapter.Factory<ChatWallpaperSelectionMappingModel> createFactory(@LayoutRes int layout, @Nullable EventListener listener, @Nullable DisplayMetrics windowDisplayMetrics) {
    return new MappingAdapter.LayoutFactory<>(view -> new ChatWallpaperViewHolder(view, listener, windowDisplayMetrics), layout);
  }

  public interface EventListener {
    default void onModelClick(@NonNull ChatWallpaperSelectionMappingModel model) {
      onClick(model.getWallpaper());
    }

    void onClick(@NonNull ChatWallpaper chatWallpaper);
  }
}
