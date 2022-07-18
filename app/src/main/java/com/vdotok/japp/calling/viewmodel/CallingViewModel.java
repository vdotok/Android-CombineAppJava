package com.vdotok.japp.calling.viewmodel;


import androidx.databinding.ObservableBoolean;

import com.vdotok.japp.base.BaseViewModel;
import com.vdotok.japp.models.ActiveSession;
import com.vdotok.streaming.enums.MediaType;
import com.vdotok.streaming.models.CallParams;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created By: VdoTok
 * Date & Time: On 25/05/2022 At 3:27 PM in 2022
 */
public class CallingViewModel extends BaseViewModel {
    public ObservableBoolean isCamEnable = new ObservableBoolean(false);
    public ObservableBoolean isMicEnable = new ObservableBoolean(false);
    public ObservableBoolean isSpeakerEnabled = new ObservableBoolean(false);

    @Inject
    public CallingViewModel() {
    }


    public void switchCam() {
        CallParams session = getAppManager().getSession();
        if (session != null) {
            getAppManager().getCallClient().switchCamera(session.getSessionUUID());
        }
    }

    public void muteMic() {
        CallParams session = getAppManager().getSession();
        if (session != null) {
            getAppManager().getCallClient().muteUnMuteMic(getOwnRefID(), session.getSessionUUID());
            isMicEnable.set(getAppManager().getCallClient().isAudioEnabled(session.getSessionUUID()));
            updateLocalViewOnAudioMuteAndUnmute(getAppManager().getCallClient().isAudioEnabled(session.getSessionUUID()));
        }
    }

    private void updateLocalViewOnAudioMuteAndUnmute(boolean isMute) {
        for (ActiveSession it : getAppManager().videoViews) {
            if (it.getRefID().equals(getOwnRefID())) {
                it.getViewRenderer().showHideMuteIcon(!isMute);
                it.setMuted(isMute);
            }
        }
    }

    public void speakerToggle() {
        if (getAppManager().getCallClient().isSpeakerEnabled()) {
            isSpeakerEnabled.set(false);
            getAppManager().getCallClient().setSpeakerEnable(false);
        } else {
            isSpeakerEnabled.set(true);
            getAppManager().getCallClient().setSpeakerEnable(true);
        }
    }

    public void speakerDefaultState() {
        if (getAppManager().getSession().getMediaType() == MediaType.AUDIO) {
            getAppManager().getCallClient().setSpeakerEnable(false);
            isSpeakerEnabled.set(false);
        } else {
            isSpeakerEnabled.set(true);
            getAppManager().getCallClient().setSpeakerEnable(true);
        }
    }

    public void micEnabled() {
        CallParams session = getAppManager().getSession();
        if (session != null) {
            isMicEnable.set(getAppManager().getCallClient().isAudioEnabled(session.getSessionUUID()));
        }
    }

    public void speakerEnabledState() {
        isSpeakerEnabled.set(getAppManager().getCallClient().isSpeakerEnabled());
    }

    public void camViewEnabled() {
        CallParams session = getAppManager().getSession();
        if (session != null) {
            isCamEnable.set(getAppManager().getCallClient().isVideoEnabled(session.getSessionUUID()));
        }
    }

    //
    public void pauseCam() {
        CallParams session = getAppManager().getSession();
        if (session != null) {
            getAppManager().getCallClient().pauseVideo(getOwnRefID(), session.getSessionUUID());
            isCamEnable.set(false);
            updateLocalViewOnVideoPauseResume(true);
        }
    }

    public void resumeCam() {
        CallParams session = getAppManager().getSession();
        if (session != null) {
            getAppManager().getCallClient().resumeVideo(getOwnRefID(), session.getSessionUUID());
            isCamEnable.set(true);
            updateLocalViewOnVideoPauseResume(false);
        }
    }

    private void updateLocalViewOnVideoPauseResume(boolean isShown) {
        for (ActiveSession it : getAppManager().videoViews) {
            if (it.getRefID().equals(getOwnRefID())) {
                it.getViewRenderer().showHideAvatar(isShown);
                it.setCamPaused(isShown);
            }
        }
    }

    public void startTimer() {
        if (!getAppManager().isTimerRunning.get()) {
            getAppManager().isTimerRunning.set(true);
            getAppManager().startTimer();
        }
    }

    public void acceptCall(CallParams callParams) {
        if (getAppManager().getCallClient() != null) {
            getAppManager().getCallClient().acceptIncomingCall(getOwnRefID(), callParams);
            getAppManager().setSession(callParams);

        }
    }

    public void rejectCall(CallParams callParams) {
        if (getAppManager().getCallClient() != null) {
            getAppManager().getCallClient().rejectIncomingCall(getOwnRefID(), callParams.getSessionUUID());
            getAppManager().setSession(null);
        }
    }

    public void endCall() {
        ArrayList<String> sessionID = new ArrayList<String>();
        if (getAppManager().getSession() != null)
            sessionID.add(getAppManager().getSession().getSessionUUID());

        if (getAppManager().getCallClient() != null) {
            getAppManager().getCallClient().endCallSession(sessionID);
            getAppManager().setSession(null);
        }
        getAppManager().videoViews.clear();
        getAppManager().isTimerRunning.set(false);
    }}
