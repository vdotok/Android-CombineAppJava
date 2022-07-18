package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created By: Vdotok
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 * <p>
 * Response model class for mapping group information
 */

public class GroupModel implements Serializable {

    @SerializedName("id")
    int id = 0;

    @SerializedName("channel_name")
    String channelName = "";

    @SerializedName("admin_id")
    int adminId = 0;

    @SerializedName("group_title")
    String groupTitle = "";

    @SerializedName("participants")
    ArrayList<Participants> participants = new ArrayList();

    @SerializedName("auto_created")
    int autoCreated;

    @SerializedName("channel_key")
    String channelKey = "";

    @SerializedName("created_datetime")
    String createdDateTime = "";

    Boolean isUnreadMessage = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public ArrayList<Participants> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participants> participants) {
        this.participants = participants;
    }

    public int getAutoCreated() {
        return autoCreated;
    }

    public void setAutoCreated(int autoCreated) {
        this.autoCreated = autoCreated;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Boolean getUnreadMessage() {
        return isUnreadMessage;
    }

    public void setUnreadMessage(Boolean unreadMessage) {
        isUnreadMessage = unreadMessage;
    }
}