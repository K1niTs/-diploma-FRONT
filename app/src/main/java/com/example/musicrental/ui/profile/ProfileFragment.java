package com.example.musicrental.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.example.musicrental.databinding.FragmentProfileBinding;
import com.example.musicrental.ui.auth.LoginActivity;
import com.example.musicrental.ui.booking.BookingsFragment;
import com.example.musicrental.util.Prefs;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding vb;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle s){
        vb = FragmentProfileBinding.inflate(inf, container, false);
        return vb.getRoot();
    }

    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
        super.onViewCreated(v,s);

        vb.tvEmail.setText(Prefs.get().getEmail());

        vb.btnBookings.setOnClickListener(c ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(((ViewGroup) requireView().getParent()).getId(),
                                new BookingsFragment())
                        .addToBackStack(null)
                        .commit());

        vb.btnLogout.setOnClickListener(c -> {
            Prefs.get().clear();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }
}
