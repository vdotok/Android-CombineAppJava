package com.vdotok.japp.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.vdotok.japp.R;
import com.vdotok.japp.account.viewmodel.AccountViewModel;
import com.vdotok.japp.base.BaseActivity;
import com.vdotok.japp.databinding.ActivityAccountBinding;
import com.vdotok.japp.network.setup.APICallObserversEnum;


public class AccountActivity extends BaseActivity<AccountViewModel, ActivityAccountBinding> {
    @Override
    public Class<AccountViewModel> getViewModel() {
        return AccountViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_account;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static void openAccountsActivity(Context context) {
        Intent i = new Intent(context, AccountActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {

    }
}