package org.tm.archive.video;

import android.media.MediaDataSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.tm.archive.crypto.AttachmentSecret;
import org.tm.archive.crypto.ModernDecryptingPartInputStream;
import org.tm.archive.video.videoconverter.mediadatasource.InputStreamMediaDataSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Create via {@link EncryptedMediaDataSource}.
 * <p>
 * A {@link MediaDataSource} that points to an encrypted file.
 * <p>
 * It is "modern" compared to the {@link ClassicEncryptedMediaDataSource}. And "modern" refers to
 * the presence of a random part of the key supplied in the constructor.
 */
@RequiresApi(23)
final class ModernEncryptedMediaDataSource extends InputStreamMediaDataSource {

  private final AttachmentSecret attachmentSecret;
  private final File             mediaFile;
  private final byte[]           random;
  private final long             length;

  ModernEncryptedMediaDataSource(@NonNull AttachmentSecret attachmentSecret, @NonNull File mediaFile, @Nullable byte[] random, long length) {
    this.attachmentSecret = attachmentSecret;
    this.mediaFile        = mediaFile;
    this.random           = random;
    this.length           = length;
  }

  @Override
  public void close() {}

  @Override
  public long getSize() {
    return length;
  }

  @NonNull
  public InputStream createInputStream(long position) throws IOException {
    if (random == null) {
      return ModernDecryptingPartInputStream.createFor(attachmentSecret, mediaFile, position);
    } else {
      return ModernDecryptingPartInputStream.createFor(attachmentSecret, random, mediaFile, position);
    }
  }
}
