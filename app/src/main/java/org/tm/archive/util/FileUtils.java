package org.tm.archive.util;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.Nullable;

import org.archiver.ArchiveConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class FileUtils {

  static {
    System.loadLibrary("native-utils");
  }

  public static native int getFileDescriptorOwner(FileDescriptor fileDescriptor);

  static native int createMemoryFileDescriptor(String name);

  public static byte[] getFileDigest(FileInputStream fin) throws IOException {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA256");

      byte[] buffer = new byte[4096];
      int read = 0;

      while ((read = fin.read(buffer, 0, buffer.length)) != -1) {
        digest.update(buffer, 0, read);
      }

      return digest.digest();
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  public static void deleteDirectoryContents(@Nullable File directory) {
    if (directory == null || !directory.exists() || !directory.isDirectory()) return;

    File[] files = directory.listFiles();

    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) deleteDirectory(file);
        else                    file.delete();
      }
    }
  }

  public static File createPlaceHolderTempFile(Context context, String fileName) {
    File dir = new File(context.getFilesDir(), ArchiveConstants.ARCHIVE_FILE_FOLDER_NAME);
    if (!dir.exists() && dir != null) {
      dir.mkdir();
    }
    return new File(dir, fileName);
  }

  public static Pair<InputStream, InputStream> duplicateInputStream(InputStream originalStreamToCopy){

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    byte[] buffer = new byte[1024];
    int len = 0;
    while (true) {
      try {
        if (!((len = originalStreamToCopy.read(buffer)) > -1)) break;
      } catch (IOException e) {
        e.printStackTrace();
      }
      baos.write(buffer, 0, len);
    }
    try {
      baos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Open new InputStreams using recorded bytes
    // Can be repeated as many times as you wish
    InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
    InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

    return new Pair(is1,is2);
  }


  public static File writeFileOnInternalStorage(Context context, String dirName, String sFileName, InputStream source) {

    File dir = new File(context.getFilesDir(), dirName);
    if (!dir.exists()) {
      dir.mkdir();
    }

    File gpxfile = null;

    try {
      //copy:
      gpxfile = new File(dir, sFileName);
      FileOutputStream fos = new FileOutputStream(gpxfile);
      try {
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = source.read(buf)) > 0) {
          fos.write(buf, 0, len);
        }
      } finally {
        fos.close();
      }
      source.close();
      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return gpxfile;

  }

  public static boolean deleteDirectory(@Nullable File directory) {
    if (directory == null || !directory.exists() || !directory.isDirectory()) {
      return false;
    }

    deleteDirectoryContents(directory);

    return directory.delete();
  }
}
