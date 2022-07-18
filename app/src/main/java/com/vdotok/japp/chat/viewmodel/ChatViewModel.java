package com.vdotok.japp.chat.viewmodel;


import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.databinding.ObservableField;

import com.vdotok.connect.models.HeaderModel;
import com.vdotok.connect.models.Message;
import com.vdotok.japp.base.BaseViewModel;
import com.vdotok.japp.network.models.Participants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created By: VdoTok
 * Date & Time: On 25/05/2022 At 3:27 PM in 2022
 */
public class ChatViewModel extends BaseViewModel {
    public ObservableField<String> groupTitle  = new ObservableField<>();
    public List<String> usersList = new ArrayList();
    @Inject
    public ChatViewModel() {
    }

    /**
     * method to get other username
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getUserName(Message model) {
        String name  = null;
        for (Participants participants : getAppManager().groupModel.getParticipants()){
            if (participants.getRefID().equals(model.getFrom())){
                name = participants.getFullName();
            }
        }
        return name;
    }


    /**
     * this method is used to get the group name respective of the group created as single/multi users
     */
    public void getGroupName() {
        if (getAppManager().groupModel.getAutoCreated() == 1) {
            for (Participants userName : getAppManager().groupModel.getParticipants()){
                if (!userName.getFullName().equals(getAppManager().getUserData().getFullName())) {
                    groupTitle.set(userName.getFullName());
                }
            }
        } else {
            groupTitle.set(getAppManager().groupModel.getGroupTitle());
        }
    }

    public HeaderModel getDummyHeader(int fileType) {
        return new HeaderModel("", 0, 0, "", "", "", getAppManager().getUserData().getRefId().toString(), fileType, 0);
    }

   public  boolean getGroupStatus() {
        return getAppManager().groupModel.getAutoCreated() == 1;
    }
    /**
     * this method is used to get get typing status according to the users in a respective group
     */
    public  String getNameOfUsers(Message message) {
        // users list using to get participant messages typing status  other then login user
        if (!usersList.contains(message.getFrom())) {
             usersList.add(message.getFrom());
        }
        String resullt;
       switch (usersList.size()) {
           case 0: resullt = "";
           break;
           case 1 : resullt =  usersList.get(0) +"is typing...";
           break;
           case 2 : resullt =  usersList.get(usersList.size() -1)+"and"+ usersList.get(usersList.size() - 2) +" are typing...";
           break;
           default: resullt =  usersList.get(usersList.size() -1)+","+usersList.get(usersList.size() -2)+"and"+ (usersList.size() -2) +"others are typing...";
           break;
        }
        return resullt;
    }
}
