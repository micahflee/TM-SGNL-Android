package org.archiver;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import org.jetbrains.annotations.Nullable;
import org.tm.archive.attachments.AttachmentId;
import org.tm.archive.attachments.DatabaseAttachment;
import org.tm.archive.contactshare.Contact;
import org.tm.archive.conversation.ConversationActivity;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.providers.BlobProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ArchiveFileUtil {


    public static String getPath(Context context, Uri uri){
        String[] projection = {MediaStore.MediaColumns.DATA};
        String path = "";
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Cursor metaCursor = cr.query(uri, projection, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    path = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }
        return path;
    }

    /*
This method can parse out the real local file path from a file URI.
*/
    public static String getUriRealPath(Context ctx, Uri uri)
    {
        String ret = "";

        if( isAboveKitKat() )
        {
            // Android sdk version number bigger than 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        }else
        {
            // Android sdk version number smaller than 19.
            ret = getImageRealPath(ctx.getContentResolver(), uri, null);
        }

        return ret;
    }

    /*
    This method will parse out the real local file path from the file content URI.
    The method is only applied to android sdk version number that is bigger than 19.
    */
    public static String getUriRealPathAboveKitkat(Context ctx, Uri uri)
    {
        String ret = "";

        if(ctx != null && uri != null) {

            if(isContentUri(uri))
            {
                if(isGooglePhotoDoc(uri.getAuthority()))
                {
                    ret = uri.getLastPathSegment();
                }else {
                    ret = getImageRealPath(ctx.getContentResolver(), uri, null);
                }
            }else if(isFileUri(uri)) {
                ret = uri.getPath();
            }else if(isDocumentUri(ctx, uri)){

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if(isMediaDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if("image".equals(docType))
                        {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if("video".equals(docType))
                        {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if("audio".equals(docType))
                        {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(ctx.getContentResolver(), mediaContentUri, whereClause);
                    }

                }else if(isDownloadDoc(uriAuthority))
                {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(ctx.getContentResolver(), downloadUriAppendId, null);

                }else if(isExternalStoreDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if("primary".equalsIgnoreCase(type))
                        {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    public static boolean isAboveKitKat()
    {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    public static boolean isDocumentUri(Context ctx, Uri uri)
    {
        boolean ret = false;
        if(ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    public static boolean isContentUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("content".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    public static boolean isFileUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("file".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. Return true means the file is saved in external storage. */
    public static boolean isExternalStoreDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.externalstorage.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. return true means this file is a downloaed file. */
    public static boolean isDownloadDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.downloads.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /*
    Check if MediaProvider provide this document, if true means this image is created in android media app.
    */
    public static boolean isMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /*
    Check whether google photos provide this document, if true means this image is created in google photos app.
    */
    public static boolean isGooglePhotoDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.google.android.apps.photos.content".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    public static String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    public static String getRealPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            System.out.println("getPath() uri: " + uri.toString());
            System.out.println("getPath() uri authority: " + uri.getAuthority());
            System.out.println("getPath() uri path: " + uri.getPath());

            // ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                System.out.println("getPath() docId: " + docId + ", split: " + split.length + ", type: " + type);

                // This is for checking Main Memory
                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1] + "/";
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                    // This is for checking SD Card
                } else {
                    return "storage" + "/" + docId.replace(":", "/");
                }

            }
        }
        return null;
    }

    public static void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if ( out != null ) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFile(Context context,String dirName,String fileName){

        File dir = new File(context.getCacheDir().toString());
        if(dir.exists()){
            for (File file : dir.listFiles()) {
                if(file.getName().equalsIgnoreCase(fileName)){
                    file.delete();
                    break;
                }
            }
        }
    }

    public static File createFileFromContentUri(Context context, String contentUri){
        File resultFile = null;
        if(contentUri.contains(ArchiveConstants.SIGNAL_PART_PATH) ) {
            resultFile = getFileFromDataBaseUri(context, contentUri);
        }else if(contentUri.contains(ArchiveConstants.SIGNAL_STICKER_PATH)){
            resultFile = getStickerFileFromBlobProvider(context, contentUri);
        }else if(contentUri.contains(ArchiveConstants.SIGNAL_BLOB_PATH)){
            resultFile = getFileFromBlobProvider(context, contentUri);
        }else {
            resultFile = getFileFromDeviceUri(context, contentUri);
        }
        return resultFile;
    }
    
    public static File getFileFromDataBaseUri(Context context, String contentUri) {

        String[] splitUri = contentUri.split("/");
        int splitLength = splitUri.length;
        DatabaseAttachment databaseAttachment = DatabaseFactory.getAttachmentDatabase(context).getAttachment(new AttachmentId(Long.parseLong(splitUri[splitLength - 1]),Long.parseLong(splitUri[splitLength - 2])));
        String fileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(databaseAttachment.getContentType());
        InputStream attachmentInputStream = null;
        try {
            attachmentInputStream = DatabaseFactory.getAttachmentDatabase(context).getAttachmentStream(databaseAttachment.getAttachmentId(),0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = contentUri.split("/")[contentUri.split("/").length - 1].split("\\.")[0] + "." + fileType;
        File resultFile = new File(context.getCacheDir(), fileName);
        ArchiveFileUtil.copyInputStreamToFile(attachmentInputStream, resultFile);

        return resultFile;
    }

    private static File getFileFromBlobProvider(Context context, String contentUri) {
        File resultFile = null;
        String fileName = "";
        String fileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(Uri.parse(contentUri)));
        InputStream stream = null;
        try {
            stream = BlobProvider.getInstance().getStream(ApplicationDependencies.getApplication(), Uri.parse(contentUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileName = contentUri.split("/")[contentUri.split("/").length - 1].split("\\.")[0] + "." + fileType;

        resultFile = new File(context.getCacheDir(), fileName);

        ArchiveFileUtil.copyInputStreamToFile(stream, resultFile);

        return resultFile;
    }

    private static File getStickerFileFromBlobProvider(Context context, String contentUri) {
        File resultFile = null;
        String fileName = "";
        InputStream stream = null;
        try {
            stream = DatabaseFactory.getStickerDatabase(context).getStickerStream(ContentUris.parseId(Uri.parse(contentUri)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileName = contentUri.split("/")[contentUri.split("/").length - 1].split("\\.")[0] + "." + "webp";

        resultFile = new File(context.getCacheDir(), fileName);

        ArchiveFileUtil.copyInputStreamToFile(stream, resultFile);

        return resultFile;
    }

    @Nullable
    private static File getFileFromDeviceUri(Context context, String contentUri) {
        if(ConversationActivity.checkWriteExternalPermission(context)) {
            File resultFile = null;
            String fileName = "";
            String fileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(Uri.parse(contentUri)));
            InputStream inputStream = null;

            fileName = contentUri.split("/")[contentUri.split("/").length - 1].split("\\.")[0] + "." + fileType;

            resultFile = new File(context.getCacheDir(), fileName);
            try {
                inputStream = context.getContentResolver().openInputStream(Uri.parse(contentUri));
                ArchiveFileUtil.copyInputStreamToFile(inputStream, resultFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return resultFile;
        }
        return null;
    }

    public static File createVCFFileFromContact(Context context, Contact contact){

        File vcfFile = new File(context.getCacheDir(), contact.getName().isEmpty()? "contact" : contact.getName().getDisplayName().replaceAll(" ","_") + ".vcf");
        FileWriter fw = null;
        try {
            fw = new FileWriter(vcfFile);
            fw.write("BEGIN:VCARD\r\n");
            fw.write("VERSION:3.0\r\n");
            fw.write("N:" + contact.getName().getFamilyName() + ";" + contact.getName().getGivenName() + "\r\n");
            fw.write("FN:" + contact.getName().getGivenName() + " " + contact.getName().getFamilyName()  + "\r\n");
            if(contact.getOrganization() != null) {
                fw.write("ORG:" + contact.getOrganization() + "\r\n");
            }
            for (int i = 0; i < contact.getPostalAddresses().size(); i++) {
                fw.write("TEL;TYPE=WORK,VOICE:" + contact.getPostalAddresses().get(0).getLabel() + "\r\n");
            }

            for (int i = 0; i < contact.getPhoneNumbers().size(); i++) {
                fw.write("TEL;TYPE=WORK,VOICE:" + contact.getPhoneNumbers().get(i).getNumber() + "\r\n");
            }

            fw.write("ADR;TYPE=WORK:;;" + contact.getPostalAddresses()+ "\r\n");
            for (int i = 0; i < contact.getEmails().size(); i++) {
                fw.write("EMAIL;TYPE=PREF,INTERNET:" + contact.getEmails().get(i).getEmail() + "\r\n");
            }

            fw.write("END:VCARD\r\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vcfFile;

    }
}
