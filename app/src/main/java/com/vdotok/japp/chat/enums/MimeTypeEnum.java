package com.vdotok.japp.chat.enums;

public enum MimeTypeEnum {

    IMAGE("image"),
    AUDIO("audio"),
    VIDEO("video"),
    DOCTEXT("text"),
    DOC("application");

   public final String value;
   MimeTypeEnum(String value) {
        this.value = value;
   }
}