package com.example.musicrental.ui.catalog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicrental.R;
import com.example.musicrental.databinding.BottomsheetFiltersBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterSheet extends BottomSheetDialogFragment {

    public interface Listener {
        void onApply(FilterState state);
    }

    private final Listener    listener;
    private final FilterState current;

    public FilterSheet(FilterState state, Listener listener) {
        this.current  = state;
        this.listener = listener;
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(
                requireContext(),
                com.google.android.material.R.style.Theme_Design_BottomSheetDialog
        );

        BottomsheetFiltersBinding vb =
                BottomsheetFiltersBinding.inflate(getLayoutInflater());
        dialog.setContentView(vb.getRoot());

        vb.etCategory.setText(current.category);
        if (current.minPrice != null) {
            vb.etMin.setText(String.valueOf(current.minPrice));
        }
        if (current.maxPrice != null) {
            vb.etMax.setText(String.valueOf(current.maxPrice));
        }

        vb.btnApply.setOnClickListener(v -> {
            FilterState ns = new FilterState();
            ns.category = TextUtils.isEmpty(vb.etCategory.getText())
                    ? null
                    : vb.etCategory.getText().toString();

            ns.minPrice = TextUtils.isEmpty(vb.etMin.getText())
                    ? null
                    : Double.parseDouble(vb.etMin.getText().toString());

            ns.maxPrice = TextUtils.isEmpty(vb.etMax.getText())
                    ? null
                    : Double.parseDouble(vb.etMax.getText().toString());

            ns.orderBy = current.orderBy;

            listener.onApply(ns);
            dismiss();
        });

        return dialog;
    }
}
