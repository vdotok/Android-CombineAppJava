package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SignUpModel implements Serializable {

    @SerializedName("full_name")
    String fullName = "";

    @SerializedName("email")
    String email = "";

    @SerializedName("password")
    String password = "";

    @SerializedName("device_type")
    String deviceType = "android";

    @SerializedName("device_model")
    String deviceModel = null;

    @SerializedName("device_os_ver")
    String deviceOsVer = null;

    @SerializedName("app_version")
    String appVersion = "1.0.0";

    @SerializedName("project_id")
    String project_id;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceOsVer() {
        return deviceOsVer;
    }

    public void setDeviceOsVer(String deviceOsVer) {
        this.deviceOsVer = deviceOsVer;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }
}