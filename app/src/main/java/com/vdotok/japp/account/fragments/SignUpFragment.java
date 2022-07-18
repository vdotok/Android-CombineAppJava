package com.vdotok.japp.account.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.vdotok.japp.R;
import com.vdotok.japp.account.viewmodel.AccountViewModel;
import com.vdotok.japp.base.BaseFragment;
import com.vdotok.japp.dashboard.DashboardActivity;
import com.vdotok.japp.databinding.FragmentSignUpBinding;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.HttpResponseCodes;
import com.vdotok.japp.utils.viewExtension;


public class SignUpFragment extends BaseFragment<AccountViewModel, FragmentSignUpBinding> {
    public ObservableBoolean hasEnterUsername = new ObservableBoolean(false);
    public ObservableBoolean hasEnterEmail = new ObservableBoolean(false);
    public ObservableBoolean hasEnterPassword = new ObservableBoolean(false);
    public ObservableField<String> password = new ObservableField<String>();
    public ObservableField<String> email = new ObservableField<String>();
    public ObservableField<String> username = new ObservableField<String>();
    viewExtension extensions = new viewExtension();

    @Override
    public Class<AccountViewModel> getViewModel() {
        return AccountViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_sign_up;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setBinding();
        setButtonClick();
        return view;
    }

    private void setBinding() {
        binding.setUserEmailEntered(hasEnterEmail);
        binding.setUserNameEntered(hasEnterUsername);
        binding.setPasswordEntered(hasEnterPassword);
        binding.setUsername(username);
        binding.setUserEmail(email);
        binding.setPassword(password);
        extensions.afterTextChanged(binding.edtUserName,hasEnterUsername,true);
        extensions.afterTextChanged(binding.edtEmail,hasEnterEmail,true);
        extensions.afterTextChanged(binding.edtPassword,hasEnterPassword,false);
    }

    private void setButtonClick() {
        binding.btnSignIn.setOnClickListener(view -> requireActivity().onBackPressed());

        binding.btnSignUp.setOnClickListener(view -> {
            extensions.hideKeyboard(requireActivity());
            checkEmail();
        });
    }

    private void checkEmail() {
        if (getActivity() != null) {
            viewModel.checkEmail(email.get()).observe(getViewLifecycleOwner(), loginResponse -> {
                if (loginResponse.getStatus() == HttpResponseCodes.SUCCESS.getValue())
                    signupUser();
                else
                    viewExtension.INSTANCE.showSnackBar(binding.getRoot(), loginResponse.getMessage());
            });
        }
    }

    private void signupUser() {
        if (getActivity() != null) {
            viewModel.signupUser(email.get(), username.get(), password.get()).observe(getViewLifecycleOwner(), loginResponse -> {
                if (loginResponse.getStatus() == HttpResponseCodes.SUCCESS.getValue())
                    handleSignupResponse(loginResponse);
                else
                    viewExtension.INSTANCE.showSnackBar(binding.getRoot(), loginResponse.getMessage());
            });
        }
    }

    private void handleSignupResponse(LoginResponse loginResponse) {
        viewModel.getAppManager().getPref().saveLoggedInUser(loginResponse);
        DashboardActivity.openDashboardActivity(requireContext());
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