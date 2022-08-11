package com.vdotok.japp.base;

import android.content.Context;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.vdotok.connect.models.Message;
import com.vdotok.japp.chat.viewmodel.ChatViewModel;

public abstract class BaseViewHolder<DB extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public DB binding;

//    protected boolean isUserSender;
//    protected boolean isMessageFromLastSender;
//    protected boolean isFirstMessageFromSender;
//
//    protected boolean isScreenShareReceiving;
//    protected boolean isScreenSharing;
//
//    protected int position = -1;
//    protected ChatAdapter.CallBack callBack;
//    protected int oneToOneGroup = -1;
//    protected boolean isUserAdmin = false;

    public BaseViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public abstract void bind(Message message, ChatViewModel chatViewModel, Context context);
//
//    public void setUserSender(boolean userSender) {
//        isUserSender = userSender;
//    }
//
//    public void setMessageFromLastSender(boolean messageFromLastSender) {
//        isMessageFromLastSender = messageFromLastSender;
//    }
//
//    public void setFirstMessageFromSender(boolean firstMessageFromSender) {
//        isFirstMessageFromSender = firstMessageFromSender;
//    }
//
//    public void setScreenShareReceiving(boolean screenShareReceiving) {
//        isScreenShareReceiving = screenShareReceiving;
//    }
//
//    public void setScreenSharing(boolean screenSharing) {
//        isScreenSharing = screenSharing;
//    }
//
//    public void setPosition(int position) {
//        this.position = position;
//    }
//
//    public void setCallBack(ChatAdapter.CallBack callBack) {
//        this.callBack = callBack;
//    }
//
//    public void setOneToOneGroup(int oneToOneGroup){
//        this.oneToOneGroup = oneToOneGroup;
//    }
//
//    public void setUserAdmin(boolean isUserAdmin){
//        this.isUserAdmin = isUserAdmin;
//    }
}