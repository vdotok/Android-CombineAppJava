package com.vdotok.japp.di.modules;

import android.app.Application;

import com.vdotok.japp.manager.AppManager;
import com.vdotok.japp.network.ApiService;
import com.vdotok.japp.network.repos.AccountsRepository;
import com.vdotok.japp.network.repos.DashboardRepository;
import com.vdotok.japp.network.repos.UsersListsRepository;
import com.vdotok.japp.network.setup.NetworkConnectivity;
import com.vdotok.japp.prefs.PersistenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(includes = ViewModelModule.class)
public class AppModule {

    @Provides
    @Singleton
    public AppManager getAppManager(Application application) {
        AppManager appManager = new AppManager(application);
        return appManager;
    }

    @Provides
    @Singleton
    public PersistenceManager getPerfManager(Application application) {
        PersistenceManager perf = new PersistenceManager(application);
        return perf;
    }

    @Provides
    @Singleton
    NetworkConnectivity provideNetworkUtils(Application application) {
        NetworkConnectivity networkUtils = new NetworkConnectivity(application);
        return networkUtils;
    }

    @Provides
    @Singleton
    AccountsRepository provideAccountsRepository(Application application) {
        ApiService service = new ApiClientModule().provideRetrofitService();
        return new AccountsRepository(application, service);
    }

    @Provides
    @Singleton
    DashboardRepository provideDashboardRepository(Application application) {
        ApiService service = new ApiClientModule().provideRetrofitService();
        return new DashboardRepository(application, service);
    }

    @Provides
    @Singleton
    UsersListsRepository provideUsersListsRepository(Application application) {
        ApiService service = new ApiClientModule().provideRetrofitService();
        return new UsersListsRepository(application, service);
    }


}
