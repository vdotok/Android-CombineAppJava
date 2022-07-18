package com.vdotok.japp.chat.enums;

public enum FileSelectionEnum {

    IMAGE("image/*"),
    AUDIO("audio/*"),
    VIDEO("video/*"),
    DOC("*/*"),
    CAM("");

   public final String value;
   FileSelectionEnum(String value) {
        this.value = value;
   }
}