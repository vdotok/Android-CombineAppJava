package com.vdotok.japp.network.setup;

public interface ViewModelCallBackObserver<T> {

    void onObserve(APICallObserversEnum event, T eventMessage);

}
