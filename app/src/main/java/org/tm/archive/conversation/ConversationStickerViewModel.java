package org.tm.archive.conversation;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.tm.archive.database.DatabaseObserver;
import org.tm.archive.database.model.StickerRecord;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.emoji.EmojiSource;
import org.tm.archive.stickers.StickerSearchRepository;
import org.tm.archive.util.Throttler;

import java.util.Collections;
import java.util.List;

class ConversationStickerViewModel extends ViewModel {

  private final StickerSearchRepository              repository;
  private final MutableLiveData<List<StickerRecord>> stickers;
  private final MutableLiveData<Boolean>             stickersAvailable;
  private final Throttler                            availabilityThrottler;
  private final DatabaseObserver.Observer            packObserver;

  private ConversationStickerViewModel(@NonNull Application application, @NonNull StickerSearchRepository repository) {
    this.repository            = repository;
    this.stickers              = new MutableLiveData<>();
    this.stickersAvailable     = new MutableLiveData<>();
    this.availabilityThrottler = new Throttler(500);
    this.packObserver          = () -> {
      availabilityThrottler.publish(() -> repository.getStickerFeatureAvailability(stickersAvailable::postValue));
    };

    ApplicationDependencies.getDatabaseObserver().registerStickerPackObserver(packObserver);
  }

  @NonNull LiveData<List<StickerRecord>> getStickerResults() {
    return stickers;
  }

  @NonNull LiveData<Boolean> getStickersAvailability() {
    repository.getStickerFeatureAvailability(stickersAvailable::postValue);
    return stickersAvailable;
  }

  void onInputTextUpdated(@NonNull String text) {
    if (TextUtils.isEmpty(text) || text.length() > EmojiSource.getLatest().getMaxEmojiLength()) {
      stickers.setValue(Collections.emptyList());
    } else {
      repository.searchByEmoji(text, stickers::postValue);
    }
  }

  @Override
  protected void onCleared() {
    ApplicationDependencies.getDatabaseObserver().unregisterObserver(packObserver);
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
