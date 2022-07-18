package com.vdotok.japp.interfaces;

import android.media.projection.MediaProjection;
import android.util.Pair;

import com.vdotok.connect.models.HeaderModel;
import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.Presence;
import com.vdotok.connect.models.ReadReceiptModel;
import com.vdotok.streaming.commands.CallInfoResponse;
import com.vdotok.streaming.commands.RegisterResponse;
import com.vdotok.streaming.enums.EnumConnectionStatus;
import com.vdotok.streaming.models.CallParams;
import com.vdotok.streaming.models.SessionDataModel;
import com.vdotok.streaming.models.SessionStateInfo;
import com.vdotok.streaming.models.Usage;

import org.webrtc.VideoTrack;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created By: VdoTok
 * Date & Time: On 30/05/2022 At 5:27 PM in 2022
 */
public interface CallBackManager {
    public void callConnectionStatus(EnumConnectionStatus enumConnectionStatus);

    public void chatConnectionStatus(String message);

    public void callRegistrationStatus(RegisterResponse registerResponse);

    public void callStatus(CallInfoResponse callInfoResponse);

    public void onMessageArrive(Message message);

    public void onTypingMessage(Message message);

    public void onLocalCamera(VideoTrack stream);

    public void onAudioTrack(String refId, String sessionID);

    public void onVideoTrack(VideoTrack stream, String refId, String sessionID);

    public void onFileSend(String fileHeaderId, int fileType);

    public void onAttachmentProgressSend(String fileHeaderId, int progress);

    public void onFileReceiveFailed();

    public void onByteReceived(byte[] payload);

    public void onReceiptReceive(ReadReceiptModel model);

    public void onFileReceivedCompleted(HeaderModel headerModel, byte[] byteArray, String msgId) throws IOException;

    public void onPresenceReceived(ArrayList<Presence> who);

    public void countParticipant(int count, ArrayList<String> participantRefIdList);

    public void audioVideoState(SessionStateInfo sessionStateInfo);

    public void incomingCall(CallParams callParams);

    public void onTimeTicks(String timer);

    public void onSSSessionReady(MediaProjection mediaProjection);

    public void onPublicURL(String url);

    public void multiSessionReady(Pair<String, String> sessionIds);

    public void sendCurrentDataUsage(String sessionKey, Usage usage);

    public void sendEndDataUsage(String sessionKey, SessionDataModel sessionDataModel);

    public void sendCallTimeOut();
}
