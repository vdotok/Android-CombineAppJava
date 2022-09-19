package com.vdotok.japp.manager;

import android.content.Context;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import com.vdotok.connect.manager.ChatManager;
import com.vdotok.connect.manager.ChatManagerCallback;
import com.vdotok.connect.models.Connection;
import com.vdotok.connect.models.HeaderModel;
import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.Presence;
import com.vdotok.connect.models.ReadReceiptModel;
import com.vdotok.japp.constants.ApplicationConstants;
import com.vdotok.japp.interfaces.CallBackManager;
import com.vdotok.japp.models.ActiveSession;
import com.vdotok.japp.network.models.GroupModel;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.models.MediaServerMap;
import com.vdotok.japp.network.models.MessagingServerMap;
import com.vdotok.japp.prefs.PersistenceManager;
import com.vdotok.streaming.CallClient;
import com.vdotok.streaming.commands.CallInfoResponse;
import com.vdotok.streaming.commands.RegisterResponse;
import com.vdotok.streaming.enums.EnumConnectionStatus;
import com.vdotok.streaming.enums.PermissionType;
import com.vdotok.streaming.enums.SessionType;
import com.vdotok.streaming.interfaces.CallSDKListener;
import com.vdotok.streaming.models.CallParams;
import com.vdotok.streaming.models.SessionDataModel;
import com.vdotok.streaming.models.SessionStateInfo;
import com.vdotok.streaming.models.Usage;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.webrtc.VideoTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kotlin.Pair;

/**
 * Created By: VdoTok
 * Date & Time: On 25/05/2022 At 6:26 PM in 2022
 */
public class AppManager {

    public static Map<String, ArrayList<Message>> mapLastMessage = new HashMap<>();
    public static Map<String, Integer> mapUnreadCount = new HashMap<>();
    // hold the listeners of call the classes and fragments
    private final ArrayList<CallBackManager> listeners = new ArrayList<>();
    public ObservableBoolean callSDKStatus = new ObservableBoolean(false);
    public ObservableBoolean callSDKRegistrationStatus = new ObservableBoolean(false);
    public ObservableBoolean chatSDKStatus = new ObservableBoolean(false);
    public ObservableBoolean isTimerRunning = new ObservableBoolean(false);
    public Boolean isCallSDKsReconnect = false;
    public HashMap<SessionType, CallParams> activeSession = new HashMap<>();
    public GroupModel groupModel = null;
    public Map<String, ArrayList<Message>> mapGroupMessages = new HashMap<>();
    public MutableLiveData<Message> messageUpdateLiveData = new MutableLiveData<Message>();
    public MutableLiveData<Boolean> isCallActivityOpened = new MutableLiveData<Boolean>(false);
    public String lastMessageGroupKey = "";
    public boolean speakerState = false;
    public ArrayList<ActiveSession> videoViews = new ArrayList<ActiveSession>();
    public ArrayList<Presence> savedPresenceList = new ArrayList();
    public Handler timeOutHandler;
    private PersistenceManager pref;
    private Context context;
    private CallClient callClient;
    public Boolean viewCam = true;
    private ChatManager chatManager;
    public CallParams ActiveSession = null;
    public boolean isDialFrag = false;
    // hold the listeners of call the classes and fragments

    public AppManager(Context context) {
        this.context = context;
        this.pref = new PersistenceManager(context);
        callClient = CallClient.Companion.getInstance(context);
        if (callClient != null) {
            callClient.setConstants(ApplicationConstants.SDK_PROJECT_ID);
        }

        chatManager = ChatManager.Companion.getInstance(context);
        if (chatManager != null) {
            chatManager.setConstants(ApplicationConstants.SDK_PROJECT_ID);
            chatManager.setIsSenderReceiveFilePackets(true);
        }
        setSDKsListener();
    }


    public void reconnectCallSDKs(Boolean checkSessionsExist) {
        LoginResponse loginData = pref.getLoggedInUser();
        if (loginData.getMediaServer() != null) {
            if (callClient != null) {
                callClient.connect(getMediaServerAddress(loginData.getMediaServer()), loginData.getMediaServer().getEndPoint());
                if (checkSessionsExist) isCallSDKsReconnect = true;
            }
        }

    }

