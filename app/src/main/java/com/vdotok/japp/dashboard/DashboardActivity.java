package com.vdotok.japp.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseActivity;
import com.vdotok.japp.dashboard.viewmodel.DashboardViewModel;
import com.vdotok.japp.databinding.ActivityDashboardBinding;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.setup.APICallObserversEnum;

public class DashboardActivity extends BaseActivity<DashboardViewModel, ActivityDashboardBinding> {


    public static void openDashboardActivity(Context context) {
        Intent i = new Intent(context, DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    @Override
    public Class<DashboardViewModel> getViewModel() {
        return DashboardViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_dashboard;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding();
        getRegister();
    }

    private void getRegister() {
        if (!viewModel.getAppManager().callSDKStatus.get())
            viewModel.getAppManager().connectCallSDK();
        if (!viewModel.getAppManager().chatSDKStatus.get())
            viewModel.getAppManager().connectChatSDK();

        setupUI();
    }

    private void setupUI() {
        LoginResponse userData = viewModel.getAppManager().getPref().getLoggedInUser();
        if (userData != null) {
            binding.setUsername(userData.getFullName());
        }
    }

    private void setBinding() {
        binding.setIsCallConnected(viewModel.getAppManager().callSDKStatus);
        binding.setIsChatConnected(viewModel.getAppManager().chatSDKStatus);
    }

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {

    }
}