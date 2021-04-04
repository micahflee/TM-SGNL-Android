package org.tm.archive.linkpreview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import org.tm.archive.R;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.megaphone.Megaphone;
import org.tm.archive.megaphone.MegaphoneActionController;

public class LinkPreviewsMegaphoneView extends FrameLayout {

  private View yesButton;
  private View noButton;

  public LinkPreviewsMegaphoneView(Context context) {
    super(context);
    initialize(context);
  }

  public LinkPreviewsMegaphoneView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(context);
  }

  private void initialize(@NonNull Context context) {
    inflate(context, R.layout.link_previews_megaphone, this);

    this.yesButton = findViewById(R.id.linkpreview_megaphone_ok);
    this.noButton  = findViewById(R.id.linkpreview_megaphone_disable);
  }

  public void present(@NonNull Megaphone megaphone, @NonNull MegaphoneActionController listener) {
    this.yesButton.setOnClickListener(v -> {
      SignalStore.settings().setLinkPreviewsEnabled(true);
      listener.onMegaphoneCompleted(megaphone.getEvent());
    });

    this.noButton.setOnClickListener(v -> {
      SignalStore.settings().setLinkPreviewsEnabled(false);
      listener.onMegaphoneCompleted(megaphone.getEvent());
    });
  }
}
