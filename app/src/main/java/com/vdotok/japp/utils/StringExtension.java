package com.vdotok.japp.utils;

import com.google.gson.Gson;
import com.vdotok.japp.models.CallNameModel;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;


public final class StringExtension {

    public static String setCallTitleCustomObject(String calleName, String groupName, String autoCreated) {
        return new Gson().toJson(new CallNameModel(calleName, groupName, autoCreated), CallNameModel.class);
    }

    public static CallNameModel getCallTitle(String customObject) {
        return new Gson().fromJson(customObject, CallNameModel.class);
    }

    public final boolean containsNonAlphaNumeric(@NotNull String pass) {
        Pattern p = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-¥¢£ø]");
        return p.matcher((CharSequence) pass).find();
    }

    public final boolean containsNonAlphaNumericName(@NotNull String email) {
        Pattern p = Pattern.compile("[!@#$%&*()+=|<>?{}\\[\\]~-¥¢£ø]");
        return p.matcher((CharSequence) email).find();
    }

}
