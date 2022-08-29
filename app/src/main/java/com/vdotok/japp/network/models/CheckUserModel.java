package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created By: Vdotok
 * Date & Time: On 1/21/21 At 1:17 PM in 2021
 * <p>
 * Response model map class to check user details
 */
// Added ths class of only one variable because we are using raw post params for API calling mechanism
public class CheckUserModel implements Serializable {

    @SerializedName("email")
    String email;

    @SerializedName("project_id")
    String projectId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}