package com.vdotok.japp.utils;

import static com.vdotok.japp.constants.ApplicationConstants.directoryName;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.vdotok.connect.models.HeaderModel;
import com.vdotok.connect.models.MediaType;
import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.MessageType;
import com.vdotok.connect.models.ReceiptType;
import com.vdotok.connect.utils.ImageUtils;
import com.vdotok.japp.chat.interfaces.OnCompleteActionListener;
import com.vdotok.japp.chat.interfaces.OnNewMessageListener;
import com.vdotok.japp.manager.AppManager;
import com.vdotok.japp.network.models.GroupModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import kotlin.jvm.internal.Intrinsics;

public final class ChatFileUtils {
    public static final ChatFileUtils INSTANCE = new ChatFileUtils();
    public File file = null;
    Message message = null;


    public void checkAndroidVersionToSave(Context context,HeaderModel headerModel, byte[] byteArray, OnCompleteActionListener listener) throws IOException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri mediaUri = null;
            String pathDirectory = null;
            String mimeType = null;

               if(headerModel.getType() == MediaType.IMAGE.getValue()){
                    mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    pathDirectory = Environment.DIRECTORY_PICTURES;
                    mimeType = "image/jpeg";
                }else if(headerModel.getType() == MediaType.VIDEO.getValue()){
                    mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    pathDirectory = Environment.DIRECTORY_MOVIES;
                    mimeType = "video/mp4";
                }else if(headerModel.getType() == MediaType.AUDIO.getValue()){
                    mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    pathDirectory = Environment.DIRECTORY_MUSIC;
                    mimeType = "audio/x-wav";
                }else if(headerModel.getType() == MediaType.FILE.getValue()){
                    mediaUri = MediaStore.Files.getContentUri("external");
                    pathDirectory = Environment.DIRECTORY_DOCUMENTS;
                    mimeType = "application/pdf";
                }
            if (mimeType != null && mediaUri != null) {
                saveImage(context, byteArray, String.valueOf(System.currentTimeMillis()), mimeType, pathDirectory +"/"+ directoryName, mediaUri,
                        new OnCompleteActionListener() {
                            @Override
                            public void onComplete() {
                                listener.onComplete();
                            }
                        });
            }

        } else {
            String fileName = "file_" + System.currentTimeMillis() + "." + headerModel.getFileExtension();
            String pathDirectory = Intrinsics.stringPlus(FileUtils.INSTANCE.createAppDirectory(headerModel.getType()), "/" + fileName);
            file = FileUtils.INSTANCE.saveFileDataOnExternalData(pathDirectory, byteArray);
            listener.onComplete();

        }

    }

    public  void saveImage(Context context, byte[] byteArray, String displayName, String mimeType, String path, Uri contentUri,OnCompleteActionListener listener) throws IOException {
        ContentValues contentValu = new ContentValues();
        contentValu.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        contentValu.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        contentValu.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
        Uri imageurl = context.getContentResolver().insert(contentUri, contentValu);
        if(imageurl != null){
        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(imageurl, "w", null);
        FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
        fileOutputStream.write(byteArray);
        fileOutputStream.close();

         file = new File(ImageUtils.INSTANCE.copyFileToInternalStorage(context, imageurl, directoryName));
                    contentValu.clear();
         context.getApplicationContext().getContentResolver().update(imageurl,contentValu,null,null);
         if (listener != null) {
             listener.onComplete();
         }
            }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public final void sendAttachmentMessage(@NotNull HeaderModel headerModel, @Nullable File files, @NotNull String msgId, @NotNull GroupModel groupModel, AppManager appManager, OnNewMessageListener listener1) {
        if (groupModel.getChannelName().equals(headerModel.getTopic())) {
                if (headerModel.getType() == MediaType.IMAGE.getValue()) {
                   message = makeImageItemModel(files, headerModel, groupModel, msgId);
                    if (message != null) {
                        appManager.updateMessageMapData(message);
                        getMessageCount(message,appManager);
                          listener1.onNewMessage(message);
                    }
                } else if (headerModel.getType() == MediaType.AUDIO.getValue()) {
                   message =makeFileItemModel(files, headerModel, groupModel, msgId);
                    if (message != null) {
                        appManager.updateMessageMapData(message);
                        getMessageCount(message,appManager);
                          listener1.onNewMessage(message);
                    }
                } else if (headerModel.getType() == MediaType.VIDEO.getValue()) {
                   message = makeFileItemModel(files, headerModel, groupModel, msgId);
                    if (message != null) {
                         appManager.updateMessageMapData(message);
                         getMessageCount(message,appManager);
                         listener1.onNewMessage(message);
                    }
                } else if (headerModel.getType() == MediaType.FILE.getValue()) {
                    message = makeFileItemModel(files, headerModel, groupModel, msgId);
                    if (message != null) {
                        appManager.updateMessageMapData(message);
                        getMessageCount(message,appManager);
                         listener1.onNewMessage(message);
                    }
                }
            }

    }

    public Message makeImageItemModel(File files, HeaderModel headerModel, GroupModel groupModel, String msgId) {
        Bitmap bitmap = null;
        Message msg = null;
       if (files != null ){
        bitmap =FileUtils.INSTANCE.getBitmap(files, 500, 500);
        }
        if (bitmap != null){
      msg = new Message(msgId,
              groupModel.getChannelName(),
              groupModel.getChannelKey(),
              headerModel.getFrom(), MessageType.media,
              ImageUtils.INSTANCE.encodeToBase64(bitmap),
              0f,
              groupModel.getParticipants().size() > 1,
              ReceiptType.SENT.INSTANCE.getValue(),
              headerModel.getType(),System.currentTimeMillis(),0f,0);
                }
        return msg;
    }

    public static Message makeFileItemModel(File files, HeaderModel headerModel, GroupModel groupModel, String msgId) {
       Message msg = new Message(msgId,
               groupModel.getChannelName(),
               groupModel.getChannelKey(),
               headerModel.getFrom(),
               MessageType.media,
               files.toURI().toString(),
               0f,
               groupModel.getParticipants().size() > 1,
               ReceiptType.SENT.INSTANCE.getValue(),
               headerModel.getType(),System.currentTimeMillis(),0f,0);
        return msg;
    }

    public void  getMessageCount(Message msg,AppManager appManager) {
        if (msg.getTo() != null){
            appManager.mapUnreadCount.put(msg.getTo(), appManager.mapUnreadCount.containsKey(msg.getTo()) ? appManager.mapUnreadCount.get(msg.getTo()) + 1 : 1);
        }
    }

    /**
     * this method is used to get the image uri path
     */
   public Uri getImageUri( Context inContext, Bitmap inImage){
       ByteArrayOutputStream bytes =new  ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(),
                inImage,
                "Title",
                null
        );
        return Uri.parse(path);
    }
}
