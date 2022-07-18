package com.vdotok.japp.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vdotok.connect.models.Connection;
import com.vdotok.japp.network.models.GroupModel;
import com.vdotok.japp.network.models.LoginResponse;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class PersistenceManager {

    Context context;
    SharedPreferences preference;


    @Inject
    public PersistenceManager(Context context) {
        this.context = context;
        this.preference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public LoginResponse getLoggedInUser() {
        Gson gson = new Gson();
        LoginResponse account = gson.fromJson(preference.getString(Keys.LOGIN_RESPONSE, null), LoginResponse.class);
        return account;
    }

    public void saveLoggedInUser(LoginResponse account) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(account);
        preference.edit().putString(Keys.LOGIN_RESPONSE, jsonStr).apply();
    }

    public Connection getConnectionObject() {
        Gson gson = new Gson();
        Connection account = gson.fromJson(preference.getString(Keys.CONNECTION_CLASS, null), Connection.class);
        return account;
    }

    public void saveConnectionObject(Connection account) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(account);
        preference.edit().putString(Keys.CONNECTION_CLASS, jsonStr).apply();
    }

    public List<GroupModel> getGroupList() {
        Gson gson = new Gson();
        String json = preference.getString(Keys.GROUP_LIST, null);
        Type type = new TypeToken<List<GroupModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void saveGroupList(List<GroupModel> list) {
        SharedPreferences.Editor editor = preference.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(Keys.GROUP_LIST, json);
        editor.apply();

    }

    public void clearApplicationPrefs() {
        preference.edit().clear().apply();
    }

    static class Keys {
        static String LOGIN_RESPONSE = "Login_Response";
        static String CONNECTION_CLASS = "Connection_Class";
        static String GROUP_LIST = "GROUP_LISTING";
    }

}
