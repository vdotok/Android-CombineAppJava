package com.vdotok.japp.chat.dialogs;

import static com.vdotok.japp.constants.ApplicationConstants.External_STORAGE_PERMISSION_REQUEST;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vdotok.connect.models.HeaderModel;
import com.vdotok.japp.R;
import com.vdotok.japp.dashboard.interfaces.OnStoragePermissionListener;
import com.vdotok.japp.databinding.FragmentStoragePermissionDialogBinding;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class StoragePermissionDialogFragment extends DialogFragment implements EasyPermissions.PermissionCallbacks {
    private FragmentStoragePermissionDialogBinding binding;
    public static String TAG = "STORAGE_PERMISSION_DIALOG";

    OnStoragePermissionListener listener;
    String msgId;
    HeaderModel headerModel;
    byte[] byteArray;

    public StoragePermissionDialogFragment(String msgId,
            HeaderModel headerModel,
            byte[] byteArray,OnStoragePermissionListener onStoragePermissionListener) {
        this.listener = onStoragePermissionListener;
        this.msgId = msgId;
        this.headerModel = headerModel;
        this.byteArray = byteArray;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        binding = FragmentStoragePermissionDialogBinding.inflate(inflater, container, false);
        setClickListeners();
        return binding.getRoot();
    }

    private void setClickListeners() {
        binding.imgClose.setOnClickListener(view -> dismiss());
        binding.btnGrantPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                EasyPermissions.requestPermissions(requireActivity(), "Our App Requires a permission to access your storage", External_STORAGE_PERMISSION_REQUEST, permission);
                try {
                    listener.saveFile(msgId,headerModel,byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        dismiss();
        listener.sendTextMessage(requireContext().getResources().getString(R.string.msg_storage_permission_denied));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, requireActivity());
    }
}