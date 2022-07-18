package com.vdotok.japp.dashboard.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.Presence;
import com.vdotok.japp.R;
import com.vdotok.japp.databinding.ItemGroupRowBinding;
import com.vdotok.japp.network.models.GroupModel;
import com.vdotok.japp.network.models.Participants;

import java.util.ArrayList;
import java.util.List;

public class AllGroupListAdapter extends RecyclerView.Adapter<AllGroupListAdapter.GroupUserViewHolder> {
    private final Boolean isGroup;
    public  List<GroupModel> dataModelList;
    public  List<GroupModel> filteredItems;
    private final OnItemClickListener listener ;
    public  List<Presence> presenceList  = new ArrayList();
    private Context context;
    private String username;


    public AllGroupListAdapter(Context context , String username,List<GroupModel> dataModelList, Boolean isGroupList, OnItemClickListener onItemClickListener) {
        this.dataModelList = dataModelList;
        this.filteredItems = dataModelList;
        this.context = context;
        this.username = username;
        this.isGroup = isGroupList;
        this.listener = onItemClickListener;

    }

    @NonNull
    @Override
    public GroupUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_group_row, parent, false);

        return new GroupUserViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull GroupUserViewHolder holder, int position) {
        GroupModel model = dataModelList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.removeUnReadCount(model);
                listener.onItemClick(model);
                holder.itemGroupRowBinding.setShowMessageCount(false);
                holder.itemGroupRowBinding.setShowMessage(false);
            }
        });
        listener.getMessageList(model);

        if (listener.getMessageList(model) != null && listener.getMessageList(model).size() > 0) {
            holder.itemGroupRowBinding.setShowLastMessage(true);
            Message message = getTypeMessage(model);
            if (message.getType().toString().equals("text")) {
                holder.itemGroupRowBinding.tvLastMessage.setText(getTypeMessage(model).getContent());
            } else {
                holder.itemGroupRowBinding.tvLastMessage.setText(R.string.attachment);
            }
        }else{
            holder.itemGroupRowBinding.setShowLastMessage(false);
        }

        if (listener.getUnreadCount(model) > 0 ) {
            holder.itemGroupRowBinding.setShowLastMessage(false);
            holder.itemGroupRowBinding.setShowMessageCount(true);
            holder.itemGroupRowBinding.setShowMessage(true);
            holder.itemGroupRowBinding.imgCount.setText(String.valueOf(listener.getUnreadCount(model)));
        } else {
            holder.itemGroupRowBinding.setShowMessageCount(false);
            holder.itemGroupRowBinding.setShowMessage(false);
        }

        if (model.getAutoCreated() == 1) {
            holder.itemGroupRowBinding.tvStatus.setText(getOneToOneStatus(model,holder.itemGroupRowBinding));

        } else {
            holder.itemGroupRowBinding.tvStatus.setText(getOneToManyStatus(model,holder.itemGroupRowBinding));
        }


        if (model != null){
            if (model.getAutoCreated() == 1) {
                for (Participants name :model.getParticipants()){
                    if (!name.getFullName().equals(username)) {
                        holder.itemGroupRowBinding.groupTitle.setText(name.getFullName());
                    }
                }
            } else {
                holder.itemGroupRowBinding.groupTitle.setText(model.getGroupTitle());
            }
        }



    }

    private Message getTypeMessage(GroupModel model) {
        return listener.getMessageList(model).get(listener.getMessageList(model).size() - 1);
    }

    private CharSequence getOneToOneStatus(GroupModel model, ItemGroupRowBinding itemGroupRowBinding) {

        String status = context.getString(R.string.offline);
        for (Participants participantList :model.getParticipants()){
            if (presenceList != null){
                for (Presence presence : presenceList){
                    if (presence.isOnline() == 0 && presence.getUsername().equals(participantList.getRefID())){
                        status = context.getString(R.string.online);
                    }
                }
            }
        }
        if (status.equals(context.getString(R.string.online))) {
            itemGroupRowBinding.setStatus(true);
        } else {
            itemGroupRowBinding.setStatus(false);
        }

        return status;
    }

    private CharSequence getOneToManyStatus(GroupModel model, ItemGroupRowBinding itemGroupRowBinding) {
        List<String> tempList = new ArrayList<>();
        String status ;
        int size = model.getParticipants().size();
        for (Participants participantList : model.getParticipants()) {
            if (presenceList != null) {
                for (Presence presence : presenceList) {
                    if (presence.isOnline() == 0 && presence.getUsername().equals(participantList.getRefID()) && !tempList.contains(presence.getUsername())) {
                        tempList.add(presence.getUsername());
                    }
                }
            }
        }
        if (tempList.size() > 0) {
            status = tempList.size() + "/" + size + " " + context.getResources().getString(R.string.online);
            itemGroupRowBinding.setStatus(true);
        } else {
            status = tempList.size() + "/" + size + " " + context.getResources().getString(R.string.offline);
            itemGroupRowBinding.setStatus(false);
        }

        return  status;
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    static class GroupUserViewHolder extends RecyclerView.ViewHolder {
        public ItemGroupRowBinding itemGroupRowBinding;

        public GroupUserViewHolder(@NonNull ItemGroupRowBinding itemGroupRowBinding) {
            super(itemGroupRowBinding.getRoot());
            this.itemGroupRowBinding = itemGroupRowBinding;
        }

    }

    public void  updateData(List<GroupModel> userModelList) {
        dataModelList.clear();
        dataModelList.addAll(userModelList);
        filteredItems.clear();
        filteredItems.addAll(userModelList);
        notifyDataSetChanged();
    }

    public void updatePresenceData(ArrayList<Presence> listPresence) {
        presenceList.clear();
        presenceList.addAll(listPresence);
        notifyItemRangeChanged(0, dataModelList.size());
    }

    public interface OnItemClickListener {
        void onItemClick(GroupModel groupModel);
        int getUnreadCount(GroupModel groupModel);
        ArrayList<Message> getMessageList(GroupModel groupModel);
        void removeUnReadCount(GroupModel groupModel);

    }
}
