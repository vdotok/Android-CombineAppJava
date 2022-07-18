package com.vdotok.japp.network.setup;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class NetworkStatusLiveData extends LiveData<Boolean> {

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    Boolean isDeviceConnected = false;

    public NetworkStatusLiveData(Application application) {

        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                if (!isDeviceConnected) {
                    isDeviceConnected = true;
                    Log.d("Network Callback", "Connection is available for network: $network");
                }
                postValue(true);
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                if (isDeviceConnected) {
                    isDeviceConnected = false;
                    Log.d("Network Callback", "Connection is lost for network: $network");
                }
                postValue(false);
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network,
                                              @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);

                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) && !isDeviceConnected) {
                        isDeviceConnected = true;
                        Log.i(
                                "Network Callback",
                                "Internet Connection is available for network: $network"
                        );
                        postValue(true);
                    } else if (!networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                            && isDeviceConnected) {
                        // handles the scenario when the internet is blocked by ISP,
                        // or when the dsl/fiber/cable line to the router is disconnected
                        isDeviceConnected = false;
                        Log.i(
                                "Network Callback",
                                "Internet Connection is lost temporarily for network: $network"
                        );
                        postValue(false);
                    }
                }

            }
        };
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActive() {
        super.onActive();

//        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//
//        if(info != null) {
//            postValue(info.isConnectedOrConnecting());
//        }else{
//            postValue(false);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    postValue(true);
            }
        } else {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    postValue(true);
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest networkRequest = new NetworkRequest.Builder().build();
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        }
    }


    @Override
    protected void onInactive() {
        super.onInactive();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}