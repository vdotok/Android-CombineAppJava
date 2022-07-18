package com.vdotok.japp.network;

import com.vdotok.japp.network.models.AllGroupsResponse;
import com.vdotok.japp.network.models.CheckUserModel;
import com.vdotok.japp.network.models.CreateGroupModel;
import com.vdotok.japp.network.models.CreateGroupResponse;
import com.vdotok.japp.network.models.GetAllUsersResponseModel;
import com.vdotok.japp.network.models.LoginResponse;
import com.vdotok.japp.network.models.LoginUserModel;
import com.vdotok.japp.network.models.SignUpModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/API/v0/Login")
    Call<LoginResponse> loginUser(@Body LoginUserModel model);

    @POST("/API/v0/SignUp")
    Call<LoginResponse> signUp(@Body SignUpModel model);

    @POST("/API/v0/CheckUsername")
    Call<LoginResponse> checkUserName(@Body CheckUserModel model);

    @POST("/API/v0/CheckEmail")
    Call<LoginResponse> checkEmail(@Body CheckUserModel model);

    @POST("/API/v0/AllUsers")
    Call<GetAllUsersResponseModel> getAllUsers(@Header("Authorization") String auth_token);

    @POST("/API/v0/AllGroups")
    Call<AllGroupsResponse> getAllGroups(@Header("Authorization") String auth_token);

    @POST("/API/v0/CreateGroup")
    Call<CreateGroupResponse> createGroup(@Header("Authorization") String auth_token, @Body CreateGroupModel model);

}
