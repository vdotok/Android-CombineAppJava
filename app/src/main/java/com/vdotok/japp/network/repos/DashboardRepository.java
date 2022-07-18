package com.vdotok.japp.network.repos;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseViewModel;
import com.vdotok.japp.network.ApiService;
import com.vdotok.japp.network.models.AllGroupsResponse;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.network.setup.HttpResponseCodes;
import com.vdotok.japp.network.setup.NetworkConnectivity;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardRepository {

    Context context;
    NetworkConnectivity networkConnectivity;
    ApiService apiService;
    Call<AllGroupsResponse> getGroupsCall;

    @Inject
    public DashboardRepository(Application application, ApiService apiService) {
        this.context = application.getApplicationContext();
        this.networkConnectivity = new NetworkConnectivity(application.getApplicationContext());
        this.apiService = apiService;
    }

    public MutableLiveData<AllGroupsResponse> getAllGroups(BaseViewModel viewModel, String authToken) {
        MutableLiveData<AllGroupsResponse> apiResponse = new MutableLiveData<>();
        if (networkConnectivity.isInternetAvailable()) {
            if (getGroupsCall != null)
                getGroupsCall.cancel();

            viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_START, context.getString(R.string.loading_groups));
            getGroupsCall = apiService.getAllGroups(authToken);
            getGroupsCall.enqueue(new Callback<AllGroupsResponse>() {
                @Override
                public void onResponse(Call<AllGroupsResponse> call, Response<AllGroupsResponse> response) {
                    if (response.body() != null && response.body().getStatus() == HttpResponseCodes.SUCCESS.getValue()) {
                        apiResponse.postValue(response.body());
                    } else {
                        viewModel.notifyObserver(APICallObserversEnum.ON_NO_DATA_RECEIVED, response.body().getMessage());
                    }
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_CALL_STOP, context.getString(R.string.api_stopped));
                }

                @Override
                public void onFailure(Call<AllGroupsResponse> call, Throwable t) {
                    viewModel.notifyObserver(APICallObserversEnum.ON_API_REQUEST_FAILURE, t.getMessage());
                }
            });

            return apiResponse;
        } else
            viewModel.notifyObserver(APICallObserversEnum.NO_INTERNET_CONNECTION, context.getString(R.string.no_internet));
        return apiResponse;
    }
}
