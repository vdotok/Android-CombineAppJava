package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created By: Vdotok
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 * <p>
 * Response model map class after creating a group
 */
public class CreateGroupModel implements Serializable {
    @SerializedName("group_title")
    String groupTitle = "";

    @SerializedName("participants")
    ArrayList<Integer> participants = new ArrayList();

    @SerializedName("auto_created")
    int autoCreated;

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public ArrayList<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Integer> participants) {
        this.participants = participants;
    }

    public int getAutoCreated() {
        return autoCreated;
    }

    public void setAutoCreated(int autoCreated) {
        this.autoCreated = autoCreated;
    }
}
