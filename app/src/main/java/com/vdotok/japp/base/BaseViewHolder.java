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

    public BaseViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public abstract void bind(Message message, ChatViewModel chatViewModel, Context context);
}