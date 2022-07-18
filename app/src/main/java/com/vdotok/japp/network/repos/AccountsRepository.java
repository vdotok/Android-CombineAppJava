package com.vdotok.japp.network.repos;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseViewModel;
import com.vdotok.japp.network.ApiService;
import com.vdotok.japp.network.models.CheckUserModel;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.models.LoginUserModel;
import com.vdotok.japp.network.models.SignUpModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.HttpResponseCodes;
import com.vdotok.japp.network.setup.NetworkConnectivity;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountsRepository {

    Context context;
    NetworkConnectivity networkConnectivity;
    ApiService apiService;
    Call<LoginResponse> loginUserCall;
    Call<LoginResponse> checkEmailCall;
    Call<LoginResponse> signupUserCall;

    @Inject
    public AccountsRepository(Application application, ApiService apiService) {
        this.context = application.getApplicationContext();
        this.networkConnectivity = new NetworkConnectivity(application.getApplicationContext());
        this.apiService = apiService;
    }


    public MutableLiveData<LoginResponse> loginUser(BaseViewModel viewModel, LoginUserModel loginUserModel) {
        MutableLiveData<LoginResponse> apiResponse = new MutableLiveData<>();
        if (networkConnectivity.isInternetAvailable()) {
            if (loginUserCall != null)
                loginUserCall.cancel();

            viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_START, context.getString(R.string.logging_in));
            loginUserCall = apiService.loginUser(loginUserModel);
            loginUserCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.body() != null && response.body().getStatus() == HttpResponseCodes.SUCCESS.getValue()) {
                        apiResponse.postValue(response.body());
                    } else {
                        viewModel.notifyObserver(APICallObserversEnum.ON_NO_DATA_RECEIVED, response.body().getMessage());
                    }
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_STOP, context.getString(R.string.api_stopped));
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_REQUEST_FAILURE, t.getMessage());
                }
            });

            return apiResponse;
        } else
            viewModel.notifyObserver(APICallObserversEnum.NO_INTERNET_CONNECTION, context.getString(R.string.no_internet));
        return apiResponse;
    }

    public MutableLiveData<LoginResponse> checkEmail(BaseViewModel viewModel, CheckUserModel checkUserModel) {
        MutableLiveData<LoginResponse> apiResponse = new MutableLiveData<>();
        if (networkConnectivity.isInternetAvailable()) {
            if (checkEmailCall != null)
                checkEmailCall.cancel();

            viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_START, context.getString(R.string.checking_email_available));
            checkEmailCall = apiService.checkEmail(checkUserModel);
            checkEmailCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.body() !=null && response.body().getStatus() == HttpResponseCodes.SUCCESS.getValue()) {
                        apiResponse.postValue(response.body());
                    } else {
                        viewModel.notifyObserver(APICallObserversEnum.ON_NO_DATA_RECEIVED, response.body().getMessage());
                    }
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_STOP, context.getString(R.string.api_stopped));
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_REQUEST_FAILURE, t.getMessage());
                }
            });

            return apiResponse;
        } else
            viewModel.notifyObserver(APICallObserversEnum.NO_INTERNET_CONNECTION, context.getString(R.string.no_internet));
        return apiResponse;
    }

    public MutableLiveData<LoginResponse> signupUser(BaseViewModel viewModel, SignUpModel signUpModel) {
        MutableLiveData<LoginResponse> apiResponse = new MutableLiveData<>();
        if (networkConnectivity.isInternetAvailable()) {
            if (signupUserCall != null)
                signupUserCall.cancel();

            viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_START, context.getString(R.string.signing_in));
            signupUserCall = apiService.signUp(signUpModel);
            signupUserCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.body() !=null && response.body().getStatus() == HttpResponseCodes.SUCCESS.getValue()) {
                        apiResponse.postValue(response.body());
                    } else {
                        viewModel.notifyObserver(APICallObserversEnum.ON_NO_DATA_RECEIVED, response.body().getMessage());
                    }
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_STOP, context.getString(R.string.api_stopped));
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_REQUEST_FAILURE, t.getMessage());
                }
            });

            return apiResponse;
        } else
            viewModel.notifyObserver(APICallObserversEnum.NO_INTERNET_CONNECTION, context.getString(R.string.no_internet));
        return apiResponse;
    }

}
