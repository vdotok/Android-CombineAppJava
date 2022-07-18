package com.vdotok.japp.userlisting.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;

import com.vdotok.connect.models.Presence;
import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseFragment;
import com.vdotok.japp.chat.ChatActivity;
import com.vdotok.japp.databinding.FragmentGroupCreationBinding;
import com.vdotok.japp.network.models.CreateGroupModel;
import com.vdotok.japp.network.models.CreateGroupResponse;
import com.vdotok.japp.network.models.GetAllUsersResponseModel;
import com.vdotok.japp.network.models.GroupModel;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.models.UserModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.HttpResponseCodes;
import com.vdotok.japp.userlisting.adapter.SelectUserContactAdapter;
import com.vdotok.japp.userlisting.dialog.CreateGroupDialog;
import com.vdotok.japp.userlisting.viewmodel.UserListingViewModel;
import com.vdotok.japp.utils.OnSingleClickListener;
import com.vdotok.japp.utils.viewExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.jvm.internal.Intrinsics;

public class GroupCreationFragment extends BaseFragment<UserListingViewModel, FragmentGroupCreationBinding> {
    SelectUserContactAdapter adapter;
    public ObservableField<String> edtSearch = new ObservableField<String>();

    @Override
    public Class<UserListingViewModel> getViewModel() {
        return UserListingViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_group_creation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
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
        viewExtension.INSTANCE.searchAfterTextChanged(binding.searchEditText, adapter);
        binding.customToolbar.imgArrowBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        binding.customToolbar.imgDone.setOnClickListener(view -> {
            viewExtension.INSTANCE.hideKeyboard(requireActivity());
            if (adapter.getSelectedUsers() != null) {
                onCreateGroupClick();
            } else {
                viewExtension.INSTANCE.showSnackBar(binding.getRoot(), getString(R.string.no_user_select));
            }
        });
    }

    private void onCreateGroupClick() {
        List<UserModel> selectedUsersList = adapter.getSelectedUsers();

        if (selectedUsersList.size() == 1) {
            String groupTitle = getGroupTitle(selectedUsersList);
            if (getGroupTitle(selectedUsersList) != null) {
                prepareGroupData(groupTitle);
            }
        } else {
            new CreateGroupDialog(this::prepareGroupData).show(
                    getChildFragmentManager(),
                    CreateGroupDialog.TAG);
        }
    }

    private void prepareGroupData(String groupTitle) {
        List<UserModel> selectedUsersList = adapter.getSelectedUsers();
        if (selectedUsersList.size() > 0) {
            CreateGroupModel model = new CreateGroupModel();
            model.setGroupTitle(groupTitle);
            model.setParticipants(getParticipantsIds(selectedUsersList));
            if (selectedUsersList.size() == 1) model.setAutoCreated(1);
            else model.setAutoCreated(0);

            createGroup(model);
        }
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

    private ArrayList<Integer> getParticipantsIds(List<UserModel> userModelList) {
        ArrayList<Integer> idList = new ArrayList<>();
        for (int i = 0; i < userModelList.size(); i++) {
            if (userModelList.get(i).getId() != null) {
                idList.add(Integer.parseInt(userModelList.get(i).getId()));
            }
        }
        return idList;
    }

    private String getGroupTitle(List<UserModel> selectedUsersList) {
        LoginResponse userData = viewModel.getAppManager().getPref().getLoggedInUser();
        String title = Intrinsics.stringPlus(userData.getFullName(), "-");
        for (UserModel userName : selectedUsersList) {
            title = title.concat(userName.getFullName());
        }

        return title;
    }

    private void setAdapter() {
        adapter = new SelectUserContactAdapter(requireActivity(),new ArrayList<>(), true, new SelectUserContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UserModel item = adapter.dataModelList.get(position);
                item.setSelected(!item.getSelected());
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onChatIconClick(int position) {

            }

            @Override
            public void onCallIconClick(int position) {

            }

            @Override
            public void onVideoIconClick(int position) {

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


    private void UiSetup() {
        binding.setShowBackIcon(true);
        binding.setShowCheckIcon(true);
        binding.setShowIcon(true);
        binding.setToolbarTitle(getString(R.string.title_new_group_chat));
        binding.setSearch(edtSearch);
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