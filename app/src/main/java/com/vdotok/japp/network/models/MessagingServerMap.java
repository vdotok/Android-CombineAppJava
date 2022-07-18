package com.vdotok.japp.network.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created By: VdoTok
 * Date & Time: On 30/05/2022 At 1:30 PM in 2022
 */
public class MessagingServerMap implements Serializable {
    @SerializedName("complete_address")
    String completeAddress;
    @SerializedName("host")
    String host;
    @SerializedName("port")
    String port;
    @SerializedName("protocol")
    String protocol;

    public String getCompleteAddress() {
        return completeAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        this.completeAddress = completeAddress;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}