package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created By: Vdotok
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 * <p>
 * Request model map class to send that a user is selected to form a group
 */

public class UserModel {

    @SerializedName("user_id")
    String id = "";

    @SerializedName("full_name")
    String fullName = null;

    @SerializedName("email")
    String email = null;

    @SerializedName("ref_id")
    String refID = null;

    @SerializedName("profile_pic")
    String profilePic = null;

    Boolean isSelected = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}


class CallerData implements Serializable {
    String calleName = null;

    public String getCalleName() {
        return calleName;
    }

    public void setCalleName(String calleName) {
        this.calleName = calleName;
    }
}
