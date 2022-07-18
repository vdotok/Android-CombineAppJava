package com.vdotok.japp.models;

import com.vdotok.japp.uielements.CustomCallView;

import org.webrtc.VideoTrack;

/**
 * Created By: VdoTok
 * Date & Time: On 08/06/2022 At 12:52 PM in 2022
 */
public class ActiveSession {
    private String refID;
    private String sessionID;
    private VideoTrack videoTrack;
    private CustomCallView viewRenderer = null;
    private Boolean isMuted = false;
    private Boolean isCamPaused = false;
    private Boolean isOwnView = false;
    private Boolean isFullView = false;

    public ActiveSession(String refID, String sessionID, VideoTrack videoTrack, CustomCallView viewRenderer, Boolean isMuted, Boolean isCamPaused, Boolean isOwnView, Boolean isFullView) {
        this.refID = refID;
        this.sessionID = sessionID;
        this.videoTrack = videoTrack;
        this.viewRenderer = viewRenderer;
        this.isMuted = isMuted;
        this.isCamPaused = isCamPaused;
        this.isOwnView = isOwnView;
        this.isFullView = isFullView;
    }

    public ActiveSession() {

    }

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public VideoTrack getVideoTrack() {
        return videoTrack;
    }

    public void setVideoTrack(VideoTrack videoTrack) {
        this.videoTrack = videoTrack;
    }

    public CustomCallView getViewRenderer() {
        return viewRenderer;
    }

    public void setViewRenderer(CustomCallView viewRenderer) {
        this.viewRenderer = viewRenderer;
    }

    public boolean getMuted() {
        return isMuted;
    }

    public void setMuted(Boolean muted) {
        isMuted = muted;
    }

    public boolean getCamPaused() {
        return isCamPaused;
    }

    public void setCamPaused(Boolean camPaused) {
        isCamPaused = camPaused;
    }

    public Boolean getOwnView() {
        return isOwnView;
    }

    public void setOwnView(Boolean ownView) {
        isOwnView = ownView;
    }

    public Boolean getFullView() {
        return isFullView;
    }

    public void setFullView(Boolean fullView) {
        isFullView = fullView;
    }
}
