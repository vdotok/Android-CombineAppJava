package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created By: Norgic
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 * <p>
 * Response model map class getting the response after user has successfully logged in
 */
class ProfileImageResponse implements Serializable {

    @SerializedName("status")
    String status = null;

    @SerializedName("data")
    String data = null;

    @SerializedName("filename")
    String image = null;

    @SerializedName("message")
    String message = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}