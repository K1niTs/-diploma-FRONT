package com.example.musicrental.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.musicrental.databinding.ActivityMainBinding;
import com.example.musicrental.ui.catalog.InstrumentListFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding vb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());

        setSupportActionBar(vb.toolbar);

        if (savedInstanceState == null) {               // первый запуск
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(vb.fragmentContainer.getId(),
                            new InstrumentListFragment())
                    .commit();
        }

        handleDeepLink(getIntent());                    // запуск через ссылку
    }

    /* ---------- deep-link (возврат из браузера) ---------- */

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);                              // чтобы getIntent() вернул свежий
        handleDeepLink(intent);
    }

    /**
     * musicrental://paid?id=11 – посылаем событие «обнови список броней».
     */
    private void handleDeepLink(Intent intent) {
        Uri u = intent.getData();
        if (u == null) return;

        if ("paid".equals(u.getHost())) {
            // при желании можно взять id платежа:  String id = u.getQueryParameter("id");
            FragmentManager fm = getSupportFragmentManager();
            fm.setFragmentResult("reload_bookings", new Bundle());   // пустой bundle
        }
    }
}