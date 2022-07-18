package com.vdotok.japp.di.modules;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vdotok.japp.account.viewmodel.AccountViewModel;
import com.vdotok.japp.calling.viewmodel.CallingViewModel;
import com.vdotok.japp.chat.viewmodel.ChatViewModel;
import com.vdotok.japp.dashboard.viewmodel.DashboardViewModel;
import com.vdotok.japp.splash.viewmodel.SplashViewModel;
import com.vdotok.japp.userlisting.viewmodel.UserListingViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;


@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindsViewModelFactory(ViewModelFactory viewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel.class)
    abstract ViewModel bindSplashViewModel(SplashViewModel splashViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel.class)
    abstract ViewModel bindAccountViewModel(AccountViewModel accountViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel.class)
    abstract ViewModel bindDashboardViewModel(DashboardViewModel dashboardViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserListingViewModel.class)
    abstract ViewModel bindUserListingViewModel(UserListingViewModel userListingViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel.class)
    abstract ViewModel bindChatViewModel(ChatViewModel chatViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CallingViewModel.class)
    abstract ViewModel bindCallingViewModel(CallingViewModel callingViewModel);

}