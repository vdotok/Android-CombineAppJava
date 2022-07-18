package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    @SerializedName("message")
    String message = "";

    @SerializedName("media_server_map")
    MessagingServerMap mediaServer;

    @SerializedName("messaging_server_map")
    MessagingServerMap messagingServer;

    @SerializedName("process_time")
    int processTime;

    @SerializedName("status")
    int status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessagingServerMap getMediaServer() {
        return mediaServer;
    }

    public void setMediaServer(MessagingServerMap mediaServer) {
        this.mediaServer = mediaServer;
    }

    public MessagingServerMap getMessagingServer() {
        return messagingServer;
    }

    public void setMessagingServer(MessagingServerMap messagingServer) {
        this.messagingServer = messagingServer;
    }

    public int getProcessTime() {
        return processTime;
    }

    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


