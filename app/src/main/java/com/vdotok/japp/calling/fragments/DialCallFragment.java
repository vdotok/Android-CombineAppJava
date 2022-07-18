package com.vdotok.japp.calling.fragments;

import static com.vdotok.japp.calling.CallingActivity.CALL_PARAMS;
import static com.vdotok.japp.utils.StringExtension.getCallTitle;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseFragment;
import com.vdotok.japp.calling.viewmodel.CallingViewModel;
import com.vdotok.japp.databinding.FragmentDialCallBinding;
import com.vdotok.japp.models.CallNameModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.utils.OnSingleClickListener;
import com.vdotok.streaming.commands.CallInfoResponse;
import com.vdotok.streaming.enums.CallType;
import com.vdotok.streaming.enums.MediaType;
import com.vdotok.streaming.enums.SessionType;
import com.vdotok.streaming.models.CallParams;

import java.util.List;

public class DialCallFragment extends BaseFragment<CallingViewModel, FragmentDialCallBinding> {

    private Boolean isInitiator = false;
    private CallParams callParams = null;

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {

    }

    @Override
    public Class<CallingViewModel> getViewModel() {
        return CallingViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_dial_call;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        viewModel.getAppManager().isDialFrag = true;
        assert getArguments() != null;
        callParams = getArguments().getParcelable(CALL_PARAMS);
        if (callParams != null) {
            isInitiator = callParams.isInitiator();
            binding.setIsOutgoingCall(callParams.isInitiator());
            binding.setIsVideoCall(callParams.getMediaType() == MediaType.VIDEO);
            CallNameModel title = getCallTitle(callParams.getCustomDataPacket());
            if (callParams.getCallType() == CallType.ONE_TO_ONE) {
                binding.setCallTitle(title.getCalleName());
            } else {
                binding.setCallTitle(title.getGroupName());
            }
        } else {
            setBinding();
        }
        setButtonClick();


        return view;
    }

    @Override
    public void callStatus(CallInfoResponse callInfoResponse) {
        super.callStatus(callInfoResponse);
        switch (callInfoResponse.getCallStatus()) {
            case CALL_REJECTED:
                if (isInitiator && callInfoResponse.getCallParams() != null) {
                    updateMessageAndFinishActivity("Call Rejected by User");
                    viewModel.getAppManager().setSession(null);
                }
                break;
            case CALL_CONNECTED: {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_move_to_connect_call);
            }
            break;
            case CALL_MISSED:
                updateMessageAndFinishActivity("Call Missed");
                break;
            case SESSION_BUSY:
                if (isInitiator && callInfoResponse.getCallParams().getSessionUUID().equals(viewModel.getAppManager().getSession().getSessionUUID())) {
                    updateMessageAndFinishActivity(callInfoResponse.getResponseMessage());
                    viewModel.getAppManager().isDialFrag = false;
                }
                break;
            case INSUFFICIENT_BALANCE:
            case OUTGOING_CALL_ENDED:
                if (isInitiator && viewModel.getAppManager().isDialFrag) {
                    updateMessageAndFinishActivity(callInfoResponse.getResponseMessage());
                    viewModel.getAppManager().isDialFrag = false;
                }

                break;
            case SESSION_TIMEOUT:
            case NO_ANSWER_FROM_TARGET:
                viewModel.getAppManager().setSession(null);
                viewModel.getAppManager().videoViews.clear();
                viewModel.getAppManager().isDialFrag = false;
                updateMessageAndFinishActivity(callInfoResponse.getResponseMessage());
                break;
            default: {
            }
        }
    }

    private void updateMessageAndFinishActivity(String message) {
        binding.setCallMessageVisibility(true);
        binding.setCallMessage(message);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                requireActivity().finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 3000);
    }

    private void setBinding() {
        CallParams value = viewModel.getAppManager().getSession();
        binding.setIsOutgoingCall(value.isInitiator());
        isInitiator = value.isInitiator();
        if (value.getSessionType() == SessionType.CALL) {
            binding.setIsVideoCall(value.getMediaType() == MediaType.VIDEO);
        }
        CallNameModel title = getCallTitle(value.getCustomDataPacket());
        if (value.getCallType() == CallType.ONE_TO_ONE) {
            binding.setCallTitle(title.getCalleName());
        } else {
            binding.setCallTitle(title.getGroupName());
        }
    }

    private void setButtonClick() {
        binding.rejectCall.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                if (isInitiator)
                    viewModel.endCall();
                else
                    viewModel.rejectCall(callParams);
                getActivity().onBackPressed();
            }
        });
        binding.acceptCall.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                checkCallPermission(callParams);
            }
        });
    }


    private void checkCallPermission(CallParams value) {
        if (value.getMediaType() == MediaType.AUDIO) {
            Dexter.withContext(requireActivity())
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            viewModel.acceptCall(callParams);
                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_move_to_connect_call);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            showAudioPermissionsRequiredDialog();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        } else {
            Dexter.withContext(requireActivity())
                    .withPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                viewModel.acceptCall(callParams);
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_move_to_connect_call);
                            } else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                showVideoCallPermissionsRequiredDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }

                    }).check();
        }
    }

    @Override
    public void sendCallTimeOut() {
        super.sendCallTimeOut();
        viewModel.getAppManager().isDialFrag = false;
        requireActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.getAppManager().isDialFrag = false;
        if (!isInitiator){
            viewModel.getAppManager().stopCancelTimer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.getAppManager().isDialFrag = false;
        if (!isInitiator){
            viewModel.getAppManager().stopCancelTimer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getAppManager().isDialFrag = true;
    }

}