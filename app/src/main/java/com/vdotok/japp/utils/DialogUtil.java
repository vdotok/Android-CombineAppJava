package com.vdotok.japp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.fragment.app.FragmentActivity;

import com.vdotok.japp.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DialogUtil {

    public final void showPermissionsDeniedAlert(@Nullable FragmentActivity activity, @NotNull String message, @NotNull String buttonText, @NotNull DialogInterface.OnClickListener dialogListener) {
        if (activity != null) {

            AlertDialog alertDialog= (new AlertDialog.Builder((Context)activity)).setCancelable(false).setMessage((CharSequence)message).setPositiveButton((CharSequence)buttonText, dialogListener).create();
            alertDialog.show();
        }

    }

    public static final DialogUtil INSTANCE = new DialogUtil();

}
