package org.tm.archive.giph.mp4;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import org.tm.archive.R;
import org.tm.archive.conversation.colors.ChatColorsPalette;
import org.tm.archive.giph.model.ChunkedImageUrl;
import org.tm.archive.giph.model.GiphyImage;
import org.tm.archive.mms.GlideApp;
import org.tm.archive.util.Projection;
import org.tm.archive.util.Util;
import org.tm.archive.util.ViewUtil;
import org.tm.archive.util.adapter.mapping.MappingViewHolder;

/**
 * Holds a view which will either play back an MP4 gif or show its still.
 */
final class GiphyMp4ViewHolder extends MappingViewHolder<GiphyImage> implements GiphyMp4Playable {

  private static final Projection.Corners CORNERS = new Projection.Corners(ViewUtil.dpToPx(8));

  private final AspectRatioFrameLayout     container;
  private final ImageView                  stillImage;
  private final GiphyMp4Adapter.Callback   listener;
  private final Drawable                   placeholder;

  private float     aspectRatio;
  private MediaItem mediaItem;

  GiphyMp4ViewHolder(@NonNull View itemView,
                     @Nullable GiphyMp4Adapter.Callback listener)
  {
    super(itemView);
    this.container          = itemView.findViewById(R.id.container);
    this.listener           = listener;
    this.stillImage         = itemView.findViewById(R.id.still_image);
    this.placeholder        = new ColorDrawable(Util.getRandomElement(ChatColorsPalette.Names.getAll()).getColor(itemView.getContext()));

    container.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
  }

  @Override
  public void bind(@NonNull GiphyImage giphyImage) {
    aspectRatio = giphyImage.getGifAspectRatio();
    mediaItem   = MediaItem.fromUri(Uri.parse(giphyImage.getMp4PreviewUrl()));

    container.setAspectRatio(aspectRatio);

    loadPlaceholderImage(giphyImage);

    itemView.setOnClickListener(v -> listener.onClick(giphyImage));
  }

  @Override
  public void showProjectionArea() {
    container.setAlpha(1f);
  }

  @Override
  public void hideProjectionArea() {
    container.setAlpha(0f);
  }

  @Override
  public @NonNull MediaItem getMediaItem() {
    return mediaItem;
  }

  @Override
  public @NonNull Projection getGiphyMp4PlayableProjection(@NonNull ViewGroup recyclerView) {
    return Projection.relativeToParent(recyclerView, container, CORNERS);
  }

  @Override
  public boolean canPlayContent() {
    return true;
  }

  @Override
  public boolean shouldProjectContent() {
    return true;
  }

  private void loadPlaceholderImage(@NonNull GiphyImage giphyImage) {
    GlideApp.with(itemView)
            .load(new ChunkedImageUrl(giphyImage.getStillUrl()))
            .placeholder(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(stillImage);
  }
}
