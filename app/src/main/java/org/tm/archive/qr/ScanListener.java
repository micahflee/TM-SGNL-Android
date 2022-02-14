package org.tm.archive.qr;

import androidx.annotation.NonNull;

public interface ScanListener {
  void onQrDataFound(@NonNull String data);
}
