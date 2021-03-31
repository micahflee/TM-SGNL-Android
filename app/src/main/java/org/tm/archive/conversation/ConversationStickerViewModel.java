package org.tm.archive.conversation;

import android.app.Application;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.tm.archive.components.emoji.EmojiUtil;
import org.tm.archive.database.CursorList;
import org.tm.archive.database.DatabaseContentProviders;
import org.tm.archive.database.model.StickerRecord;
import org.tm.archive.stickers.StickerSearchRepository;
import org.tm.archive.util.Throttler;

import java.util.List;

class ConversationStickerViewModel extends ViewModel {

  private final Application                          application;
  private final StickerSearchRepository              repository;
  private final MutableLiveData<List<StickerRecord>> stickers;
  private final MutableLiveData<Boolean>             stickersAvailable;
  private final Throttler                            availabilityThrottler;
  private final ContentObserver                      packObserver;

  private ConversationStickerViewModel(@NonNull Application application, @NonNull StickerSearchRepository repository) {
    this.application           = application;
    this.repository            = repository;
    this.stickers              = new MutableLiveData<>();
    this.stickersAvailable     = new MutableLiveData<>();
    this.availabilityThrottler = new Throttler(500);
    this.packObserver          = new ContentObserver(new Handler(Looper.getMainLooper())) {
      @Override
      public void onChange(boolean selfChange) {
        availabilityThrottler.publish(() -> repository.getStickerFeatureAvailability(stickersAvailable::postValue));
      }
    };

    application.getContentResolver().registerContentObserver(DatabaseContentProviders.StickerPack.CONTENT_URI, true, packObserver);
  }

  @NonNull LiveData<List<StickerRecord>> getStickerResults() {
    return stickers;
  }

  @NonNull LiveData<Boolean> getStickersAvailability() {
    repository.getStickerFeatureAvailability(stickersAvailable::postValue);
    return stickersAvailable;
  }

  void onInputTextUpdated(@NonNull String text) {
    if (TextUtils.isEmpty(text) || text.length() > EmojiUtil.MAX_EMOJI_LENGTH) {
      stickers.setValue(CursorList.emptyList());
    } else {
      repository.searchByEmoji(text, stickers::postValue);
    }
  }

  @Override
  protected void onCleared() {
    application.getContentResolver().unregisterContentObserver(packObserver);
  }

  static class Factory extends ViewModelProvider.NewInstanceFactory {
    private final Application             application;
    private final StickerSearchRepository repository;

    public Factory(@NonNull Application application, @NonNull StickerSearchRepository repository) {
      this.application = application;
      this.repository  = repository;
    }

    @Override
    public @NonNull <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      //noinspection ConstantConditions
      return modelClass.cast(new ConversationStickerViewModel(application, repository));
    }
  }
}
