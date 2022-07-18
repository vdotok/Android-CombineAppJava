package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created By: Vdotok
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 *
 * Response model map class for fetching all groups user is connected to
 */
public class AllGroupsResponse implements Serializable {

    @SerializedName("groups")
    public ArrayList<GroupModel> groups = new ArrayList<>();

    @SerializedName("message")
    String message = "";

    @SerializedName("status")
    int status = 0;

    public ArrayList<GroupModel> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<GroupModel> groups) {
        this.groups = groups;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}