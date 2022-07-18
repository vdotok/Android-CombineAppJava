package com.vdotok.japp.account.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.navigation.Navigation;

import com.vdotok.japp.R;
import com.vdotok.japp.account.viewmodel.AccountViewModel;
import com.vdotok.japp.base.BaseFragment;
import com.vdotok.japp.dashboard.DashboardActivity;
import com.vdotok.japp.databinding.FragmentLoginBinding;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.HttpResponseCodes;
import com.vdotok.japp.utils.viewExtension;


public class LoginFragment extends BaseFragment<AccountViewModel, FragmentLoginBinding> {

    public ObservableBoolean hasEnterUsername = new ObservableBoolean(false);
    public ObservableBoolean hasEnterPassword = new ObservableBoolean(false);
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> usernameEmail = new ObservableField<>();
    viewExtension extensions = new viewExtension();

    @Override
    public Class<AccountViewModel> getViewModel() {
        return AccountViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_login;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setBinding();
        setButtonClicks();
        return view;
    }

    private void setBinding() {
        binding.setUsernameEntered(hasEnterUsername);
        binding.setPasswordEntered(hasEnterPassword);
        binding.setUserEmail(usernameEmail);
        binding.setPassword(password);
        extensions.afterTextChanged(binding.edtEmail, hasEnterUsername, true);
        extensions.afterTextChanged(binding.edtPassword, hasEnterPassword, false);


    }

    private void setButtonClicks() {
        binding.btnSignIn.setOnClickListener(view -> {
            extensions.hideKeyboard(requireActivity());
            loginUser();
        });
        binding.btnSignUp.setOnClickListener(view -> Navigation.findNavController(binding.getRoot()).navigate(R.id.action_move_to_signup_user));
    }

    public void loginUser() {
        if (getActivity() != null) {
            viewModel.loginUser(usernameEmail.get(), password.get()).observe(getViewLifecycleOwner(), loginResponse -> {
                if (loginResponse.getStatus() == HttpResponseCodes.SUCCESS.getValue())
                    handleLoginResponse(loginResponse);
                else
                    viewExtension.INSTANCE.showSnackBar(binding.getRoot(), loginResponse.getMessage());
            });
        }
    }

    private void handleLoginResponse(LoginResponse loginResponse) {
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