package com.vdotok.japp.di;

import android.app.Application;

import com.vdotok.japp.VdoTok;
import com.vdotok.japp.di.modules.ActivityBuilderModule;
import com.vdotok.japp.di.modules.ApiClientModule;
import com.vdotok.japp.di.modules.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AppModule.class, ApiClientModule.class, AndroidInjectionModule.class,
        ActivityBuilderModule.class})
public interface NetComponent {
    void inject(VdoTok app);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        NetComponent build();
    }
}