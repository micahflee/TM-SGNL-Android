package org.tm.archive.scribbles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import org.signal.core.util.concurrent.SignalExecutors;
import org.tm.archive.R;
import org.tm.archive.components.emoji.MediaKeyboard;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.database.model.StickerRecord;
import org.tm.archive.keyboard.KeyboardPage;
import org.tm.archive.keyboard.KeyboardPagerViewModel;
import org.tm.archive.keyboard.sticker.StickerKeyboardPageFragment;
import org.tm.archive.keyboard.sticker.StickerSearchDialogFragment;
import org.tm.archive.stickers.StickerEventListener;
import org.tm.archive.stickers.StickerManagementActivity;
import org.tm.archive.util.ViewUtil;

public final class ImageEditorStickerSelectActivity extends AppCompatActivity implements StickerEventListener, MediaKeyboard.MediaKeyboardListener, StickerKeyboardPageFragment.Callback {

  @Override
  protected void attachBaseContext(@NonNull Context newBase) {
    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    super.attachBaseContext(newBase);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.scribble_select_new_sticker_activity);

    KeyboardPagerViewModel keyboardPagerViewModel = new ViewModelProvider(this).get(KeyboardPagerViewModel.class);
    keyboardPagerViewModel.setOnlyPage(KeyboardPage.STICKER);

    MediaKeyboard mediaKeyboard = findViewById(R.id.emoji_drawer);
    mediaKeyboard.show();
  }

  @Override
  public void onShown() {
  }

  @Override
  public void onHidden() {
    finish();
  }

  @Override
  public void onKeyboardChanged(@NonNull KeyboardPage page) {
  }

  @Override
  public void onStickerSelected(@NonNull StickerRecord sticker) {
    Intent intent = new Intent();
    intent.setData(sticker.getUri());
    setResult(RESULT_OK, intent);

    SignalExecutors.BOUNDED.execute(() -> SignalDatabase.stickers().updateStickerLastUsedTime(sticker.getRowId(), System.currentTimeMillis()));
    ViewUtil.hideKeyboard(this, findViewById(android.R.id.content));
    finish();
  }

  @Override
  public void onStickerManagementClicked() {
    startActivity(StickerManagementActivity.getIntent(ImageEditorStickerSelectActivity.this));
  }


  @Override
  public void openStickerSearch() {
    StickerSearchDialogFragment.show(getSupportFragmentManager());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
