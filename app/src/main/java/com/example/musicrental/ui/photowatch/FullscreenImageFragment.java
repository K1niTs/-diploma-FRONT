package com.example.musicrental.ui.photowatch;

import android.app.Dialog;
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
import com.github.chrisbanes.photoview.PhotoView;


import com.bumptech.glide.Glide;
import com.example.musicrental.R;

public class FullscreenImageFragment extends DialogFragment {
    private static final String ARG_URL = "arg_url";

    public static FullscreenImageFragment newInstance(String imageUrl) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, imageUrl);
        FullscreenImageFragment f = new FullscreenImageFragment();
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = super.onCreateDialog(savedInstanceState);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dlg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fullscreen_image, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String url = requireArguments().getString(ARG_URL);
        PhotoView photo = view.findViewById(R.id.photoView);
        photo.setOnClickListener(v -> dismiss());
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.for_add)
                .error(R.drawable.for_add)
                .into(photo);
    }

    @Override public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null && d.getWindow() != null) {
            Window w = d.getWindow();
            w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            w.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
    }
}
