package com.vdotok.japp.userlisting.fragments;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.navigation.Navigation;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.vdotok.connect.models.Presence;
import com.vdotok.japp.R;
import com.vdotok.japp.account.AccountActivity;
import com.vdotok.japp.base.BaseFragment;
import com.vdotok.japp.chat.ChatActivity;
import com.vdotok.japp.databinding.FragmentAllUserBinding;
import com.vdotok.japp.network.models.CreateGroupModel;
import com.vdotok.japp.network.models.CreateGroupResponse;
import com.vdotok.japp.network.models.GetAllUsersResponseModel;
import com.vdotok.japp.network.models.GroupModel;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.models.UserModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.HttpResponseCodes;
import com.vdotok.japp.userlisting.adapter.SelectUserContactAdapter;
import com.vdotok.japp.userlisting.viewmodel.UserListingViewModel;
import com.vdotok.japp.utils.viewExtension;
import com.vdotok.streaming.enums.CallType;
import com.vdotok.streaming.enums.MediaType;
import com.vdotok.streaming.enums.SessionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.jvm.internal.Intrinsics;


public class AllUserFragment extends BaseFragment<UserListingViewModel, FragmentAllUserBinding> {
    public ObservableField<String> edtSearch = new ObservableField<>();
    View view;
    SelectUserContactAdapter adapter;

    @Override
    public Class<UserListingViewModel> getViewModel() {
        return UserListingViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_all_user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        UiSetup();
        setAdapter();
        setClickListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllUsers();
    }

    private void getAllUsers() {
        if (getActivity() != null) {
            LoginResponse userData = viewModel.getAppManager().getPref().getLoggedInUser();
            if (userData != null) {
                viewModel.getAllUsers("Bearer " + userData.getAuthToken()).observe(getViewLifecycleOwner(), allUsersResponseModel -> {
                    if (allUsersResponseModel.getStatus() == HttpResponseCodes.SUCCESS.getValue())
                        handleGetAllUsersResponse(allUsersResponseModel);
                    else
                        viewExtension.INSTANCE.showSnackBar(binding.getRoot(), allUsersResponseModel.getMessage());
                });
            }
        }
    }

    private void handleGetAllUsersResponse(GetAllUsersResponseModel allUsersResponseModel) {
        if (allUsersResponseModel.getUsers().size() > 0) {
            adapter.updateData(allUsersResponseModel.getUsers());
        }
    }

    private void setClickListeners() {
        binding.customToolbar.imgArrowBack.setOnClickListener(view -> requireActivity().onBackPressed());
        binding.customToolbar.imgDone.setOnClickListener(v -> {
            showProgress(getString(R.string.logging_out));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                hideProgress();
                logoutUser();
            }, 3000);
        });
        binding.tvAddGroupChat.setOnClickListener(view -> {
            viewExtension.INSTANCE.hideKeyboard(requireActivity());
            edtSearch.set("");
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_move_to_create_group_users_list);
        });

        viewExtension.INSTANCE.searchAfterTextChanged(binding.searchEditText, adapter);
    }

    private void logoutUser() {
        viewModel.getAppManager().getChatManager().disconnect();
        viewModel.getAppManager().getCallClient().unRegister(viewModel.getAppManager().getPref().getLoggedInUser().getRefId());
        viewModel.getAppManager().getPref().clearApplicationPrefs();
        AccountActivity.openAccountsActivity(requireContext());
    }

    private void setAdapter() {
        adapter = new SelectUserContactAdapter(requireActivity(),new ArrayList<>(), false, new SelectUserContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onChatIconClick(int position) {
                createGroup(position);
            }

            @Override
            public void onCallIconClick(int position) {
                Dexter.withContext(requireActivity())
                        .withPermission(Manifest.permission.RECORD_AUDIO)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                UserModel selectedUser = adapter.getItem(position);
                                viewModel.dialOne2OneCall(selectedUser,requireContext(), CallType.ONE_TO_ONE, MediaType.AUDIO, SessionType.CALL);
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
            }

            @Override
            public void onVideoIconClick(int position) {
                Dexter.withContext(requireActivity())
                        .withPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {

                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    UserModel selectedUser = adapter.getItem(position);
                                    viewModel.dialOne2OneCall(selectedUser, requireContext(), CallType.ONE_TO_ONE, MediaType.VIDEO, SessionType.CALL);
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

            @Override
            public void searchResult(int position) {
                if (edtSearch.get() != null) {
                    binding.setEmptyUserList(position == 0 && Objects.requireNonNull(edtSearch.get()).length() > 0);
                }
            }
        });
        binding.rcvUserList.setAdapter(adapter);
    }

    private void createGroup(int position) {
        String title = Intrinsics.stringPlus(viewModel.getAppManager().getUserData().getFullName(), "-");
        String groupTitle = Intrinsics.stringPlus(title, adapter.dataModelList.get(position).toString());
        CreateGroupModel model = new CreateGroupModel();
        model.setGroupTitle(groupTitle);
        model.setParticipants(getParticipant(adapter.dataModelList.get(position)));
        model.setAutoCreated(1);
        createGroup(model);

    }


    private ArrayList<Integer> getParticipant(UserModel selectedUsersList) {
        ArrayList<Integer> list = new ArrayList<>();
        if (selectedUsersList != null) {
            list.add((Integer.parseInt(selectedUsersList.getId())));
        }
        return list;
    }

    private void UiSetup() {
        binding.setShowBackIcon(true);
        binding.setShowCheckIcon(false);
        binding.setShowIcon(true);
        binding.setToolbarTitle(getString(R.string.title_new_chat));
        binding.setSearch(edtSearch);

    }

    private void createGroup(CreateGroupModel model) {
        if (getActivity() != null) {
            LoginResponse userData = viewModel.getAppManager().getPref().getLoggedInUser();
            if (userData != null) {
                viewModel.createGroup("Bearer " + userData.getAuthToken(), model).observe(getViewLifecycleOwner(), createGroupResponse -> {
                    if (createGroupResponse.getStatus() == HttpResponseCodes.SUCCESS.getValue())
                        handleCreateGroupResponse(createGroupResponse);
                    else
                        viewExtension.INSTANCE.showSnackBar(binding.getRoot(), createGroupResponse.getMessage());
                });
            }
        }
    }

    private void handleCreateGroupResponse(CreateGroupResponse createGroupResponse) {
        if (createGroupResponse.getGroupModel() != null) {
            GroupModel groupModel = createGroupResponse.getGroupModel();
            viewModel.getAppManager().getChatManager().subscribeTopic(groupModel.getChannelKey(), groupModel.getChannelName());
            viewModel.getAppManager().mapGroupMessages.put(groupModel.getChannelName(),new ArrayList<>());
            ChatActivity.openChatActivity(requireContext(), groupModel);
        }
    }

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {
        switch (event) {
            case ON_API_CALL_START:
                showProgress(eventMessage.toString());
                break;
            case ON_API_CALL_STOP:
                hideProgress();
                break;
            case ON_API_REQUEST_FAILURE:
            case ON_NO_DATA_RECEIVED:
            case NO_INTERNET_CONNECTION:
                viewExtension.INSTANCE.showSnackBar(binding.getRoot(), eventMessage.toString());
                break;
        }
    }


}
