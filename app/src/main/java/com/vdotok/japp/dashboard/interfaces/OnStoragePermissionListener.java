package com.vdotok.japp.dashboard.interfaces;

import com.vdotok.connect.models.HeaderModel;

import java.io.IOException;

public interface OnStoragePermissionListener {
    void saveFile(String msgId, HeaderModel headerModel, byte[] byteArray) throws IOException;
    void sendTextMessage(String message);

}
