package com.vdotok.japp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginUserModel implements Serializable {

    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("project_id")
    String project_id;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }
}