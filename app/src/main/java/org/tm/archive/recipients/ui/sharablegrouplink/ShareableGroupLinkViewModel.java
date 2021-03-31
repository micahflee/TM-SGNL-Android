package org.tm.archive.recipients.ui.sharablegrouplink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.tm.archive.groups.GroupId;
import org.tm.archive.groups.LiveGroup;
import org.tm.archive.groups.ui.GroupChangeFailureReason;
import org.tm.archive.groups.ui.GroupErrors;
import org.tm.archive.groups.v2.GroupLinkUrlAndStatus;
import org.tm.archive.util.AsynchronousCallback;
import org.tm.archive.util.SingleLiveEvent;

final class ShareableGroupLinkViewModel extends ViewModel {

  private final ShareableGroupLinkRepository    repository;
  private final LiveData<GroupLinkUrlAndStatus> groupLink;
  private final SingleLiveEvent<Integer>        toasts;
  private final SingleLiveEvent<Boolean>        busy;
  private final LiveData<Boolean>               canEdit;

  private ShareableGroupLinkViewModel(@NonNull GroupId.V2 groupId, @NonNull ShareableGroupLinkRepository repository) {
    LiveGroup liveGroup = new LiveGroup(groupId);

    this.repository = repository;
    this.groupLink  = liveGroup.getGroupLink();
    this.canEdit    = liveGroup.isSelfAdmin();
    this.toasts     = new SingleLiveEvent<>();
    this.busy       = new SingleLiveEvent<>();
  }

  LiveData<GroupLinkUrlAndStatus> getGroupLink() {
    return groupLink;
  }

  LiveData<Integer> getToasts() {
    return toasts;
  }

  LiveData<Boolean> getBusy() {
    return busy;
  }

  LiveData<Boolean> getCanEdit() {
    return canEdit;
  }

  void onToggleGroupLink() {
    busy.setValue(true);
    repository.toggleGroupLinkEnabled(new AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason>() {
      @Override
      public void onComplete(@Nullable Void result) {
        busy.postValue(false);
      }

      @Override
      public void onError(@Nullable GroupChangeFailureReason error) {
        busy.postValue(false);
        toasts.postValue(GroupErrors.getUserDisplayMessage(error));
      }
    });
  }

  void onToggleApproveMembers() {
    busy.setValue(true);
    repository.toggleGroupLinkApprovalRequired(new AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason>() {
      @Override
      public void onComplete(@Nullable Void result) {
        busy.postValue(false);
      }

      @Override
      public void onError(@Nullable GroupChangeFailureReason error) {
        busy.postValue(false);
        toasts.postValue(GroupErrors.getUserDisplayMessage(error));
      }
    });
  }

  void onResetLink() {
    busy.setValue(true);
    repository.cycleGroupLinkPassword(new AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason>() {
      @Override
      public void onComplete(@Nullable Void result) {
         busy.postValue(false);
      }

      @Override
      public void onError(@Nullable GroupChangeFailureReason error) {
        busy.postValue(false);
        toasts.postValue(GroupErrors.getUserDisplayMessage(error));
      }
    });
  }

  public static final class Factory implements ViewModelProvider.Factory {

    private final GroupId.V2                   groupId;
    private final ShareableGroupLinkRepository repository;

    public Factory(@NonNull GroupId.V2 groupId, @NonNull ShareableGroupLinkRepository repository) {
      this.groupId    = groupId;
      this.repository = repository;
    }

    @Override
    public @NonNull <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      //noinspection ConstantConditions
      return modelClass.cast(new ShareableGroupLinkViewModel(groupId, repository));
    }
  }
}
