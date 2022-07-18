package com.vdotok.japp.chat.ChatViewHolders;

import android.content.Context;
import android.view.View;

import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.ReceiptType;
import com.vdotok.japp.base.BaseViewHolder;
import com.vdotok.japp.chat.viewmodel.ChatViewModel;
import com.vdotok.japp.databinding.ItemChatTextBinding;

public class ChatListTextViewHolder extends BaseViewHolder<ItemChatTextBinding> {
    public boolean sendStatus = false;

    public ChatListTextViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Message message, ChatViewModel chatViewModel, Context context) {
        seenReadMessage(chatViewModel,message);
        binding.customMessageTypeText.messageDisplay.setText(message.getContent());

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