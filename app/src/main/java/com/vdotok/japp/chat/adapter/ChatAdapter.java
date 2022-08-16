package com.vdotok.japp.chat.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.MessageType;
import com.vdotok.connect.models.ReadReceiptModel;
import com.vdotok.connect.models.ReceiptType;
import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseViewHolder;
import com.vdotok.japp.chat.ChatViewHolders.ChatListFileViewHolder;
import com.vdotok.japp.chat.ChatViewHolders.ChatListImageViewHolder;
import com.vdotok.japp.chat.ChatViewHolders.ChatListTextViewHolder;
import com.vdotok.japp.chat.viewmodel.ChatViewModel;
import com.vdotok.japp.databinding.ItemChatFileBinding;
import com.vdotok.japp.databinding.ItemChatImageBinding;
import com.vdotok.japp.databinding.ItemChatTextBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatAdapter  extends RecyclerView.Adapter<BaseViewHolder> {
    public final List<Message> dataModelList;
    private static final int VIEW_TYPE_TEXT_MESSAGE = 1;
    private static final int VIEW_TYPE_IMAGE_MESSAGE = 2;
    private static final int VIEW_TYPE_FILE_MESSAGE = 3;
    public boolean sendStatus = false;
    ChatViewModel chatViewModel;
    Context context;

    public ChatAdapter(Context context, List<Message> dataModelList, ChatViewModel chatViewModel) {
        this.context = context;
        this.dataModelList = dataModelList;
        this.chatViewModel = chatViewModel;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TEXT_MESSAGE) {
            ItemChatTextBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_chat_text, parent, false);
            return new ChatListTextViewHolder(binding.getRoot());

        } else if (viewType == VIEW_TYPE_IMAGE_MESSAGE) {
            ItemChatImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_chat_image, parent, false);
            return new ChatListImageViewHolder(binding.getRoot());

        } else {
            ItemChatFileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_chat_file, parent, false);
            return new ChatListFileViewHolder(binding.getRoot());

        }
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Message model = dataModelList.get(position);
        holder.bind(model,chatViewModel,context);
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }


    @Override
    public int getItemViewType(int position) {
        int type = 0;
        Message model = dataModelList.get(position);
        if (model.getType() == MessageType.text) {
            type = VIEW_TYPE_TEXT_MESSAGE;
        } else if (model.getType() == MessageType.media) {
            if (model.getSubType() == 0) {
                type = VIEW_TYPE_IMAGE_MESSAGE;
            } else  {
              type = VIEW_TYPE_FILE_MESSAGE;
            }
        }
        return type;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateMessageForReceipt(ReadReceiptModel model) {
        Optional<Message> itemResult = dataModelList.stream()
                .filter(List -> List.getId().equals(model.getMessageId()))
                .findFirst();
        if (itemResult.isPresent()) {
            Message item = itemResult.get();
            int position = dataModelList.indexOf(item);
            if (model.getReceiptType() == ReceiptType.SEEN.INSTANCE.getValue()) {
                item.setStatus(model.getReceiptType());
                item.setReadCount(item.getReadCount() + 1);
                dataModelList.set(position, item);
                chatViewModel.getAppManager().updateMessageMapData(item);
                notifyItemChanged(position);
            }
        }
    }

    public void addItem(Message item) {
        item.setDate(System.currentTimeMillis());
        dataModelList.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(ArrayList<Message> chatList) {
        if (chatList != null && chatList.size() > 0) {
            dataModelList.clear();
            dataModelList.addAll(chatList);
            notifyItemRangeChanged(0, dataModelList.size());
        }
    }


}

