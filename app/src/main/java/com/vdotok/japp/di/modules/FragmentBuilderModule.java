package com.vdotok.japp.di.modules;


import com.vdotok.japp.account.fragments.LoginFragment;
import com.vdotok.japp.account.fragments.SignUpFragment;
import com.vdotok.japp.calling.fragments.ConnectedCallFragment;
import com.vdotok.japp.calling.fragments.DialCallFragment;
import com.vdotok.japp.chat.fragment.ChatFragment;
import com.vdotok.japp.dashboard.fragment.GroupListingFragment;
import com.vdotok.japp.userlisting.fragments.AllUserFragment;
import com.vdotok.japp.userlisting.fragments.GroupCreationFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract LoginFragment contributeLoginFragment();

    @ContributesAndroidInjector
    abstract SignUpFragment contributeSignUpFragment();

    @ContributesAndroidInjector
    abstract GroupListingFragment contributeGroupListingFragment();

    @ContributesAndroidInjector
    abstract AllUserFragment contributeAllUserFragment();

    @ContributesAndroidInjector
    abstract GroupCreationFragment contributeGroupCreationFragment();

    @ContributesAndroidInjector
    abstract ChatFragment contributeChatFragment();

    @ContributesAndroidInjector
    abstract ConnectedCallFragment contributeConnectedCallFragment();

    @ContributesAndroidInjector
    abstract DialCallFragment contributeDialCallFragment();


}