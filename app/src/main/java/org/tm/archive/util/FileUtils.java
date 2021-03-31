package org.tm.archive.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;
import android.webkit.MimeTypeMap;

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
import java.io.OutputStream;
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
        else file.delete();
      }
    }
  }

  public static boolean deleteDirectory(@Nullable File directory) {
    if (directory == null || !directory.exists() || !directory.isDirectory()) {
      return false;
    }

    deleteDirectoryContents(directory);

    return directory.delete();
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




  public static String getPath(final Context context, final Uri uri) {

    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }

        // TODO handle non-primary volumes
      }
      // DownloadsProvider
      else if (isDownloadsDocument(uri)) {

        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        return getDataColumn(context, contentUri, null, null);
      }
      // MediaProvider
      else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{
                split[1]
        };

        return getDataColumn(context, contentUri, selection, selectionArgs);
      }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
      return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
      return uri.getPath();
    }

    return null;
  }

  /**
   * Get the value of the data column for this Uri. This is useful for
   * MediaStore Uris, and other file-based ContentProviders.
   *
   * @param context       The context.
   * @param uri           The Uri to query.
   * @param selection     (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
  public static String getDataColumn(Context context, Uri uri, String selection,
                                     String[] selectionArgs) {

    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {
            column
    };

    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
              null);
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }


  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }


  public static File createPlaceHolderTempFile(Context context,  String fileName) {
    File dir = new File(context.getFilesDir(), ArchiveConstants.ARCHIVE_FILE_FOLDER_NAME);
    if (!dir.exists() && dir != null) {
      dir.mkdir();
    }
    return new File(dir, fileName);
  }

  public static void deleteFile(Context context,String dirName,String fileName){

    File dir = new File(context.getFilesDir(), dirName);
    if(dir.exists()){
      for (File file : dir.listFiles()) {
        if(file.getName().equalsIgnoreCase(fileName)){
          file.delete();
          break;
        }
      }
    }

  }

  public static int copy(InputStream input, OutputStream output) throws IOException{
    byte[] buffer = new byte[1024];
    int count = 0;
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  private OutputStream copyInputStreamToFile( InputStream in, File file ) {
    OutputStream out = null;
    try {
      out = new FileOutputStream(file);
      byte[] buf = new byte[1024];
      int len;
      while((len=in.read(buf))>0){
        out.write(buf,0,len);
      }
      out.close();
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return out;
  }


  // Code simulating the copy
  // You could alternatively use NIO
  // And please, unlike me, do something about the Exceptions :D
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


  public static String getExtensionFromMimeType(Context ctx , String mimeType) {

    ContentResolver cR = ctx.getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(mimeType);
  }


/*  public static String getRealPathFromURI(Context context, Uri contentUri) {
    Cursor cursor = null;
    try {
      String[] proj = { MediaStore.Images.Media.DATA };
      cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }*/


  public static String getFilePathForN(Uri uri, Context context) {
    Uri returnUri = uri;
    Cursor returnCursor = context.getContentResolver().query(/*returnUri*/ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
    /*
     * Get the column indexes of the data in the Cursor,
     *     * move to the first row in the Cursor, get the data,
     *     * and display it.
     * */
    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
    returnCursor.moveToFirst();
    String name = (returnCursor.getString(nameIndex));
    String size = (Long.toString(returnCursor.getLong(sizeIndex)));
    File file = new File(context.getFilesDir(), name);
    try {
      InputStream inputStream = context.getContentResolver().openInputStream(uri);
      FileOutputStream outputStream = new FileOutputStream(file);
      int read = 0;
      int maxBufferSize = 1 * 1024 * 1024;
      int bytesAvailable = inputStream.available();

      //int bufferSize = 1024;
      int bufferSize = Math.min(bytesAvailable, maxBufferSize);

      final byte[] buffers = new byte[bufferSize];
      while ((read = inputStream.read(buffers)) != -1) {
        outputStream.write(buffers, 0, read);
      }
      Log.e("File Size", "Size " + file.length());
      inputStream.close();
      outputStream.close();
      Log.e("File Path", "Path " + file.getPath());
      Log.e("File Size", "Size " + file.length());
    } catch (Exception e) {
      Log.e("Exception", e.getMessage());
    }
    return file.getPath();
  }



  @TargetApi(Build.VERSION_CODES.KITKAT)
  public static String getRealPathFromURI_API19(Context context, Uri uri){
    String filePath = "";
    String wholeID = DocumentsContract.getDocumentId(uri);
    String id = wholeID.split(":")[1];

    String[] column = { MediaStore.Images.Media.DATA };

    // where id is equal to
    String sel = MediaStore.Images.Media._ID + "=?";

    Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, new String[]{ id }, null);
    int columnIndex = cursor.getColumnIndex(column[0]);
    if (cursor.moveToFirst()) {
      filePath = cursor.getString(columnIndex);
    }
    cursor.close();
    return filePath;
  }


  public static String getRealPathFromURI(Context context, Uri contentUri) {
    Cursor cursor = null;
    String path = "";
    try {
      String[] proj = { MediaStore.Images.Media.DATA };
      cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
      cursor.moveToFirst();
      int column_index = cursor.getColumnIndex(proj[0]);
       path = cursor.getString(column_index);
      return path;
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }

  }





}
