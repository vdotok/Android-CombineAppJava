package com.vdotok.japp.chat.dialogs;

import com.vdotok.japp.chat.enums.FileSelectionEnum;

public interface OnAttachmentClickListener {
    void onClick(FileSelectionEnum fileSelectionEnum);
    void onDocClick(FileSelectionEnum fileSelectionEnum);
}
