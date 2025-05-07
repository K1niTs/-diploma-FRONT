package com.example.musicrental.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.musicrental.R;
import com.example.musicrental.databinding.ActivityMainBinding;
import com.example.musicrental.ui.catalog.InstrumentListFragment;
import com.example.musicrental.ui.chat.ChatListFragment;
import com.example.musicrental.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding vb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());

        setSupportActionBar(vb.toolbar);

        vb.bottomNav.setOnItemSelectedListener(item -> {
            Fragment dest = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                dest = new InstrumentListFragment();
            } else if (id == R.id.nav_chats) {
                dest = new ChatListFragment();
            } else if (id == R.id.nav_profile) {
                dest = new ProfileFragment();
            }

            if (dest != null) {
                switchTo(dest);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            vb.bottomNav.setSelectedItemId(R.id.nav_home);
        }

        handleDeepLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    private void switchTo(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(vb.fragmentContainer.getId(), fragment)
                .commit();
    }

    private void handleDeepLink(Intent intent) {
        Uri u = intent.getData();
        if (u == null) return;

        if ("paid".equals(u.getHost())) {
            FragmentManager fm = getSupportFragmentManager();
            fm.setFragmentResult("reload_bookings", new Bundle());
        }
    }
}
