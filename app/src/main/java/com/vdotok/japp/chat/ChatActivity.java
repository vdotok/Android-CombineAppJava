package com.vdotok.japp.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseActivity;
import com.vdotok.japp.chat.viewmodel.ChatViewModel;
import com.vdotok.japp.dashboard.DashboardActivity;
import com.vdotok.japp.databinding.ActivityChatBinding;
import com.vdotok.japp.network.models.GroupModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;


public class ChatActivity extends BaseActivity<ChatViewModel, ActivityChatBinding> {
    private static Intent i;

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {

    }

    @Override
    public Class<ChatViewModel> getViewModel() {
        return ChatViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        GroupModel groupModel = (GroupModel) bundle.get("groupModel");
        viewModel.getAppManager().groupModel = groupModel;
        bundle.putSerializable("groupModel",groupModel);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.chat_nav_host_fragment);
        NavController navCo = null;
        if (navHostFragment != null) {
            navCo = navHostFragment.getNavController();
        }
        navCo.setGraph(R.navigation.chat_nav, bundle);

    }

    public static void openChatActivity(Context context, GroupModel groupModel) {
        i = new Intent(context, ChatActivity.class);
        i.putExtra("groupModel", groupModel);
        context.startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DashboardActivity.openDashboardActivity(this);
    }
}