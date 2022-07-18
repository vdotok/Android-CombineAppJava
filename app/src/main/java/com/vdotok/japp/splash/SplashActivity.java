package com.vdotok.japp.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.vdotok.japp.R;
import com.vdotok.japp.account.AccountActivity;
import com.vdotok.japp.base.BaseActivity;
import com.vdotok.japp.dashboard.DashboardActivity;
import com.vdotok.japp.databinding.ActivitySplashBinding;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.splash.viewmodel.SplashViewModel;

/**
 * Created By: VdoTok
 * Date & Time: On 25/05/2022 At 3:25 PM in 2022
 */

public class SplashActivity extends BaseActivity<SplashViewModel, ActivitySplashBinding> {
    @Override
    public Class<SplashViewModel> getViewModel() {
        return SplashViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkUserData();
    }

    private void checkUserData() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginResponse userData = viewModel.getAppManager().getPref().getLoggedInUser();
                if (userData != null) {
                    DashboardActivity.openDashboardActivity(SplashActivity.this);
                } else {
                    AccountActivity.openAccountsActivity(SplashActivity.this);
                }
            }
        }, 2000 );
    }

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {

    }
}
