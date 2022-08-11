package com.vdotok.japp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.fragment.app.FragmentActivity;

import com.vdotok.japp.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DialogUtil {

    public final void showDeleteGroupAlert(@Nullable FragmentActivity activity, @NotNull DialogInterface.OnClickListener dialogListener) {
        if (activity != null) {
            AlertDialog alertDialog = (new AlertDialog.Builder((Context)activity)).setMessage((CharSequence)activity.getString(R.string.delete_group_des)).setPositiveButton(R.string.delete_group, dialogListener).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener)null).create();
            alertDialog.show();
        }

    }

    public final void showPermissionsDeniedAlert(@Nullable FragmentActivity activity, @NotNull String message, @NotNull String buttonText, @NotNull DialogInterface.OnClickListener dialogListener) {
        if (activity != null) {

            AlertDialog alertDialog= (new AlertDialog.Builder((Context)activity)).setCancelable(false).setMessage((CharSequence)message).setPositiveButton((CharSequence)buttonText, dialogListener).create();
            alertDialog.show();
        }

    }

    public static final DialogUtil INSTANCE = new DialogUtil();

}
