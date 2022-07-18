package com.vdotok.japp.account.viewmodel;

import static com.vdotok.japp.constants.ApplicationConstants.SDK_PROJECT_ID;

import androidx.lifecycle.MutableLiveData;

import com.vdotok.japp.base.BaseViewModel;
import com.vdotok.japp.network.models.CheckUserModel;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.models.LoginUserModel;
import com.vdotok.japp.network.models.SignUpModel;
import com.vdotok.japp.network.repos.AccountsRepository;

import javax.inject.Inject;

/**
 * Created By: VdoTok
 * Date & Time: On 25/05/2022 At 3:27 PM in 2022
 */
public class AccountViewModel extends BaseViewModel {

    @Inject
    AccountsRepository accountsRepository;

    @Inject
    public AccountViewModel() {}

    public MutableLiveData<LoginResponse> loginUser(String email, String password) {
        LoginUserModel loginUserModel = new LoginUserModel();
        loginUserModel.setEmail(email);
        loginUserModel.setPassword(password);
        loginUserModel.setProject_id(SDK_PROJECT_ID);
        return accountsRepository.loginUser(this, loginUserModel);
    }

    public MutableLiveData<LoginResponse> checkEmail(String email) {
        CheckUserModel checkUserModel = new CheckUserModel();
        checkUserModel.setEmail(email);
        return accountsRepository.checkEmail(this, checkUserModel);
    }

    public MutableLiveData<LoginResponse> signupUser(String email, String fullName, String password) {
        SignUpModel signUpModel = new SignUpModel();
        signUpModel.setEmail(email);
        signUpModel.setFullName(fullName);
        signUpModel.setPassword(password);
        signUpModel.setProject_id(SDK_PROJECT_ID);
        return accountsRepository.signupUser(this, signUpModel);
    }

}
