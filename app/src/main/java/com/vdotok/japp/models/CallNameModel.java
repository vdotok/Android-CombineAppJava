package com.vdotok.japp.models;


/**
 * Created By: VdoTok
 * Date & Time: On 20/12/2021 At 6:54 PM in 2021
 */
public class CallNameModel {
    private String calleName = null;
    private String groupName = null;
    private String groupAutoCreatedValue = null;


    public CallNameModel(String calleName, String groupName, String autoCreated) {
        this.calleName = calleName;
        this.groupName = groupName;
        this.groupAutoCreatedValue = autoCreated;
    }

    public String getCalleName() {
        return calleName;
    }

    public void setCalleName(String calleName) {
        this.calleName = calleName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAutoCreatedValue() {
        return groupAutoCreatedValue;
    }

    public void setGroupAutoCreatedValue(String groupAutoCreatedValue) {
        this.groupAutoCreatedValue = groupAutoCreatedValue;
    }
}