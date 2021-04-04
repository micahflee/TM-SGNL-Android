package org.tm.archive.recipients.ui.sharablegrouplink.qr;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.tm.archive.groups.GroupId;
import org.tm.archive.groups.LiveGroup;
import org.tm.archive.groups.v2.GroupLinkUrlAndStatus;

public final class GroupLinkShareQrViewModel extends ViewModel {

  private final LiveData<String> qrData;

  private GroupLinkShareQrViewModel(@NonNull GroupId.V2 groupId) {
    LiveGroup liveGroup = new LiveGroup(groupId);

    this.qrData = Transformations.map(liveGroup.getGroupLink(), GroupLinkUrlAndStatus::getUrl);
  }

  LiveData<String> getQrUrl() {
    return qrData;
  }

  public static final class Factory implements ViewModelProvider.Factory {

    private final GroupId.V2 groupId;

    public Factory(@NonNull GroupId.V2 groupId) {
      this.groupId = groupId;
    }

    @Override
    public @NonNull <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      //noinspection ConstantConditions
      return modelClass.cast(new GroupLinkShareQrViewModel(groupId));
    }
  }
}
