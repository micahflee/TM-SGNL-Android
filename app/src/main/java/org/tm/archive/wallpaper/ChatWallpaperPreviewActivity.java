package org.tm.archive.wallpaper;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.annimon.stream.Stream;

import org.tm.archive.PassphraseRequiredActivity;
import org.tm.archive.R;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.ActivityTransitionUtil;
import org.tm.archive.util.DynamicNoActionBarTheme;
import org.tm.archive.util.DynamicTheme;
import org.tm.archive.util.FullscreenHelper;
import org.tm.archive.util.MappingModel;
import org.tm.archive.util.WindowUtil;

import java.util.Collections;

public class ChatWallpaperPreviewActivity extends PassphraseRequiredActivity {

  public  static final String EXTRA_CHAT_WALLPAPER   = "extra.chat.wallpaper";
  private static final String EXTRA_DIM_IN_DARK_MODE = "extra.dim.in.dark.mode";
  private static final String EXTRA_RECIPIENT_ID     = "extra.recipient.id";

  private final DynamicTheme dynamicTheme = new DynamicNoActionBarTheme();

  public static @NonNull Intent create(@NonNull Context context, @NonNull ChatWallpaper selection, @NonNull RecipientId recipientId, boolean dimInDarkMode) {
    Intent intent = new Intent(context, ChatWallpaperPreviewActivity.class);

    intent.putExtra(EXTRA_CHAT_WALLPAPER, selection);
    intent.putExtra(EXTRA_DIM_IN_DARK_MODE, dimInDarkMode);
    intent.putExtra(EXTRA_RECIPIENT_ID, recipientId);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    dynamicTheme.onCreate(this);

    setContentView(R.layout.chat_wallpaper_preview_activity);

    ViewPager2                       viewPager  = findViewById(R.id.preview_pager);
    ChatWallpaperPreviewAdapter      adapter    = new ChatWallpaperPreviewAdapter();
    View                             submit     = findViewById(R.id.preview_set_wallpaper);
    ChatWallpaperRepository          repository = new ChatWallpaperRepository();
    ChatWallpaper                    selected   = getIntent().getParcelableExtra(EXTRA_CHAT_WALLPAPER);
    boolean                          dim        = getIntent().getBooleanExtra(EXTRA_DIM_IN_DARK_MODE, false);
    Toolbar                          toolbar    = findViewById(R.id.toolbar);
    View                             bubble1    = findViewById(R.id.preview_bubble_1);
    TextView                         bubble2    = findViewById(R.id.preview_bubble_2_text);

    toolbar.setNavigationOnClickListener(unused -> {
      finish();
      ActivityTransitionUtil.setSlideOutTransition(this);
    });

    viewPager.setAdapter(adapter);

    adapter.submitList(Collections.singletonList(new ChatWallpaperSelectionMappingModel(selected)));
    repository.getAllWallpaper(wallpapers -> adapter.submitList(Stream.of(wallpapers)
                                                                      .map(wallpaper -> ChatWallpaperFactory.updateWithDimming(wallpaper, dim ? ChatWallpaper.FIXED_DIM_LEVEL_FOR_DARK_THEME : 0f))
                                                                      .<MappingModel<?>>map(ChatWallpaperSelectionMappingModel::new)
                                                                      .toList()));

    submit.setOnClickListener(unused -> {
      ChatWallpaperSelectionMappingModel model = (ChatWallpaperSelectionMappingModel) adapter.getCurrentList().get(viewPager.getCurrentItem());

      setResult(RESULT_OK, new Intent().putExtra(EXTRA_CHAT_WALLPAPER, model.getWallpaper()));
      finish();
    });

    RecipientId recipientId = getIntent().getParcelableExtra(EXTRA_RECIPIENT_ID);
    if (recipientId != null) {
      Recipient recipient = Recipient.live(recipientId).get();
      bubble1.getBackground().setColorFilter(recipient.getColor().toConversationColor(this), PorterDuff.Mode.SRC_IN);
      bubble2.setText(getString(R.string.ChatWallpaperPreviewActivity__set_wallpaper_for_s, recipient.getDisplayName(this)));
    }

    new FullscreenHelper(this).showSystemUI();
    WindowUtil.setLightStatusBarFromTheme(this);
    WindowUtil.setLightNavigationBarFromTheme(this);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    ActivityTransitionUtil.setSlideOutTransition(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    dynamicTheme.onResume(this);
  }
}
