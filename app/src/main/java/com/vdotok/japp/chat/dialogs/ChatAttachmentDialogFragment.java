package com.vdotok.japp.chat.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vdotok.japp.chat.enums.FileSelectionEnum;
import com.vdotok.japp.databinding.FragmentChatAttachmentDialogBinding;

public class ChatAttachmentDialogFragment extends DialogFragment {
    private FragmentChatAttachmentDialogBinding binding;
    private final OnAttachmentClickListener listener ;
    public static String TAG = "CHAT_ATTACHMENT_DIALOG";

    public ChatAttachmentDialogFragment(OnAttachmentClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        binding = FragmentChatAttachmentDialogBinding.inflate(inflater, container, false);
        setClickListeners();
        return binding.getRoot();
    }

    private void setClickListeners() {
        binding.closeDialog.setOnClickListener(view ->  dismiss());

        binding.audioOption.setOnClickListener(view ->  {listener.onClick(FileSelectionEnum.AUDIO);
        dismiss();}
        );

        binding.albumOption.setOnClickListener(view ->  {listener.onClick(FileSelectionEnum.VIDEO);
            dismiss();}
        );

        binding.locationOption.setOnClickListener(view ->  { Toast.makeText(getContext(), "In Progress !!", Toast.LENGTH_LONG).show();
            dismiss();}
        );

        binding.contactOption.setOnClickListener(view ->  {
            Toast.makeText(getContext(), "In Progress !!", Toast.LENGTH_LONG).show();
            dismiss();}
        );

        binding.cameraOption.setOnClickListener(view ->  {listener.onDocClick(FileSelectionEnum.CAM);
            dismiss();}
        );
        binding.fileOption.setOnClickListener(view ->  {listener.onDocClick(FileSelectionEnum.DOC);
            dismiss();}
        );
    }
}

