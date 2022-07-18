package com.vdotok.japp.network.setup;

public enum APICallObserversEnum {

    NO_INTERNET_CONNECTION(1),
    ON_API_REQUEST_FAILURE(2),
    ON_API_CALL_START(3),
    ON_API_CALL_STOP(4),
    ON_DATA_RECEIVED(5),
    ON_NO_DATA_RECEIVED(6);

    int value;

    APICallObserversEnum(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
