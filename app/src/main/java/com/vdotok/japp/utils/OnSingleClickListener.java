package com.vdotok.japp.utils;

import android.os.SystemClock;
import android.view.View;

/**
 * Created By: VdoTok
 * Date & Time: On 09/06/2022 At 1:13 PM in 2022
 */
public abstract class OnSingleClickListener implements View.OnClickListener {

    protected int defaultInterval;
    private long lastTimeClicked = 0;

    public OnSingleClickListener() {
        this(1000);
    }

    public OnSingleClickListener(int minInterval) {
        this.defaultInterval = minInterval;
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return;
        }
        lastTimeClicked = SystemClock.elapsedRealtime();
        performClick(v);
    }

    public abstract void performClick(View v);

}