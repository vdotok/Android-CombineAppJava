package com.vdotok.japp.utils;

import static com.vdotok.japp.constants.ApplicationConstants.AUDIO_DIRECTORY;
import static com.vdotok.japp.constants.ApplicationConstants.DOCS_DIRECTORY;
import static com.vdotok.japp.constants.ApplicationConstants.IMAGES_DIRECTORY;
import static com.vdotok.japp.constants.ApplicationConstants.VIDEO_DIRECTORY;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.vdotok.connect.models.MediaType;
import com.vdotok.connect.utils.ImageUtils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import kotlin.jvm.internal.Intrinsics;

public final class FileUtils {
    public static final FileUtils INSTANCE = new FileUtils();
    public String getMimeType(Context context, @NonNull Uri uri) {
           String mimeType;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
             ContentResolver cr  = context.getContentResolver();
           mimeType =  cr.getType(uri);
        } else {
           String  fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase(Locale.getDefault()));
        }
        return mimeType;
    }

    public byte[] convertFileToByteArray(String path) throws IOException {
            FileInputStream fis = new FileInputStream(path);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
        while(true) {
            int readNum = fis.read(b);
            if (readNum == -1) {
                return bos.toByteArray();
            }

            bos.write(b, 0,readNum);
        }
    }

    public File getFileData(Context context, Uri uri, MediaType type) throws IOException {
        return getPathFromInputStreamUri(context, uri, type);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    /**
     * Function to get a file using URI
     * @return Returns a file
     * */
   public File getPathFromInputStreamUri(Context context, Uri uri, MediaType type) throws IOException {
    InputStream inputStream = null;
         File photoFile = null;
         File extFile = null;
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                extFile = new File(ImageUtils.INSTANCE.copyFileToInternalStorage(context,uri,"temp"));
                photoFile = createTemporalFileFrom(inputStream, type, getFileExtension(extFile));
            } catch ( IOException ignored) {
                ignored.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch ( IOException e) {
                    e.printStackTrace();
                }
        }
        return photoFile;
    }

    private File createTemporalFileFrom(InputStream inputStream, MediaType type,  String extension) throws IOException {
        File targetFile = null;
        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            targetFile = getImageFile(type,extension);
            OutputStream outputStream= new FileOutputStream(targetFile);

            while(true) {
                int readnum = inputStream.read(buffer);
                if (readnum == -1) {
                    outputStream.flush();
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    break;
                }

                outputStream.write(buffer, 0, readnum);
            }
        }
        return targetFile;
    }

    public static File getImageFile(@NotNull MediaType type, @NotNull String extension) {
       String childName = System.currentTimeMillis() + extension;
//        String childName = String.valueOf(System.currentTimeMillis()) + "." + extension;
        return new File(Intrinsics.stringPlus(createAppDirectory(type.getValue()), "/"), childName);
    }

    public static String createAppDirectory(int type) {
        File dir ;
        StringBuilder stringBuilder;
        File filePath;
        if (type == MediaType.IMAGE.getValue()) {
            stringBuilder = new StringBuilder();
            filePath = Environment.getExternalStorageDirectory();
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + IMAGES_DIRECTORY);
        } else if (type == MediaType.VIDEO.getValue()) {
            stringBuilder = new StringBuilder();
            filePath = Environment.getExternalStorageDirectory();
            dir = new File(stringBuilder.append(filePath.getAbsolutePath()).append(VIDEO_DIRECTORY).toString());
        } else if (type == MediaType.AUDIO.getValue()) {
            stringBuilder = new StringBuilder();
            filePath = Environment.getExternalStorageDirectory();
            dir = new File(stringBuilder.append(filePath.getAbsolutePath()).append(AUDIO_DIRECTORY).toString());
        } else if (type == MediaType.FILE.getValue()) {
            stringBuilder = new StringBuilder();
            filePath = Environment.getExternalStorageDirectory();
            dir = new File(stringBuilder.append(filePath.getAbsolutePath()).append(DOCS_DIRECTORY).toString());
        } else {
            stringBuilder = new StringBuilder();
            filePath = Environment.getExternalStorageDirectory();
            dir = new File(stringBuilder.append(filePath.getAbsolutePath()).append(IMAGES_DIRECTORY).toString());
        }
        if (dir != null) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        return dir.getAbsolutePath();
    }


    public File saveFileDataOnExternalData(String filePath, byte[] fileData) {
        try {
            File f = new File(filePath);
//        if (f.exists()) f.delete()
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(fileData);
            fos.flush();
            fos.close();
            return f;
            // File Saved
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
        // File Not Saved
    }

   public Bitmap getBitmap(File file, int reqWidth,  int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = ImageUtils.calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }
}
