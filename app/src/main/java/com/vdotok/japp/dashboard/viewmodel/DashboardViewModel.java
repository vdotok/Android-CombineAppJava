package com.vdotok.japp.dashboard.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.vdotok.connect.models.Presence;
import com.vdotok.japp.base.BaseViewModel;
import com.vdotok.japp.network.models.AllGroupsResponse;
import com.vdotok.japp.network.repos.DashboardRepository;

import java.util.ArrayList;

import javax.inject.Inject;

public class DashboardViewModel extends BaseViewModel {


    @Inject
    DashboardRepository dashboardRepository;

    @Inject
    public DashboardViewModel() {}


    public MutableLiveData<AllGroupsResponse> getAllGroups(String authToken) {
        return dashboardRepository.getAllGroups(this, "Bearer " + authToken);
    }

}
