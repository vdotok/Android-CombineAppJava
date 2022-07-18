package com.vdotok.japp.di.modules;


import com.vdotok.japp.account.AccountActivity;
import com.vdotok.japp.calling.CallingActivity;
import com.vdotok.japp.chat.ChatActivity;
import com.vdotok.japp.dashboard.DashboardActivity;
import com.vdotok.japp.splash.SplashActivity;
import com.vdotok.japp.userlisting.UserListingActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    public abstract SplashActivity splashActivity();

    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    public abstract AccountActivity accountActivity();

    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    public abstract DashboardActivity dashboardActivity();

    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    public abstract UserListingActivity userListingActivity();

    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    public abstract ChatActivity chatActivity();

    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    public abstract CallingActivity callingActivity();

}