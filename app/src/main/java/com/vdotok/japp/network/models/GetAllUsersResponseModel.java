package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created By: Vdotok
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 *
 * Response model map class for fetching all users in the list
 */
public class GetAllUsersResponseModel implements Serializable {

    @SerializedName("min_android_verion")
    String minAndroidVersion = "";

    @SerializedName("min_ios_verion")
    String minIosVersion = "";

    @SerializedName("message")
    String message;

    @SerializedName("process_time")
    String processTime;

    @SerializedName("users")
    ArrayList<UserModel> users = new ArrayList();

    @SerializedName("status")
    int status = 0;

    public String getMinAndroidVersion() {
        return minAndroidVersion;
    }

    public void setMinAndroidVersion(String minAndroidVersion) {
        this.minAndroidVersion = minAndroidVersion;
    }

    public String getMinIosVersion() {
        return minIosVersion;
    }

    public void setMinIosVersion(String minIosVersion) {
        this.minIosVersion = minIosVersion;
    }

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

    public ArrayList<UserModel> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserModel> users) {
        this.users = users;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
