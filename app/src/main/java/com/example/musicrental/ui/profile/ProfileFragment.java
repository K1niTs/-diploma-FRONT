package com.example.musicrental.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.musicrental.databinding.FragmentProfileBinding;
import com.example.musicrental.ui.auth.LoginActivity;
import com.example.musicrental.ui.booking.BookingsFragment;
import com.example.musicrental.util.Prefs;
import com.google.android.material.switchmaterial.SwitchMaterial;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding vb;
    private SharedPreferences prefs;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vb = FragmentProfileBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences("app_settings", MODE_PRIVATE);

        vb.tvEmail.setText(Prefs.get().getEmail());

        boolean darkMode = prefs.getBoolean("dark_mode", false);
        vb.switchTheme.setChecked(darkMode);
        applyTheme(darkMode);

        vb.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            applyTheme(isChecked);
        });

        vb.btnBookings.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                ((ViewGroup) requireView().getParent()).getId(),
                                new BookingsFragment()
                        )
                        .addToBackStack(null)
                        .commit()
        );

        vb.btnLogout.setOnClickListener(v -> {
            Prefs.get().clear();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    private void applyTheme(boolean dark) {
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        vb = null;
    }
}
