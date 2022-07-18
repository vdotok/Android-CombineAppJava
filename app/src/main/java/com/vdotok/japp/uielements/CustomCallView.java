package com.vdotok.japp.uielements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.vdotok.japp.R;
import com.vdotok.japp.databinding.CustomCallViewBinding;
import com.vdotok.streaming.views.ProxyVideoSink;

import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;

public class CustomCallView extends FrameLayout {
    private final EglBase rootEglBase = EglBase.create();
    String refID = null;
    String sessionID = null;
    private CustomCallViewBinding binding;
    private ConstraintLayout avatarLayout;
    private ImageView avatarImage;
    private ImageView muteIcon;
    private TextView avatarText;
    private SurfaceViewRenderer preview;
    private View borderView;


    @SuppressLint("ResourceType")
    public CustomCallView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.custom_call_view, this, true);
        setLocalVariables();
        binding.localGlSurfaceView.setMirror(false);
        binding.localGlSurfaceView.init(rootEglBase.getEglBaseContext(), null);
        binding.localGlSurfaceView.setZOrderOnTop(true);
        binding.localGlSurfaceView.setZOrderMediaOverlay(true);

        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.surfaceView, 0, 0);

        try {
            String ref = typeArray.getString(R.styleable.surfaceView_avatarUsername);
            this.binding.avatar.setImageDrawable(ResourcesCompat.getDrawable(getResources(), typeArray.getResourceId(R.styleable.surfaceView_avatar, 0), null));
            this.binding.avatarName.setText(ref);
            setBorderValues(typeArray);
            setMuteDrawable(typeArray);
        } finally {
            typeArray.recycle();
        }

    }

    private void setLocalVariables() {
        preview = binding.localGlSurfaceView;
    }

    private void setMuteDrawable(TypedArray typedArray) {
        Drawable muteDrawable = typedArray.getDrawable(R.styleable.surfaceView_muteIcon);
        if (muteDrawable != null) {
            binding.muteIcon.setImageDrawable(muteDrawable);
        }
    }

    private void setBorderValues(TypedArray typedArray) {
        GradientDrawable drawableGradient = (GradientDrawable) binding.borderView.getBackground();
        if (typedArray.getInt(R.styleable.surfaceView_borderStrokeWidth, 0) >= 12) {
            drawableGradient.setStroke(
                    typedArray.getInt(R.styleable.surfaceView_borderStrokeWidth, 0),
                    typedArray.getInt(R.styleable.surfaceView_borderStrokeColor, 0)
            );
        } else {
            drawableGradient.setStroke(
                    12,
                    typedArray.getInt(R.styleable.surfaceView_borderStrokeColor, 0)
            );
        }
//        show hide border for the view
        if (typedArray.getBoolean(R.styleable.surfaceView_showViewBorder, false))
            binding.borderView.setVisibility(VISIBLE);
        else
            binding.borderView.setVisibility(GONE);
    }

    public void release() {
        try {
            binding.localGlSurfaceView.release();
            binding.localGlSurfaceView.clearImage();
            rootEglBase.releaseSurface();
            rootEglBase.release();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public Boolean isAvatarShown() {
        return binding.avatarLayout.isShown();
    }

    public void showHideAvatar(Boolean showAvatar) {
        if (showAvatar) {
            binding.avatarLayout.setVisibility(VISIBLE);
        } else {
            binding.avatarLayout.setVisibility(GONE);
        }
    }

    public void showHideMuteIcon(Boolean showMuteIcon) {
        if (showMuteIcon) {
            binding.muteIcon.setVisibility(VISIBLE);
        } else {
            binding.muteIcon.setVisibility(GONE);
        }
    }

    public SurfaceViewRenderer getPreview() {
        return preview;
    }


    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public String getSessionID() {
        return sessionID;
    }


    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
