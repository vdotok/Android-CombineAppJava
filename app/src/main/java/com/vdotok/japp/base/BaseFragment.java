package com.vdotok.japp.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.norgic.spotsdialog.SpotsDialog;
import com.vdotok.connect.models.HeaderModel;
import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.Presence;
import com.vdotok.connect.models.ReadReceiptModel;
import com.vdotok.japp.R;
import com.vdotok.japp.interfaces.CallBackManager;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.ViewModelCallBackObserver;
import com.vdotok.japp.utils.DialogUtil;
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

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


public abstract class BaseFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends Fragment implements ViewModelCallBackObserver, LifecycleOwner, CallBackManager {

    public VM viewModel;
    public DB binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private AlertDialog spotsDialog;


    private String TAG = BaseFragment.class.getCanonicalName();

    @Override
    public abstract void onObserve(APICallObserversEnum event, Object eventMessage);

    public boolean isToUseActivityContext() {
        return false;
    }

    public abstract Class<VM> getViewModel();

    @LayoutRes
    public abstract int getLayoutRes();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
        if (!isToUseActivityContext())
            viewModel = new ViewModelProvider(this, viewModelFactory).get(getViewModel());
        else
            viewModel = new ViewModelProvider(getActivity(), viewModelFactory).get(getViewModel());
        viewModel.addObserver(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                getLayoutRes(), container, false);
        return binding.getRoot();
    }

    public void onSearchFilterTextChange(String newText) {
    }

    protected void doStartActivity(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showProgress(String message) {
        spotsDialog = new SpotsDialog.Builder()
                .setContext(requireContext())
                .setMessage(message)
                .setCancelable(false)
                .setTheme(R.style.LoadingStyleTransparent)
                .build();
        spotsDialog.show();
    }

    public void hideProgress() {
        if (spotsDialog != null)
            spotsDialog.dismiss();
    }

    public final void showAudioPermissionsRequiredDialog() {
        DialogUtil.INSTANCE.showPermissionsDeniedAlert(requireActivity(), getResources().getString(R.string.audio_permission_denied), getResources().getString(R.string.grant_permissions), (DialogInterface.OnClickListener) (new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialog, int which) {
                requireActivity().startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:com.vdotok.japp")));
            }
        }));
    }

    public final void showVideoCallPermissionsRequiredDialog() {
        DialogUtil.INSTANCE.showPermissionsDeniedAlert(requireActivity(), getResources().getString(R.string.video_permission_denied), getResources().getString(R.string.grant_permissions), (DialogInterface.OnClickListener) (new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:com.vdotok.japp")));
            }
        }));
    }

    public final void showReadWritePermissionsRequiredDialog() {
        DialogUtil.INSTANCE.showPermissionsDeniedAlert(requireActivity(), getResources().getString(R.string.read_write_permission_denied), getResources().getString(R.string.grant_permissions), (DialogInterface.OnClickListener) (new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:com.vdotok.japp")));
            }
        }));
    }

    @Override
    public void callConnectionStatus(EnumConnectionStatus enumConnectionStatus) {

    }

    @Override
    public void chatConnectionStatus(String message) {

    }

    @Override
    public void callRegistrationStatus(RegisterResponse registerResponse) {

    }

    @Override
    public void callStatus(CallInfoResponse callInfoResponse) {
        Log.e("CallStatus", callInfoResponse.getCallStatus().toString());
    }

    @Override
    public void onMessageArrive(Message message) {

    }

    @Override
    public void onTypingMessage(Message message) {

    }

    @Override
    public void onLocalCamera(VideoTrack stream) {
        Log.d(TAG, "onLocalCam");
    }

    @Override
    public void onAudioTrack(String refId, String sessionID) {

    }

    @Override
    public void onVideoTrack(VideoTrack stream, String refId, String sessionID) {

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
    public void onFileReceivedCompleted(HeaderModel headerModel, byte[] byteArray, String msgId) throws IOException {

    }

    @Override
    public void onPresenceReceived(ArrayList<Presence> who) {

    }

    @Override
    public void countParticipant(int count, ArrayList<String> participantRefIdList) {

    }

    @Override
    public void audioVideoState(SessionStateInfo sessionStateInfo) {

    }

    @Override
    public void incomingCall(CallParams callParams) {

    }

    @Override
    public void onTimeTicks(String timer) {

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
    public void onResume() {
        super.onResume();
        viewModel.appManager.addListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.appManager.removeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.appManager.removeListener(this);
    }

    @Override
    public void sendCallTimeOut() {

    }
}
