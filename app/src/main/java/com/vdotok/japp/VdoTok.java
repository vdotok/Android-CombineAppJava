package com.vdotok.japp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.vdotok.connect.models.Presence;
import com.vdotok.japp.di.DaggerNetComponent;
import com.vdotok.japp.manager.AppManager;
import com.vdotok.streaming.enums.MediaType;
import com.vdotok.streaming.enums.SessionType;
import com.vdotok.streaming.models.CallParams;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

/**
 * Created By: VdoTok
 * Date & Time: On 25/05/2022 At 4:06 PM in 2022
 */

public class VdoTok extends Application implements HasAndroidInjector {
    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;
    @Inject
    AppManager appManager;
    LifecycleEventObserver lifecycleEventObserver = new LifecycleEventObserver() {
        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
           if (event == Lifecycle.Event.ON_RESUME){
               if (appManager.ActiveSession != null) {
                       if (appManager.ActiveSession.getMediaType() == MediaType.VIDEO && appManager.ActiveSession.getSessionType() == SessionType.CALL){
                           if (appManager.viewCam) {
                               appManager.getCallClient().resumeVideo(appManager.getUserData().getRefId(), appManager.ActiveSession.getSessionUUID());
                           }else{
                               appManager.getCallClient().pauseVideo(appManager.getUserData().getRefId(), appManager.ActiveSession.getSessionUUID());
                           }
                       }
                   }
               else{
                   Log.d("alpha12","bbbbb");
               }

           }else  if (event == Lifecycle.Event.ON_PAUSE){
               if (appManager.ActiveSession != null) {
                   if (appManager.ActiveSession.getMediaType() == MediaType.VIDEO && appManager.ActiveSession.getSessionType() == SessionType.CALL) {
                       appManager.getCallClient().pauseVideo(appManager.getUserData().getRefId(), appManager.ActiveSession.getSessionUUID());
                   }
               }else{
                   Log.d("alpha12","bbbbb");
               }

           } else{

           }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleEventObserver);
        DaggerNetComponent.builder().application(this).build().inject(this);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }

}
