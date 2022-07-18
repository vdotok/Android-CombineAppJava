package com.vdotok.japp.userlisting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.vdotok.connect.models.Presence;
import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseActivity;
import com.vdotok.japp.databinding.ActivityUserListingBinding;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.userlisting.viewmodel.UserListingViewModel;

import java.util.ArrayList;

public class UserListingActivity extends BaseActivity<UserListingViewModel, ActivityUserListingBinding> {
    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {

    }

    @Override
    public Class<UserListingViewModel> getViewModel() {
        return UserListingViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_user_listing;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void openUserListActivity(Context context) {
        Intent i = new Intent(context, UserListingActivity.class);
        context.startActivity(i);
    }

}