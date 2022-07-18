package com.vdotok.japp.userlisting.viewmodel;


import androidx.lifecycle.MutableLiveData;

import com.vdotok.japp.base.BaseViewModel;
import com.vdotok.japp.network.models.CreateGroupModel;
import com.vdotok.japp.network.models.CreateGroupResponse;
import com.vdotok.japp.network.models.GetAllUsersResponseModel;
import com.vdotok.japp.network.repos.UsersListsRepository;

import javax.inject.Inject;


/**
 * Created By: VdoTok
 * Date & Time: On 25/05/2022 At 3:27 PM in 2022
 */
public class UserListingViewModel extends BaseViewModel {

    @Inject
    UsersListsRepository usersListsRepository;

    @Inject
    public UserListingViewModel() {
    }

    public MutableLiveData<GetAllUsersResponseModel> getAllUsers(String authToken) {
        return usersListsRepository.getAllUsers(this, authToken);
    }

    public MutableLiveData<CreateGroupResponse> createGroup(String authToken, CreateGroupModel createGroupModel) {
        return usersListsRepository.createGroup(this, authToken, createGroupModel);
    }
}
