package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created By: Norgic
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 * <p>
 * Response model map class getting the response after user has successfully logged in
 */
public class LoginResponse implements Serializable {

    @SerializedName("message")
    String message = null;

    @SerializedName("process_time")
    String processTime = null;

    @SerializedName("full_name")
    String fullName = null;

    @SerializedName("auth_token")
    String authToken = null;

    @SerializedName("authorization_token")
    String authorizationToken = null;

    @SerializedName("ref_id")
    String refId = null;

    @SerializedName("status")
    int status = 0;

    @SerializedName("userid")
    String userId = null;

    @SerializedName("phone_num")
    String contact = null;

    @SerializedName("email")
    String email = null;

    @SerializedName("profile_pic")
    String profile_pic = null;

    String mcToken = null;
    int bytesInterval = 0;

    @SerializedName("media_server_map")
    MediaServerMap mediaServer = null;

    @SerializedName("messaging_server_map")
    MessagingServerMap messagingServer = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getMcToken() {
        return mcToken;
    }

    public void setMcToken(String mcToken) {
        this.mcToken = mcToken;
    }

    public int getBytesInterval() {
        return bytesInterval;
    }

    public void setBytesInterval(int bytesInterval) {
        this.bytesInterval = bytesInterval;
    }

    public MediaServerMap getMediaServer() {
        return mediaServer;
    }

    public void setMediaServer(MediaServerMap mediaServer) {
        this.mediaServer = mediaServer;
    }

    public MessagingServerMap getMessagingServer() {
        return messagingServer;
    }

    public void setMessagingServer(MessagingServerMap messagingServer) {
        this.messagingServer = messagingServer;
    }

}


