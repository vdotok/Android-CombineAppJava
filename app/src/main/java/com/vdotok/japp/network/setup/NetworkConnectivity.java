package com.vdotok.japp.network.setup;

import android.content.Context;

import javax.inject.Inject;

/**
 * Created By: VdoTok
 * Date & Time: On 11/8/21 At 1:30 PM in 2021
 */
public class NetworkConnectivity {

    private Context context;

    @Inject
    public NetworkConnectivity(Context pContext) {
        this.context = pContext;
    }

    public Boolean isInternetAvailable() {
        return new ConnectivityStatus(context).isConnected();
    }
}