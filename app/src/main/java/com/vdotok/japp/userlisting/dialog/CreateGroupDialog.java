package com.vdotok.japp.userlisting.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.DialogFragment;

import com.vdotok.japp.R;
import com.vdotok.japp.databinding.FragmentCreateGroupDialogBinding;
import com.vdotok.japp.utils.viewExtension;


public class CreateGroupDialog extends DialogFragment {
    public static String TAG = "CREATE_GROUP_DIALOG";
    private FragmentCreateGroupDialogBinding binding;
    private CreateGroupDialogInterface createGroupDialogInterface;
    public ObservableBoolean isGroupNameEmpty = new ObservableBoolean(true);

    public CreateGroupDialog(CreateGroupDialogInterface createGroupDialogInterface) {
        this.createGroupDialogInterface = createGroupDialogInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        binding = FragmentCreateGroupDialogBinding.inflate(inflater, container, false);
        setBinding();
        setClickListeners();
        return binding.getRoot();
    }

    private void setBinding() {
        binding.setIsGroupNameEmpty(isGroupNameEmpty);
        viewExtension.INSTANCE.groupAfterTextChanged(binding.edtGroupName, isGroupNameEmpty);
    }

    private void setClickListeners() {
        binding.imgClose.setOnClickListener(view -> dismiss());
        binding.btnDone.setOnClickListener(view -> {
            if (binding.edtGroupName.getText() != null) {
                createGroupDialogInterface.onDoneClick(binding.edtGroupName.getText().toString());
                dismiss();
            } else {
                viewExtension.INSTANCE.showSnackBar(binding.getRoot(),getString(R.string.group_name_empty));
            }
        });
    }

    public interface CreateGroupDialogInterface {
        void onDoneClick(String groupTitle);
    }


}