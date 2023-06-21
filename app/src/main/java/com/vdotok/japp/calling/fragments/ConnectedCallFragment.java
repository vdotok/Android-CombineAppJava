package com.vdotok.japp.calling.fragments;

import static com.vdotok.japp.utils.StringExtension.getCallTitle;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.WindowManager;


import androidx.annotation.Nullable;

import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseFragment;
import com.vdotok.japp.calling.viewmodel.CallingViewModel;
import com.vdotok.japp.databinding.FragmentConnectedCallBinding;
import com.vdotok.japp.models.ActiveSession;
import com.vdotok.japp.models.CallNameModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.uielements.CustomCallView;
import com.vdotok.japp.utils.OnSingleClickListener;
import com.vdotok.japp.utils.viewExtension;
import com.vdotok.streaming.commands.CallInfoResponse;
import com.vdotok.streaming.enums.MediaType;
import com.vdotok.streaming.enums.SessionType;
import com.vdotok.streaming.models.CallParams;

import org.webrtc.VideoTrack;

import java.util.Map;


public class ConnectedCallFragment extends BaseFragment<CallingViewModel, FragmentConnectedCallBinding> {
    private boolean isCamCasting = false;
    private boolean isPaused = false;

    private CustomCallView localView;
    private CustomCallView remoteView;


    @Override
    public Class<CallingViewModel> getViewModel() {
        return CallingViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_connected_call;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        viewModel.getAppManager().isDialFrag = false;
        defaultButtonStates();
        setBinding();
        setCallData();
        setListeners();
        if (isCamCasting) {
            requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        viewModel.startTimer();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("newtest", "onResume");
        setViews();

    }

    private void setViews() {
        Log.e("newtest", "setView");

        int arraySize = viewModel.getAppManager().videoViews.size();
        for (int i = 0; i < arraySize; i++) {
            ActiveSession view = viewModel.getAppManager().videoViews.get(i);
            if (view.getViewRenderer() == null) {
                switch (i) {
                    case 0:
                        view.getVideoTrack().addSink(localView.getPreview());
                        view.setViewRenderer(localView);
                        break;
                    case 1:
                        view.getVideoTrack().addSink(remoteView.getPreview());
                        view.setViewRenderer(remoteView);
                        break;
                }
            }
        }

    }

    @Override
    public void onVideoTrack(VideoTrack stream, String refId, String sessionID) {
          setViews();
    }

    @Override
    public void onLocalCamera(VideoTrack stream) {
        super.onLocalCamera(stream);
        Log.e("newtest", "onLocalCamera");
        setViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        removeCurrentView();
    }

    private void removeCurrentView() {
        for (ActiveSession it : viewModel.getAppManager().videoViews) {
            it.getVideoTrack().removeSink(it.getViewRenderer().getPreview());
            it.setViewRenderer(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void setListeners() {
        binding.toolbar.setNavigationOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        binding.camSwitch.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                viewModel.switchCam();
            }
        });

        binding.ivSpeaker.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                viewModel.speakerToggle();
            }
        });

        binding.mute.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewModel.muteMic();
                    }
                });
            }
        });

        binding.camOnOff.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewModel.isCamEnable.get()) {
                            viewModel.pauseCam();
                            viewModel.getAppManager().viewCam = false;
                        }
                        else {
                            viewModel.resumeCam();
                            viewModel.getAppManager().viewCam = true;
                        }
                    }
                });

            }
        });

        binding.endCall.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                viewModel.endCall();
            }
        });

    }

    private void setBinding() {
        binding.setIsCamCasting(isCamCasting);
        binding.setIsCamEnable(viewModel.isCamEnable);
        binding.setIsMicEnable(viewModel.isMicEnable);
        binding.setIsSpeakerEnable(viewModel.isSpeakerEnabled);
        localView = binding.videoViewFull;
        remoteView = binding.videoStripView1;
    }

    private void defaultButtonStates() {
        viewModel.micEnabled();
        viewModel.camViewEnabled();
        speakerStateCheck();

    }

    private void speakerStateCheck() {
        if (!viewModel.getAppManager().speakerState) {
            viewModel.speakerDefaultState();
            viewModel.getAppManager().speakerState = true;
        } else {
            viewModel.speakerEnabledState();
        }
    }

    private void setCallData() {
        CallParams value = viewModel.getAppManager().getSession();
        if (value.getSessionType() == SessionType.CALL) {
            binding.setIsCamCasting(value.getMediaType() == MediaType.VIDEO);
        }
        CallNameModel title = getCallTitle(value.getCustomDataPacket().toString());
        binding.setCallTitle(title.getCalleName());
    }

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {
        viewExtension.INSTANCE.showSnackBar(binding.getRoot(), eventMessage.toString());
    }

    @Override
    public void onTimeTicks(String timer) {
        super.onTimeTicks(timer);
        binding.timer.setText(timer);
    }

    @Override
    public void callStatus(CallInfoResponse callInfoResponse) {
        super.callStatus(callInfoResponse);
        switch (callInfoResponse.getCallStatus()) {
            case OUTGOING_CALL_ENDED:
            case INSUFFICIENT_BALANCE:
                viewModel.getAppManager().speakerState = false;
                viewModel.getAppManager().isDialFrag = false;
                requireActivity().finish();
                break;
            default: {
            }
        }
    }

}