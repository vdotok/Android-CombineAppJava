package com.vdotok.japp.network.repos;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseViewModel;
import com.vdotok.japp.network.ApiService;
import com.vdotok.japp.network.models.CreateGroupModel;
import com.vdotok.japp.network.models.CreateGroupResponse;
import com.vdotok.japp.network.models.GetAllUsersResponseModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.HttpResponseCodes;
import com.vdotok.japp.network.setup.NetworkConnectivity;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersListsRepository {

    Context context;
    NetworkConnectivity networkConnectivity;
    ApiService apiService;
    Call<GetAllUsersResponseModel> getAllUsersCall;
    Call<CreateGroupResponse> createGroupCall;

    @Inject
    public UsersListsRepository(Application application, ApiService apiService) {
        this.context = application.getApplicationContext();
        this.networkConnectivity = new NetworkConnectivity(application.getApplicationContext());
        this.apiService = apiService;
    }

    public MutableLiveData<GetAllUsersResponseModel> getAllUsers(BaseViewModel viewModel, String authToken) {
        MutableLiveData<GetAllUsersResponseModel> apiResponse = new MutableLiveData<>();
        if (networkConnectivity.isInternetAvailable()) {
            if (getAllUsersCall != null)
                getAllUsersCall.cancel();

            viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_START, context.getString(R.string.loading_all_users));
            getAllUsersCall = apiService.getAllUsers(authToken);
            getAllUsersCall.enqueue(new Callback<GetAllUsersResponseModel>() {
                @Override
                public void onResponse(Call<GetAllUsersResponseModel> call, Response<GetAllUsersResponseModel> response) {
                    if (response.body() != null && response.body().getStatus() == HttpResponseCodes.SUCCESS.getValue()) {
                        apiResponse.postValue(response.body());
                    } else {
                        viewModel.notifyObserver(APICallObserversEnum.ON_NO_DATA_RECEIVED, response.body().getMessage());
                    }
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_STOP, context.getString(R.string.api_stopped));
                }

                @Override
                public void onFailure(Call<GetAllUsersResponseModel> call, Throwable t) {
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_REQUEST_FAILURE, t.getMessage());
                }
            });

            return apiResponse;
        } else
            viewModel.notifyObserver(APICallObserversEnum.NO_INTERNET_CONNECTION, context.getString(R.string.no_internet));
        return apiResponse;
    }

    public MutableLiveData<CreateGroupResponse> createGroup(BaseViewModel viewModel, String authToken, CreateGroupModel createGroupModel) {
        MutableLiveData<CreateGroupResponse> apiResponse = new MutableLiveData<>();
        if (networkConnectivity.isInternetAvailable()) {
            if (createGroupCall != null)
                createGroupCall.cancel();

            viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_START, context.getString(R.string.creating_group));
            createGroupCall = apiService.createGroup(authToken, createGroupModel);
            createGroupCall.enqueue(new Callback<CreateGroupResponse>() {
                @Override
                public void onResponse(Call<CreateGroupResponse> call, Response<CreateGroupResponse> response) {
                    if (response.body() !=null && response.body().getStatus() == HttpResponseCodes.SUCCESS.getValue()) {
                        apiResponse.postValue(response.body());
                    } else {
                        viewModel.notifyObserver(APICallObserversEnum.ON_NO_DATA_RECEIVED, response.body().getMessage());
                    }
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_STOP, context.getString(R.string.api_stopped));
                }

                @Override
                public void onFailure(Call<CreateGroupResponse> call, Throwable t) {
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_REQUEST_FAILURE, t.getMessage());
                }
            });

            return apiResponse;
        } else
            viewModel.notifyObserver(APICallObserversEnum.NO_INTERNET_CONNECTION, context.getString(R.string.no_internet));
        return apiResponse;
    }

}
