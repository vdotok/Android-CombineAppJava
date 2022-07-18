package com.vdotok.japp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.databinding.ObservableBoolean;

import com.google.android.material.snackbar.Snackbar;
import com.vdotok.japp.userlisting.adapter.SelectUserContactAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;


public final class viewExtension {
    StringExtension stringExtension = new StringExtension();

    public void showSnackBar(View view, String msg) {
        if (msg != null)
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @SuppressLint("NewApi")
    public final void hideKeyboard(@NotNull Activity activity) {
        View focusedView = activity.getCurrentFocus();
        if (focusedView instanceof EditText) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }


    public final boolean checkEmail(@NotNull EditText editText, @NotNull String email, boolean showErrorMsg) {
        boolean value = false;
        if (!TextUtils.isEmpty((CharSequence) email) && Patterns.EMAIL_ADDRESS.matcher((CharSequence) email).matches()) {
            value = true;
        } else {
            if (showErrorMsg) {
                value = false;
            }


        }

        return value;
    }

    public final boolean checkUserName(@NotNull EditText editText, @NotNull String username, boolean showErrorMsg) {
        if (!stringExtension.containsNonAlphaNumericName(username) && username.length() >= 4 && username.length() <= 20) {
            if (username.length() != 0 && !TextUtils.isDigitsOnly((CharSequence) username)) {
                showErrorMsg = true;
            }
        } else {
            if (showErrorMsg) {
                showErrorMsg = false;
            }

        }

        return showErrorMsg;
    }

    public final boolean checkPassword(@NotNull EditText editText, @NotNull String password, boolean showErrorMsg) {
        if (!stringExtension.containsNonAlphaNumeric(password) && password.length() >= 8) {
            if (password.length() != 0) {
                showErrorMsg = true;
            }
        } else {
            if (showErrorMsg) {
                showErrorMsg = false;
            }
        }
        return showErrorMsg;
    }

    public final boolean checkValidation(@NotNull EditText editText, @NotNull String input) {
        boolean value = false;
        if (input.contains("@") && input.matches("^(.+)@(.+)$")) {
            value = checkEmail(editText, input, false);
        } else {
            value = checkUserName(editText, input, false);
        }
        return value;
    }


    public final void afterTextChanged(@NotNull EditText editText, ObservableBoolean validate, boolean name) {
        editText.addTextChangedListener((TextWatcher) (new TextWatcher() {
            public void beforeTextChanged(@Nullable CharSequence p0, int p1, int p2, int p3) {
            }

            public void onTextChanged(@Nullable CharSequence p0, int p1, int p2, int p3) {
            }

            public void afterTextChanged(@Nullable Editable editable) {
                if (name) {
                    validate.set(checkValidation(editText, editable.toString()));
                } else {
                    validate.set(checkPassword(editText, editable.toString(), false));
                }
            }
        }));
    }

    public final void searchAfterTextChanged(@NotNull EditText editText, SelectUserContactAdapter adapter) {
        editText.addTextChangedListener((TextWatcher) (new TextWatcher() {
            public void beforeTextChanged(@Nullable CharSequence p0, int p1, int p2, int p3) {
            }

            public void onTextChanged(@Nullable CharSequence p0, int p1, int p2, int p3) {
            }

            public void afterTextChanged(@Nullable Editable editable) {
                adapter.getFilter().filter(editable.toString());
            }
        }));
    }

    public final void groupAfterTextChanged(EditText editText, @NotNull ObservableBoolean groupName) {
        editText.addTextChangedListener((TextWatcher) new TextWatcher() {
            public void beforeTextChanged(@Nullable CharSequence p0, int p1, int p2, int p3) {
            }

            public void onTextChanged(@Nullable CharSequence p0, int p1, int p2, int p3) {
            }

            public void afterTextChanged(@Nullable Editable editable) {
                groupName.set(editable.toString().isEmpty());
            }
        });
    }

    @SuppressLint({"SimpleDateFormat"})
    @NotNull
    public final String timeCheck(long milli) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        return simpleDateFormat.format(milli);
    }

    public static final viewExtension INSTANCE = new viewExtension();

    public void  fadeOut(ImageView transparentView) {
        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(500);
        AnimationSet animation = new  AnimationSet(false);
        animation.addAnimation(fadeOut);
        transparentView.setAnimation(fadeOut);
        transparentView.setVisibility(View.GONE);
    }


}
