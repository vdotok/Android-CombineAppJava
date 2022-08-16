package com.vdotok.japp.dashboard.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.vdotok.connect.models.HeaderModel;
import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.MessageType;
import com.vdotok.connect.models.Presence;
import com.vdotok.connect.models.ReceiptType;
import com.vdotok.japp.R;
import com.vdotok.japp.account.AccountActivity;
import com.vdotok.japp.base.BaseFragment;
import com.vdotok.japp.chat.ChatActivity;
import com.vdotok.japp.chat.dialogs.StoragePermissionDialogFragment;
import com.vdotok.japp.chat.interfaces.OnCompleteActionListener;
import com.vdotok.japp.chat.interfaces.OnNewMessageListener;
import com.vdotok.japp.dashboard.DashboardActivity;
import com.vdotok.japp.dashboard.adapter.AllGroupListAdapter;
import com.vdotok.japp.dashboard.interfaces.OnStoragePermissionListener;
import com.vdotok.japp.dashboard.viewmodel.DashboardViewModel;
import com.vdotok.japp.databinding.FragmentGroupListingBinding;
import com.vdotok.japp.manager.AppManager;
import com.vdotok.japp.network.models.AllGroupsResponse;
import com.vdotok.japp.network.models.GroupModel;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.HttpResponseCodes;
import com.vdotok.japp.splash.SplashActivity;
import com.vdotok.japp.userlisting.UserListingActivity;
import com.vdotok.japp.utils.ChatFileUtils;
import com.vdotok.japp.utils.viewExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupListingFragment extends BaseFragment<DashboardViewModel, FragmentGroupListingBinding> {
    AllGroupListAdapter adapter;
    public List<GroupModel> groupDataList = new ArrayList();

    @Override
    public Class<DashboardViewModel> getViewModel() {
        return DashboardViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_group_listing;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        UiSetup();
        setAdapter();
        setClickListeners();
        addPullToRefresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllGroups();
    }

    private void setClickListeners() {
        binding.btnNewChat.setOnClickListener(view ->
                UserListingActivity.openUserListActivity(requireContext()));
        binding.addGroup.setOnClickListener(view ->
                UserListingActivity.openUserListActivity(requireContext()));
        binding.btnRefresh.setOnClickListener(view ->
                getAllGroups());

        binding.customToolbar.imgDone.setOnClickListener(v -> {
            showProgress(getString(R.string.logging_out));
            if (subscriptionHandler != null)
                subscriptionHandler.removeCallbacks(subscriptionRunnable);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                hideProgress();
                logoutUser();
            }, 1500);
        });
    }

    private void logoutUser() {
        viewModel.getAppManager().getCallClient().unRegister(viewModel.getAppManager().getPref().getLoggedInUser().getRefId());
        viewModel.getAppManager().getChatManager().disconnect();
        viewModel.getAppManager().getPref().clearApplicationPrefs();
        AccountActivity.openAccountsActivity(requireContext());
    }

    private void getAllGroups() {
        binding.swipeRefreshLay.setRefreshing(false);
        if (getActivity() != null) {
            LoginResponse userData = viewModel.getAppManager().getPref().getLoggedInUser();
            if (userData != null) {
                viewModel.getAllGroups(userData.getAuthToken()).observe(getViewLifecycleOwner(), allGroupsResponse -> {
                    if (allGroupsResponse.getStatus() == HttpResponseCodes.SUCCESS.getValue())
                        handleGetAllGroupResponse(allGroupsResponse);
                    else
                        viewExtension.INSTANCE.showSnackBar(binding.getRoot(), allGroupsResponse.getMessage());
                });
            }
        }
    }

    private void handleGetAllGroupResponse(AllGroupsResponse allGroupsResponse) {
        if (allGroupsResponse.groups.size() > 0) {
            viewModel.getAppManager().getPref().saveGroupList(allGroupsResponse.groups);
            groupDataList.clear();
            groupDataList.addAll(viewModel.getAppManager().getPref().getGroupList());
            addLastMessageGroupToTop();
            setGroupMapData((ArrayList<GroupModel>) groupDataList);
            binding.setIsGroupListEmpty(false);
            doSubscribe();
        } else {
            binding.setIsGroupListEmpty(true);
        }

    }

    private void addPullToRefresh() {
        binding.swipeRefreshLay.setEnabled(true);
        binding.swipeRefreshLay.setOnRefreshListener(() -> getAllGroups());
    }


    private void setAdapter() {
        adapter = new AllGroupListAdapter(requireContext(), viewModel.getAppManager().getPref().getLoggedInUser().getFullName(), new ArrayList<>(), true, new AllGroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupModel groupModel) {
                ChatActivity.openChatActivity(requireContext(), groupModel);
            }

            @Override
            public int getUnreadCount(GroupModel groupModel) {
                int result = 0;
                if (AppManager.mapUnreadCount.get(groupModel.getChannelName()) != null) {
                    result = AppManager.mapUnreadCount.get(groupModel.getChannelName());
                } else {
                    result = 0;
                }
                return result;
            }

            @Override
            public ArrayList<Message> getMessageList(GroupModel groupModel) {
                ArrayList<Message> list = null;
                if (AppManager.mapLastMessage.get(groupModel.getChannelName()) != null) {
                    list = AppManager.mapLastMessage.get(groupModel.getChannelName());
                } else {
                    list = new ArrayList();
                }
                return list;
            }

            @Override
            public void removeUnReadCount(GroupModel groupModel) {
                AppManager.mapUnreadCount.remove(groupModel.getChannelName());
            }
        });
        binding.rcvGroupList.setAdapter(adapter);
        adapter.updatePresenceData(viewModel.getAppManager().getPresenceList());
    }

    private void UiSetup() {
        binding.setShowBackIcon(false);
        binding.setShowCheckIcon(false);
        binding.setShowIcon(true);
        binding.setToolbarTitle(getString(R.string.title_chat_room));
        viewModel.getAppManager().messageUpdateLiveData.observe(this.getViewLifecycleOwner(), message -> {
            adapter.notifyDataSetChanged();
            viewModel.sendAcknowledgeMsgToGroup(message);
        });
    }

    private void checkForStoragePermissions(
            String msgId,
            HeaderModel headerModel,
            byte[] byteArray
    ) throws IOException {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            new StoragePermissionDialogFragment(msgId, headerModel, byteArray, new OnStoragePermissionListener() {
                @Override
                public void saveFile(String msgId, HeaderModel headerModel, byte[] byteArray) throws IOException {
                    saveFileMessage(msgId, headerModel, byteArray);
                }

                @Override
                public void sendTextMessage(String message) {
                    sendTextMessageGroup(message);
                }

            }).show(
                    getChildFragmentManager(),
                    StoragePermissionDialogFragment.TAG);
        } else {
            saveFileMessage(msgId, headerModel, byteArray);
        }

    }

    public void sendTextMessageGroup(String message) {
        Message chatModel = null;
        if (viewModel.getAppManager().groupModel != null) {
            if (viewModel.getAppManager().getUserData().getRefId() != null) {
                chatModel = new Message(
                        String.valueOf(System.currentTimeMillis()),
                        viewModel.getAppManager().groupModel.getChannelName(),
                        viewModel.getAppManager().groupModel.getChannelKey(),
                        viewModel.getAppManager().getUserData().getRefId(),
                        MessageType.text,
                        message.trim(),
                        0f,
                        viewModel.getAppManager().groupModel.getParticipants().size() > 1,
                        ReceiptType.SENT.INSTANCE.getValue(),
                        0,
                        System.currentTimeMillis(),
                        0f,
                        0
                );
            }
            if (chatModel != null) {
                viewModel.getAppManager().getChatManager().publishMessage(chatModel);
            }
        }
    }

    public void saveFileMessage(String msgId, HeaderModel headerModel, byte[] byteArray) throws IOException {
        ChatFileUtils.INSTANCE.checkAndroidVersionToSave(requireContext(), headerModel, byteArray, new OnCompleteActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete() {
                ChatFileUtils.INSTANCE.sendAttachmentMessage(headerModel, ChatFileUtils.INSTANCE.file, msgId, viewModel.getAppManager().groupModel, viewModel.getAppManager(), new OnNewMessageListener() {
                    @Override
                    public void onNewMessage(Message message) {
                        addLastMessageGroupToTop();
                        adapter.notifyDataSetChanged();
                        viewModel.sendAcknowledgeMsgToGroup(message);
                    }
                });
            }
        });
    }

    private void addLastMessageGroupToTop() {
        List<GroupModel> prefsGroupsList = (ArrayList<GroupModel>) viewModel.getAppManager().getPref().getGroupList();
        if (prefsGroupsList.size() == groupDataList.size()) {
            String lastGroupMessageKey = viewModel.getAppManager().lastMessageGroupKey;
            int lastUpdatedGroupIndex = -1;
            GroupModel lastUpdatedGroupModel = null;
            int i = 0;
            for (GroupModel groupModel : groupDataList) {
                if (groupModel.getChannelKey().equals(lastGroupMessageKey)) {
                    lastUpdatedGroupIndex = i;
                    lastUpdatedGroupModel = groupModel;
                }
                i++;
            }
            if (lastUpdatedGroupModel != null) {
                groupDataList.remove(lastUpdatedGroupIndex);
                groupDataList.add(0, lastUpdatedGroupModel);
            }
        }

        adapter.updateData(groupDataList);
    }

    private Handler subscriptionHandler = new Handler(Looper.getMainLooper());
    private Runnable subscriptionRunnable = () -> {
        for (GroupModel group : groupDataList) {
            viewModel.getAppManager().getChatManager().subscribeTopic(group.getChannelKey(), group.getChannelName());
        }
    };

    private void doSubscribe() {
        subscriptionHandler.postDelayed(subscriptionRunnable, 2000L);
    }

    private void setGroupMapData(ArrayList<GroupModel> groupList) {
        if (!groupList.isEmpty()) {
            int index = 0;
            for (GroupModel groupModel : groupList) {
                if (!viewModel.getAppManager().mapGroupMessages.containsKey(groupModel.getChannelName())) {
                    viewModel.getAppManager().mapGroupMessages.put(groupModel.getChannelName(), new ArrayList());
                }
                index++;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFileReceivedCompleted(HeaderModel headerModel, byte[] byteArray, String msgId) throws IOException {
        super.onFileReceivedCompleted(headerModel, byteArray, msgId);
        for (GroupModel group : groupDataList) {
            if (group.getChannelName().equals(headerModel.getTopic())) {
                viewModel.getAppManager().groupModel = group;
            }

        }
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkForStoragePermissions(msgId, headerModel, byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageArrive(Message message) {
        super.onMessageArrive(message);
        viewModel.getAppManager().lastMessageGroupKey = message.getKey();
        if (!message.getFrom().equals(viewModel.getOwnRefID())) {
            if (message.getTo() != null) {
                viewModel.getAppManager().mapUnreadCount.put(message.getTo(), viewModel.getAppManager().mapUnreadCount.containsKey(message.getTo()) ? viewModel.getAppManager().mapUnreadCount.get(message.getTo()) + 1 : 1);
            }
        }
        if (getActivity()!=null) {
            getActivity().runOnUiThread(this::addLastMessageGroupToTop);
        }
        viewModel.sendAcknowledgeMsgToGroup(message);
        viewModel.getAppManager().updateMessageMapData(message);
    }

    @Override
    public void onPresenceReceived(ArrayList<Presence> who) {
        super.onPresenceReceived(who);
        saveUpdatePresenceList(who);
    }

    private void saveUpdatePresenceList(ArrayList<Presence> list) {
        adapter.updatePresenceData(viewModel.getAppManager().getPresenceList());
        adapter.notifyItemRangeChanged(0, groupDataList.size());

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