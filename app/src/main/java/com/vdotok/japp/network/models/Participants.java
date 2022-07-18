package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Created By: vdotok
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 * <p>
 * Response model map class to get details of the participants involved in groups
 */
public class Participants implements Serializable {

    @SerializedName("color_code")
    String colorCode = null;

    @SerializedName("color_id")
    int colorId = 0;

    @SerializedName("email")
    String email = null;

    @SerializedName("full_name")
    String fullName = null;

    @SerializedName("user_id")
    int userId = 0;

    @SerializedName("ref_id")
    String refID = null;

    Boolean isSelected = false;

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}