package com.vdotok.japp.base;

import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.vdotok.connect.models.HeaderModel;
import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.Presence;
import com.vdotok.connect.models.ReadReceiptModel;
import com.vdotok.japp.R;
import com.vdotok.japp.calling.CallingActivity;
import com.vdotok.japp.databinding.ActivityBaseBinding;
import com.vdotok.japp.interfaces.CallBackManager;
import com.vdotok.japp.manager.AppManager;
import com.vdotok.japp.models.ActiveSession;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.NetworkConnectivity;
import com.vdotok.japp.network.setup.ViewModelCallBackObserver;
import com.vdotok.japp.prefs.PersistenceManager;
import com.vdotok.japp.utils.OnSingleClickListener;
import com.vdotok.streaming.commands.CallInfoResponse;
import com.vdotok.streaming.commands.RegisterResponse;
import com.vdotok.streaming.enums.EnumConnectionStatus;
import com.vdotok.streaming.models.CallParams;
import com.vdotok.streaming.models.SessionDataModel;
import com.vdotok.streaming.models.SessionStateInfo;
import com.vdotok.streaming.models.Usage;

import org.webrtc.VideoTrack;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;


public abstract class BaseActivity<VM extends BaseViewModel, DB extends ViewDataBinding> extends AppCompatActivity implements HasAndroidInjector, ViewModelCallBackObserver, CallBackManager {

    public DB binding;
    public VM viewModel;
    public Bundle bundle;

    private ActivityBaseBinding rootBinding = null;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentAndroidInjector;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    DispatchingAndroidInjector<Object> androidInjector;
    @Inject
    AppManager appManager;
    @Inject
    PersistenceManager perf;
    @Inject
    NetworkConnectivity networkConnectivity;


    private String TAG = "BaseActivity";

    @Override
    public abstract void onObserve(APICallObserversEnum event, Object eventMessage);

    public abstract Class<VM> getViewModel();

    @LayoutRes
    public abstract int getLayoutRes();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        rootBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_base, null, false);
        binding =  DataBindingUtil.inflate(getLayoutInflater(), getLayoutRes(),rootBinding.layoutContainer, true);
        super.setContentView(rootBinding.getRoot());
        rootBinding.timerLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                CallingActivity.openCallActivityConnectedCall(BaseActivity.this);
            }
        });
        viewModel = new ViewModelProvider(this, viewModelFactory).get(getViewModel());
        viewModel.addObserver(this);
        rootBinding.setIsActiveSession(viewModel.appManager.isTimerRunning);
        viewModel.appManager.isCallActivityOpened.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                rootBinding.setIsCallActivityOpen(new ObservableBoolean(aBoolean));
            }
        });

        bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.appManager.removeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rootBinding.callTime.setText("");
        viewModel.appManager.removeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.appManager.addListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onExit(View view) {
        finish();
    }

    @Override
    public void callConnectionStatus(EnumConnectionStatus enumConnectionStatus) {
        switch (enumConnectionStatus) {
            case CONNECTED: {
                viewModel.appManager.callSDKStatus.set(true);
                runOnUiThread(() -> {
                    viewModel.registerCallClient(viewModel.getAppManager().isCallSDKsReconnect ? 1 : 0);
                });
            }
            break;
            case CLOSED: {
                viewModel.appManager.callSDKStatus.set(false);
            }
            break;
            case ERROR: {
                viewModel.appManager.callSDKStatus.set(false);
                if (networkConnectivity.isInternetAvailable()) {
                    viewModel.appManager.reconnectCallSDKs(viewModel.appManager.activeSession.size() > 0);
                }
            }
            break;
            case OPEN: {
                viewModel.appManager.callSDKStatus.set(true);
            }
            break;
            default: {
                Log.i(TAG, "Call Connection Status");
            }
        }

        if (!viewModel.appManager.callSDKStatus.get())
            viewModel.appManager.callSDKRegistrationStatus.set(false);

    }


    @Override
    public void chatConnectionStatus(String message) {
        if (message == "Connected") {
            viewModel.appManager.chatSDKStatus.set(true);
        } else
            viewModel.appManager.chatSDKStatus.set(false);

    }

    @Override
    public void callRegistrationStatus(RegisterResponse registerResponse) {

    }

    @Override
    public void callStatus(CallInfoResponse callInfoResponse) {

    }

    @Override
    public void onMessageArrive(Message message) {

    }

    @Override
    public void onTypingMessage(Message message) {

    }

    @Override
    public void onLocalCamera(VideoTrack stream) {
        ActiveSession ownView = new ActiveSession();
        ownView.setRefID(viewModel.getOwnRefID());
        ownView.setSessionID("ownRefID");
        ownView.setVideoTrack(stream);
        ownView.setViewRenderer(null);
        viewModel.appManager.videoViews.add(ownView);

    }

    @Override
    public void onAudioTrack(String refId, String sessionID) {

    }

    @Override
    public void onVideoTrack(VideoTrack stream, String refId, String sessionID) {
        ActiveSession track = new ActiveSession();
        track.setRefID(refId);
        track.setSessionID(sessionID);
        track.setViewRenderer(null);
        track.setVideoTrack(stream);
        viewModel.appManager.videoViews.add(track);
    }

    @Override
    public void onFileSend(String fileHeaderId, int fileType) {

    }

    @Override
    public void onAttachmentProgressSend(String fileHeaderId, int progress) {

    }

    @Override
    public void onFileReceiveFailed() {

    }

    @Override
    public void onByteReceived(byte[] payload) {

    }

    @Override
    public void onReceiptReceive(ReadReceiptModel model) {

    }

    @Override
    public void onFileReceivedCompleted(HeaderModel headerModel, byte[] byteArray, String msgId) {

    }

    @Override
    public void onPresenceReceived(ArrayList<Presence> who) {

    }

    @Override
    public void countParticipant(int count, ArrayList<String> participantRefIdList) {

    }

    @Override
    public void audioVideoState(SessionStateInfo sessionStateInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (ActiveSession it : viewModel.appManager.videoViews){
                    if ((it.getRefID().equals(sessionStateInfo.getRefID())) && (it.getSessionID().equals(sessionStateInfo.getSessionKey()))) {
                        it.setMuted(sessionStateInfo.getAudioState() == 0);
                        it.setCamPaused(sessionStateInfo.getVideoState() == 0);
                        it.getViewRenderer().showHideAvatar(it.getCamPaused());
                        it.getViewRenderer().showHideMuteIcon(it.getMuted());

                    }
                }
            }
        });


    }

    @Override
    public void incomingCall(CallParams callParams) {
        CallingActivity.openCallActivityIncomingCall(this, callParams);
    }

    @Override
    public void onTimeTicks(String timer) {
        rootBinding.callTime.setText(timer);
    }

    @Override
    public void onSSSessionReady(MediaProjection mediaProjection) {

    }

    @Override
    public void onPublicURL(String url) {

    }

    @Override
    public void multiSessionReady(Pair<String, String> sessionIds) {

    }

    @Override
    public void sendCurrentDataUsage(String sessionKey, Usage usage) {

    }

    @Override
    public void sendEndDataUsage(String sessionKey, SessionDataModel sessionDataModel) {

    }

    @Override
    public void sendCallTimeOut() {

    }
}
