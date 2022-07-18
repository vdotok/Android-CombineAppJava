package com.vdotok.japp.chat.ChatViewHolders;

import static com.vdotok.japp.constants.ApplicationConstants.directoryName;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.ReceiptType;
import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseViewHolder;
import com.vdotok.japp.chat.viewmodel.ChatViewModel;
import com.vdotok.japp.databinding.ItemChatFileBinding;

import java.io.File;

public class ChatListFileViewHolder extends BaseViewHolder<ItemChatFileBinding> {
    public boolean sendStatus = false;

    public ChatListFileViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Message message, ChatViewModel chatViewModel, Context context) {
        seenReadMessage(chatViewModel,message);
        if (message.getSubType() == 2) {
            binding.customFileTypeText.fileTypeDisplay.setText(R.string.video_file);
            binding.customFileTypeText.file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFileClick(context);
                }
            });
        } else if (message.getSubType() == 3) {
            binding.customFileTypeText.fileTypeDisplay.setText(R.string.doc_file);
            binding.customFileTypeText.file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   onFileClick(context);
                }
            });
        } else if (message.getSubType() == 1) {
            binding.customFileTypeText.fileTypeDisplay.setText(R.string.audio_file);
            binding.customFileTypeText.file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFileClick(context);
                }
            });
        }
    }

    private void onFileClick(Context context) {
        Intent intent = new  Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            intent.setDataAndType(
                    Uri.parse(
                            Environment.getDataDirectory().getAbsolutePath().toString()
                                    + File.separator + directoryName + File.separator
                    ), "*/*"
            );
            context.startActivity(Intent.createChooser(intent, "Complete action using"));
        } else {

            intent.setDataAndType(
                    Uri.parse(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()
                                    + File.separator + directoryName + File.separator
                    ), "*/*"
            );
            context.startActivity(Intent.createChooser(intent, "Open folder"));
        }
    }

    private void seenReadMessage(ChatViewModel chatViewModel, Message message) {
        binding.setModel(message);
        binding.setChatViewModel(chatViewModel);
        binding.setSendStatus(sendStatus);
        if (message.getFrom().equals(chatViewModel.getAppManager().getUserData().getRefId())) {
            binding.setSender(true);
            binding.setSeenMsg(message.getStatus() == ReceiptType.SEEN.INSTANCE.getValue() && message.getReadCount() > 0);
        } else {
            if (message.getStatus() != ReceiptType.SEEN.INSTANCE.getValue()) {
                message.setStatus(ReceiptType.SEEN.INSTANCE.getValue());
                chatViewModel.sendAcknowledgeMsgToGroup(message);
            }
            binding.setSender(false);
        }
    }
}