package com.vdotok.japp.calling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseActivity;
import com.vdotok.japp.calling.viewmodel.CallingViewModel;
import com.vdotok.japp.databinding.ActivityCallingBinding;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.streaming.models.CallParams;


public class CallingActivity extends BaseActivity<CallingViewModel, ActivityCallingBinding> {
    public static final String SELECTED_SCREEN = "selected_screen";
    public static final String CALL_PARAMS = "call_params";

    public static void openCallActivity(Context context) {
        Intent i = new Intent(context, CallingActivity.class);
        i.putExtra(SELECTED_SCREEN, "not_connected");
        context.startActivity(i);
    }

    public static void openCallActivityConnectedCall(Context context) {
        Intent i = new Intent(context, CallingActivity.class);
        i.putExtra(SELECTED_SCREEN, "connected");
        context.startActivity(i);
    }

    public static void openCallActivityIncomingCall(Context context, CallParams callParams) {
        Intent i = new Intent(context, CallingActivity.class);
        i.putExtra(SELECTED_SCREEN, "not_connected");
        i.putExtra(CALL_PARAMS, callParams);
        context.startActivity(i);
    }

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {

    }

    @Override
    public Class<CallingViewModel> getViewModel() {

        return CallingViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_calling;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.getAppManager().isCallActivityOpened.setValue(true);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.call_nav_host_fragment);

        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavInflater navInflater = navController.getNavInflater();
        NavGraph navGraph = navInflater.inflate(R.navigation.call_nav);
        String destinationStr = getIntent().getStringExtra(SELECTED_SCREEN);
        int destination;
        if (destinationStr.equals("connected")) {
            destination = R.id.callConnectedFragment;
        } else {
            destination = R.id.callFragment;
        }
        CallParams callParams = (CallParams) getIntent().getParcelableExtra(CALL_PARAMS);
        Bundle bundle = new Bundle();
        if (callParams != null) {
            bundle.putParcelable(CALL_PARAMS, callParams);
        }
        navGraph.setStartDestination(destination);
        navController.setGraph(navGraph, bundle);

    }


    @Override
    protected void onStop() {
        super.onStop();
        viewModel.getAppManager().isCallActivityOpened.setValue(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.getAppManager().isCallActivityOpened.setValue(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getAppManager().isCallActivityOpened.setValue(true);
    }

    @Override
    public void onBackPressed() {
        viewModel.getAppManager().isCallActivityOpened.setValue(false);
        finish();
    }

}