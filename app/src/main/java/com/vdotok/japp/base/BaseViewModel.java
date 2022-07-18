package com.vdotok.japp.base;

import static com.vdotok.japp.utils.StringExtension.setCallTitleCustomObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModel;

import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.ReadReceiptModel;
import com.vdotok.connect.models.ReceiptType;
import com.vdotok.japp.calling.CallingActivity;
import com.vdotok.japp.manager.AppManager;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.models.UserModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.ViewModelCallBackObserver;
import com.vdotok.japp.prefs.PersistenceManager;
import com.vdotok.streaming.enums.CallType;
import com.vdotok.streaming.enums.MediaType;
import com.vdotok.streaming.enums.SessionType;
import com.vdotok.streaming.models.CallParams;

import java.util.ArrayList;

import javax.inject.Inject;

public class BaseViewModel extends ViewModel {
    ArrayList<ViewModelCallBackObserver> callBackObservers = new ArrayList<>();

    @Inject
    AppManager appManager;

    @Inject
    PersistenceManager perf;

    @Inject
    public BaseViewModel() {

    }

    public AppManager getAppManager() {
        return appManager;
    }

    public void addObserver(ViewModelCallBackObserver callBackObserver) {
        this.callBackObservers.add(callBackObserver);
    }

    public void removeObserver() {
        this.callBackObservers = null;
    }

    public void notifyObserver(APICallObserversEnum eventType, String message) {
        if (callBackObservers != null)
            for (ViewModelCallBackObserver callBackObserver : callBackObservers) {
                callBackObserver.onObserve(eventType, message);
            }
    }

    public String getOwnRefID() {
        LoginResponse userData = perf.getLoggedInUser();
        if (userData != null && userData.getRefId() != null) {
            return userData.getRefId();
        } else {
            return "Not Available";
        }
    }

    public String getOwnUsername() {
        LoginResponse userData = perf.getLoggedInUser();
        if (userData != null && userData.getFullName() != null) {
            return userData.getFullName();
        } else {
            return "anonymous";
        }
    }

    public void registerCallClient(int reconnectStatus) {
        if (appManager.getCallClient() != null && appManager.getCallClient().isConnected()) {
            LoginResponse activeUser = perf.getLoggedInUser();
            if (activeUser != null && activeUser.getAuthorizationToken() != null) {
                appManager.getCallClient().register(activeUser.getAuthorizationToken(), getOwnRefID(), reconnectStatus);
            }
        }
    }


    /**
     * function to handle sending acknowledgment message to the group that the message is received and seen
     *
     * @param myMessage MqttMessage object containing details sent for the acknowledgment in group
     */
    public void sendAcknowledgeMsgToGroup(Message myMessage) {
        if (myMessage.getStatus() != ReceiptType.SEEN.INSTANCE.getValue())
            myMessage.setStatus(ReceiptType.DELIVERED.INSTANCE.getValue());
        if (!myMessage.getFrom().equals(getOwnRefID())) {
            ReadReceiptModel receipt = new ReadReceiptModel(
                    myMessage.getStatus(),
                    myMessage.getKey(),
                    System.currentTimeMillis(),
                    myMessage.getId(),
                    getOwnRefID(),
                    myMessage.getTo()
            );

            appManager.getChatManager().publishPacketMessage(receipt, receipt.getKey(), receipt.getTo());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        removeObserver();
    }

    public void dialOne2OneCall(UserModel userModel, Context context, CallType callType, MediaType mediaType, SessionType sessionType) {
        ArrayList<String> toRefIdsList = new ArrayList<String>();
        toRefIdsList.add(userModel.getRefID());
        CallParams callParams = new CallParams();
        callParams.setRefId(getOwnRefID());
        callParams.setToRefIds(toRefIdsList);
        callParams.setInitiator(true);
        callParams.setMediaType(mediaType);
        callParams.setCallType(callType);
        callParams.setSessionType(sessionType);
        callParams.setCustomDataPacket(setCallTitleCustomObject(getOwnUsername(), null, "1"));
        String session = appManager.getCallClient().dialOne2OneCall(callParams);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session != null) {
                    CallParams tempCallParams = callParams;
                    tempCallParams.setSessionUUID(session);
                    tempCallParams.setCustomDataPacket(setCallTitleCustomObject(userModel.getFullName(), null, "1"));
                    appManager.setSession(tempCallParams);
                    CallingActivity.openCallActivity(context);
                }
            }
        }, 200);

    }
}