    private void setSDKsListener() {
        chatManager.setListener(new ChatManagerCallback() {
            @Override
            public void onConnect() {
                notifyChatConnectionStatus("Connected");
            }

            @Override
            public void onConnectionFailed(@NonNull Throwable throwable) {
                throwable.printStackTrace();
                notifyChatConnectionStatus(throwable.toString());
            }

            @Override
            public void onSubscribe(@NonNull String s) {

            }

            @Override
            public void onSubscribeFailed(@NonNull String s, @Nullable Throwable throwable) {

            }

            @Override
            public void onPresenceReceived(@NonNull ArrayList<Presence> who) {
                saveUpdatePresenceList(who);
                for (CallBackManager listener : listeners) {
                    listener.onPresenceReceived(who);
                }
            }

            @Override
            public void onReceiptReceived(@NonNull ReadReceiptModel readReceiptModel) {
                for (CallBackManager listener : listeners) {
                    listener.onReceiptReceive(readReceiptModel);
                }
            }

            @Override
            public void onMessageArrived(@NonNull Message message) {
                notifyNewMessage(message);
            }

            @Override
            public void onTypingMessage(@NonNull Message message) {
                for (CallBackManager listener : listeners) {
                    listener.onTypingMessage(message);
                }
            }

            @Override
            public void onBytesReceived(@NonNull byte[] bytes) {
                for (CallBackManager listener : listeners) {
                    listener.onByteReceived(bytes);
                }
            }

            @Override
            public void reconnectAction(boolean connectionState) {
                Log.e("Reconnect Action", "Called");
                if (connectionState || chatManager.isConnected()) {
                    notifyChatConnectionStatus("Connected");
                }
            }

            @Override
            public void connectionError() {
                Log.e("Coonection Error", "error");
                notifyChatConnectionStatus("Error");
            }

            @Override
            public void onConnectionLost(@NonNull Throwable throwable) {
                notifyChatConnectionStatus(throwable.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFileSendingStarted(@NonNull String fileHeaderId, int fileType) {
                notifyFileSendStart(fileHeaderId, fileType);
            }

            @Override
            public void onFileSendingComplete(@NonNull String s, int i) {

            }

            @Override
            public void onFileSendingProgressChanged(@NonNull String fileHeaderId, int progress) {
                for (CallBackManager listener : listeners) {
                    listener.onAttachmentProgressSend(fileHeaderId, progress);
                }
            }

            @Override
            public void onFileReceivingStarted(@NonNull String s) {

            }

            @Override
            public void onFileReceivingProgressChanged(@NonNull String s, int i) {

            }

            @Override
            public void onFileReceivingCompleted(@NonNull HeaderModel headerModel, @NonNull byte[] bytes, @NonNull String msgId) {
                for (CallBackManager listener : listeners) {
                    try {
                        listener.onFileReceivedCompleted(headerModel, bytes, msgId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFileSendingFailed(@NonNull String s) {

            }

            @Override
            public void onFileReceivingFailed() {
                notifyFileReceivingFailed();
            }
        });
        callClient.setListener(new CallSDKListener() {
            @Override
            public void connectionStatus(@NonNull EnumConnectionStatus enumConnectionStatus) {
                notifyCallConnectionStatus(enumConnectionStatus);
            }

            @Override
            public void onClose(@NonNull String s) {
                notifyCallConnectionStatus(EnumConnectionStatus.CLOSED);
            }

            @Override
            public void audioVideoState(@NonNull SessionStateInfo sessionStateInfo) {
                for (CallBackManager listener : listeners) {
                    listener.audioVideoState(sessionStateInfo);
                }
            }

            @Override
            public void incomingCall(@NonNull CallParams callParams) {
                if (getSession() != null || isDialFrag) {
                    getCallClient().sessionBusy(callParams.getRefId(), callParams.getSessionUUID());
                } else {
                    notifyIncomingCall(callParams);
                    startCancelTimer(callParams);
                }
            }

            @Override
            public void onSessionReady(@Nullable MediaProjection mediaProjection) {

            }

            @Override
            public void callStatus(@NonNull CallInfoResponse callInfoResponse) {
                notifyCallStatus(callInfoResponse);
                CallParams callParams = callInfoResponse.getCallParams();
                switch (callInfoResponse.getCallStatus()) {
                    case OUTGOING_CALL_ENDED: {
                        setSession(null);
                        videoViews.clear();
                        speakerState = false;
                        isTimerRunning.set(false);
                        isDialFrag = false;
                    }
                    break;
                    case CALL_REJECTED:
                        setSession(null);
                        break;
                    case INSUFFICIENT_BALANCE:
                        isTimerRunning.set(false);
                        break;
                }
            }

            @Override
            public void registrationStatus(@NonNull RegisterResponse registerResponse) {

            }

            @Override
            public void onError(@NonNull String s) {
                notifyCallConnectionStatus(EnumConnectionStatus.ERROR);
            }

            @Override
            public void onPublicURL(@NonNull String s) {

            }

            @Override
            public void sessionHold(@NonNull String s) {

            }

            @Override
            public void multiSessionCreated(@NonNull Pair<String, String> pair) {

            }

            @Override
            public void permissionError(@NonNull ArrayList<PermissionType> arrayList) {

            }

            @Override
            public void onRemoteStream(@NonNull VideoTrack videoTrack, @NonNull String refId, @NonNull String sessionID) {
                notifyRemoteViewStream(videoTrack, refId, sessionID);
            }

            @Override
            public void onRemoteStream(@NonNull String s, @NonNull String s1) {

            }

            @Override
            public void onCameraStream(@NonNull VideoTrack videoTrack) {
                notifyCameraStream(videoTrack);
            }

            @Override
            public void sendCurrentDataUsage(@NonNull String s, @NonNull Usage usage) {

            }

            @Override
            public void sendEndDataUsage(@NonNull String s, @NonNull SessionDataModel sessionDataModel) {

            }

            @Override
            public void memoryUsageDetails(long l) {

            }
        });
    }

    public String getTextValue() {
        return "value return from app manager";
    }

    public void connectChatSDK() {
        LoginResponse activeUser = pref.getLoggedInUser();
        if (chatManager != null && activeUser.getMessagingServer() != null) {
            MessagingServerMap messagingServer = activeUser.getMessagingServer();
            Connection connection = new Connection(activeUser.getRefId(),
                    activeUser.getAuthorizationToken(),
                    messagingServer.getHost(),
                    messagingServer.getPort(),
                    true,
                    MqttAsyncClient.generateClientId(),
                    5,
                    true);
            chatManager.setIsSenderReceiveFilePackets(true);
            chatManager.connect(connection);
//            pref.saveConnectionObject(connection);
        }
    }

    public void connectCallSDK() {
        LoginResponse activeUser = pref.getLoggedInUser();
        if (callClient != null && activeUser.getMediaServer() != null) {
            callClient.connect(getMediaServerAddress(activeUser.getMediaServer()), activeUser.getMediaServer().getEndPoint());
        }
    }

    public PersistenceManager getPref() {
        return pref;
    }

    public void setPref(PersistenceManager pref) {
        this.pref = pref;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CallClient getCallClient() {
        return callClient;
    }

    public void setCallClient(CallClient callClient) {
        this.callClient = callClient;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    private String getMediaServerAddress(MediaServerMap mediaServer) {
        return "https://" + mediaServer.getHost() + ":" + mediaServer.getPort();
    }


    public LoginResponse getUserData() {
        return getPref().getLoggedInUser();
    }

    /**
     * Function to help in persisting local chat by updating local data till the user is connected to the socket
     *
     * @param message message object we will be sending to the server
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateMessageMapData(Message message) {
        if (mapGroupMessages.containsKey(message.getTo())) {
            ArrayList<Message> messageValue = (ArrayList<Message>) mapGroupMessages.get(message.getTo());
            boolean check = messageValue.stream().anyMatch(it -> it.getId() == message.getId());
            if (!check) {
                messageValue.add(message);
                mapGroupMessages.put(message.getTo(), messageValue);
                mapLastMessage.put(message.getTo(), messageValue);
            } else {
                ArrayList<Message> msg = (ArrayList<Message>) mapGroupMessages.get(message.getTo());
                Message oldValue = msg.stream().filter(it -> it.getId() == message.getId()).findFirst().get();
                msg.set(msg.indexOf(oldValue), message);
                mapGroupMessages.put(message.getTo(), msg);
            }

        } else {
            ArrayList<Message> messageValue = new ArrayList();
            messageValue.add(message);
            mapGroupMessages.put(message.getTo(), messageValue);
            mapLastMessage.put(message.getTo(), messageValue);
        }
    }

    public void addListener(CallBackManager callManagerListener) {
        if (!listeners.contains(callManagerListener)) listeners.add(callManagerListener);
    }

    public void removeListener(CallBackManager callManagerListener) {
        listeners.remove(callManagerListener);
    }

    private void notifyChatConnectionStatus(String message) {
        for (CallBackManager listener : listeners) {
            listener.chatConnectionStatus(message);
        }
    }

    private void notifyRemoteViewStream(VideoTrack stream, String refId, String sessionID) {
        for (CallBackManager listener : listeners) {
            listener.onVideoTrack(stream, refId, sessionID);
        }
    }


    private void notifyCameraStream(VideoTrack stream) {
        for (CallBackManager listener : listeners) {
            listener.onLocalCamera(stream);
        }
    }


    private void notifyCallConnectionStatus(EnumConnectionStatus enumConnectionStatus) {
        for (CallBackManager listener : listeners) {
            listener.callConnectionStatus(enumConnectionStatus);
        }
    }


    private void notifyNewMessage(Message message) {
        for (CallBackManager listener : listeners) {
            listener.onMessageArrive(message);
        }
    }

    private void notifyCallStatus(CallInfoResponse callInfoResponse) {
        for (CallBackManager listener : listeners) {
            listener.callStatus(callInfoResponse);
        }
    }


    private void notifyTypingMessage(Message message) {
        for (CallBackManager listener : listeners) {
            listener.onTypingMessage(message);
        }
    }

    private void notifyFileReceivingFailed() {
        for (CallBackManager listener : listeners) {
            listener.onFileReceiveFailed();
        }
    }


    private void notifyFileSendStart(String fileHeaderId, int fileType) {
        for (CallBackManager listener : listeners) {
            listener.onFileSend(fileHeaderId, fileType);
        }
    }

    private void notifyIncomingCall(CallParams callParams) {
        for (CallBackManager listener : listeners) {
            listener.incomingCall(callParams);
        }
    }


    private void timerClick(String time) {
        for (CallBackManager listener : listeners) {
            listener.onTimeTicks(time);
        }
    }

    public void startTimer() {
        final int[] seconds = {0};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String time = null;
                int hours = seconds[0] / 3600;
                int minutes = seconds[0] % 3600 / 60;
                int sec = seconds[0] % 60;
                if (minutes <= 10) {
                    time = String.format(Locale.getDefault(), "%d:%02d", minutes, sec);
                } else if (hours == 0) {
                    time = String.format(Locale.getDefault(), "%02d:%02d", minutes, sec);
                } else {
                    time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, sec);
                }
                seconds[0]++;
                timerClick(time);
                if (isTimerRunning.get()) {
                    new Handler().postDelayed(this, 1000);
                }
            }
        };
        new Handler().postDelayed(runnable, 1000);
    }

    public CallParams getSession() {
        return ActiveSession;
    }

    //call session related data
    public void setSession(CallParams sessionCallParams) {
        this.ActiveSession = sessionCallParams;
    }

    private void startCancelTimer(CallParams callParams) {
        timeOutHandler = new Handler(Looper.getMainLooper());
        timeOutHandler.postDelayed(() -> {
            sendTimeout(callParams);
            for (CallBackManager listener : listeners) {
                listener.sendCallTimeOut();
            }
        }, (30 * 1000));
    }

    public void stopCancelTimer() {
        Log.e("timer", "stopCancelTimer: called");
        if (timeOutHandler != null)
            timeOutHandler.removeCallbacksAndMessages(null);
    }

    public void sendTimeout(CallParams callParams) {
        getCallClient().callTimeout(callParams.getRefId(), callParams.getSessionUUID());
    }


    private void saveUpdatePresenceList(ArrayList<Presence> list) {
        for (Presence it : list) {
            addUniqueElements(it);
        }

    }

    private void addUniqueElements(Presence mPresence) {
        boolean isUpdated = false;
        int i = 0;
        for (Presence presence : savedPresenceList) {
            if (presence.getUsername().equals(mPresence.getUsername())) {
                savedPresenceList.set(i, mPresence);
                isUpdated = true;
            }
            i++;
        }

        if (!isUpdated) {
            savedPresenceList.add(mPresence);
        }

    }
    public ArrayList<Presence> getPresenceList() {
        return savedPresenceList;
    }

}
