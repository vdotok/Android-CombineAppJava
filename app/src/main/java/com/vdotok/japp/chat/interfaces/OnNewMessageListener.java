package com.vdotok.japp.chat.interfaces;

import com.vdotok.connect.models.Message;

public interface OnNewMessageListener {
    void onNewMessage(Message message);
}
