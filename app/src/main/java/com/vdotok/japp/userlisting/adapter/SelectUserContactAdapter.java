package com.vdotok.japp.userlisting.adapter;

import static com.vdotok.japp.constants.ApplicationConstants.MAX_PARTICIPANTS;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vdotok.japp.R;
import com.vdotok.japp.databinding.ItemRowBinding;
import com.vdotok.japp.network.models.UserModel;
import com.vdotok.japp.utils.viewExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SelectUserContactAdapter extends RecyclerView.Adapter<SelectUserContactAdapter.SelectUserContactViewHolder> implements Filterable {
    private final Boolean isGroup;
    private final ArrayList<UserModel> filteredList;
    private final OnItemClickListener listener;
    public ArrayList<UserModel> dataModelList;
    private boolean selection = false;
    private Activity activity;


    public SelectUserContactAdapter(Activity activity, ArrayList<UserModel> dataModelList, Boolean isGroupList, OnItemClickListener onItemClickListener) {
        this.dataModelList = dataModelList;
        this.filteredList = dataModelList;
        this.isGroup = isGroupList;
        this.listener = onItemClickListener;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SelectUserContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_row, parent, false);
        return new SelectUserContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectUserContactViewHolder holder, int position) {
        if (isGroup) {
            setCreateGroupUserListing(holder, position, dataModelList);
        } else {
            setUserListingUI(holder, position, dataModelList);
        }


    }

    private void setUserListingUI(SelectUserContactViewHolder holder, int position, List<UserModel> dataModelList) {
        UserModel model = dataModelList.get(position);
        holder.itemRowBinding.setIsGroupUsersList(isGroup);
        holder.itemRowBinding.groupTitle.setText(model.getFullName());
        holder.itemRowBinding.chatIcon.setOnClickListener(view -> listener.onChatIconClick(position));

        holder.itemRowBinding.callIcon.setOnClickListener(view -> listener.onCallIconClick(position));

        holder.itemRowBinding.videoIcon.setOnClickListener(view -> listener.onVideoIconClick(position));
    }

    private void setCreateGroupUserListing(SelectUserContactViewHolder holder, int position, List<UserModel> dataModelList) {
        UserModel model = dataModelList.get(position);
        holder.itemRowBinding.groupTitle.setText(model.getFullName());
        holder.itemRowBinding.setIsGroupUsersList(isGroup);
        holder.itemView.getRootView().setOnClickListener(view -> {
            if (checkItemExists(model) || getSelectedUsers().size() < MAX_PARTICIPANTS) {
                listener.onItemClick(position);
                selection = true;
            } else {
                viewExtension.INSTANCE.hideKeyboard(activity);
                viewExtension.INSTANCE.showSnackBar(view.getRootView(), "You can create groups with four participants only");
            }
        });
        if (model.getSelected()) {
            holder.itemRowBinding.imgUserSelected.setVisibility(View.VISIBLE);
        } else {
            selection = false;
            holder.itemRowBinding.imgUserSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    dataModelList = filteredList;
                } else {
//                    ArrayList<UserModel> filterList = new ArrayList<>();
                    ArrayList<UserModel> filterList = (ArrayList<UserModel>) filteredList.stream()
                            .filter(s -> s.getFullName().contains(charString.toLowerCase(Locale.getDefault())))
                            .collect(Collectors.toList());
//                    for (UserModel row : filteredList) {
//                        if (row.contains(charString.toLowerCase())) {
//                            filterList.add(row);
//                        }
//
//                        if (row.getFullName().split(" ").fi.lowercase(Locale.getDefault()).contains(charString.lowercase(Locale.getDefault())) == true
//                                || row.fullName?.split(" ")?.last()?.lowercase(Locale.getDefault())
//                                ?.contains(
//                                charString.lowercase(Locale.getDefault())
//                        ) == true
//                        ) {
//                            filteredList.add(row)
//                        }
//                    }
                    dataModelList = filterList;
                }
                Filter.FilterResults filterResults = new FilterResults();
                filterResults.values = dataModelList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataModelList = (ArrayList<UserModel>) filterResults.values;
                listener.searchResult(dataModelList.size());
                notifyDataSetChanged();
            }
        };
    }

    public UserModel getItem(int position) {
        return dataModelList.get(position);
    }

    private boolean checkItemExists(UserModel user) {
        return getSelectedUsers().contains(user);
    }

    public List<UserModel> getSelectedUsers() {
        List<UserModel> users = new ArrayList<>();
        for (UserModel user : filteredList) {
            if (user.getSelected())
                users.add(user);
        }
        return users;
    }

    public void updateData(List<UserModel> userModelList) {
        dataModelList.clear();
        dataModelList.addAll(userModelList);
        filteredList.clear();
        filteredList.addAll(userModelList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onChatIconClick(int position);

        void onCallIconClick(int position);

        void onVideoIconClick(int position);

        void searchResult(int position);
    }

    static class SelectUserContactViewHolder extends RecyclerView.ViewHolder {
        public ItemRowBinding itemRowBinding;

        public SelectUserContactViewHolder(@NonNull ItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }

    }
}
