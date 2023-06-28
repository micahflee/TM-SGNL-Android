package org.tm.archive.stickers;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.SignalExecutors;
import org.tm.archive.database.AttachmentTable;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.database.StickerTable;
import org.tm.archive.database.StickerTable.StickerPackRecordReader;
import org.tm.archive.database.model.StickerPackRecord;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.JobManager;
import org.tm.archive.jobs.MultiDeviceStickerPackOperationJob;
import org.tm.archive.jobs.StickerPackDownloadJob;
import org.tm.archive.util.TextSecurePreferences;

import java.util.ArrayList;
import java.util.List;

final class StickerManagementRepository {

  private final Context         context;
  private final StickerTable    stickerDatabase;
  private final AttachmentTable attachmentDatabase;

  StickerManagementRepository(@NonNull Context context) {
    this.context            = context.getApplicationContext();
    this.stickerDatabase    = SignalDatabase.stickers();
    this.attachmentDatabase = SignalDatabase.attachments();
  }

  void deleteOrphanedStickerPacks() {
    SignalExecutors.SERIAL.execute(stickerDatabase::deleteOrphanedPacks);
  }

  void fetchUnretrievedReferencePacks() {
    SignalExecutors.SERIAL.execute(() -> {
      JobManager jobManager = ApplicationDependencies.getJobManager();

      try (Cursor cursor = attachmentDatabase.getUnavailableStickerPacks()) {
        while (cursor != null && cursor.moveToNext()) {
          String packId  = cursor.getString(cursor.getColumnIndexOrThrow(AttachmentTable.STICKER_PACK_ID));
          String packKey = cursor.getString(cursor.getColumnIndexOrThrow(AttachmentTable.STICKER_PACK_KEY));

          jobManager.add(StickerPackDownloadJob.forReference(packId, packKey));
        }
      }
    });
  }

  void getStickerPacks(@NonNull Callback<PackResult> callback) {
    SignalExecutors.SERIAL.execute(() -> {
      List<StickerPackRecord> installedPacks = new ArrayList<>();
      List<StickerPackRecord> availablePacks = new ArrayList<>();
      List<StickerPackRecord> blessedPacks   = new ArrayList<>();

      try (StickerPackRecordReader reader = new StickerPackRecordReader(stickerDatabase.getAllStickerPacks())) {
        StickerPackRecord record;
        while ((record = reader.getNext()) != null) {
          if (record.isInstalled()) {
            installedPacks.add(record);
          } else if (BlessedPacks.contains(record.getPackId())) {
            blessedPacks.add(record);
          } else {
            availablePacks.add(record);
          }
        }
      }

      callback.onComplete(new PackResult(installedPacks, availablePacks, blessedPacks));
    });
  }

  void uninstallStickerPack(@NonNull String packId, @NonNull String packKey) {
    SignalExecutors.SERIAL.execute(() -> {
      stickerDatabase.uninstallPack(packId);

      if (TextSecurePreferences.isMultiDevice(context)) {
        ApplicationDependencies.getJobManager().add(new MultiDeviceStickerPackOperationJob(packId, packKey, MultiDeviceStickerPackOperationJob.Type.REMOVE));
      }
    });
  }

  void installStickerPack(@NonNull String packId, @NonNull String packKey, boolean notify) {
    SignalExecutors.SERIAL.execute(() -> {
      JobManager jobManager = ApplicationDependencies.getJobManager();

      if (stickerDatabase.isPackAvailableAsReference(packId)) {
        stickerDatabase.markPackAsInstalled(packId, notify);
      }

      jobManager.add(StickerPackDownloadJob.forInstall(packId, packKey, notify));

      if (TextSecurePreferences.isMultiDevice(context)) {
        jobManager.add(new MultiDeviceStickerPackOperationJob(packId, packKey, MultiDeviceStickerPackOperationJob.Type.INSTALL));
      }
    });
  }

  void setPackOrder(@NonNull List<StickerPackRecord> packsInOrder) {
    SignalExecutors.SERIAL.execute(() -> {
      stickerDatabase.updatePackOrder(packsInOrder);
    });
  }

  static class PackResult {

    private final List<StickerPackRecord> installedPacks;
    private final List<StickerPackRecord> availablePacks;
    private final List<StickerPackRecord> blessedPacks;

    PackResult(@NonNull List<StickerPackRecord> installedPacks,
               @NonNull List<StickerPackRecord> availablePacks,
               @NonNull List<StickerPackRecord> blessedPacks)
    {
      this.installedPacks = installedPacks;
      this.availablePacks = availablePacks;
      this.blessedPacks   = blessedPacks;
    }

    @NonNull List<StickerPackRecord> getInstalledPacks() {
      return installedPacks;
    }

    @NonNull List<StickerPackRecord> getAvailablePacks() {
      return availablePacks;
    }

    @NonNull List<StickerPackRecord> getBlessedPacks() {
      return blessedPacks;
    }
  }

  interface Callback<T> {
    void onComplete(T result);
  }
}
