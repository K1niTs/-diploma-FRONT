package com.example.musicrental.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicrental.R;
import com.example.musicrental.databinding.ActivityWelcomeBinding;
import com.example.musicrental.ui.auth.LoginActivity;


public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());

        vb.btnEnter.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
