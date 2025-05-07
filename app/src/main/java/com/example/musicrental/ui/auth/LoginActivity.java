package com.example.musicrental.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicrental.data.AuthRequest;
import com.example.musicrental.data.AuthResponse;
import com.example.musicrental.databinding.ActivityLoginBinding;
import com.example.musicrental.network.ApiClient;
import com.example.musicrental.network.AuthApi;
import com.example.musicrental.ui.main.MainActivity;
import com.example.musicrental.util.Prefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding vb;
    private final AuthApi api = ApiClient.get().create(AuthApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Prefs.init(getApplicationContext());

        vb = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());

        vb.btnLogin.setOnClickListener(v -> login());
        vb.btnRegister.setOnClickListener(v -> register());
    }


    private void register() {
        String email = vb.etEmail.getText().toString().trim();
        String pass  = vb.etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email и пароль обязательны",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AuthRequest req = new AuthRequest(email, pass, "User");
        api.register(req).enqueue(authCallback(email));
    }

    private void login() {
        String email = vb.etEmail.getText().toString().trim();
        String pass  = vb.etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email и пароль обязательны",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AuthRequest req = new AuthRequest(email, pass, null);
        api.login(req).enqueue(authCallback(email));
    }


    private Callback<AuthResponse> authCallback(String email) {
        return new Callback<AuthResponse>() {

            @Override
            public void onResponse(Call<AuthResponse> call,
                                   Response<AuthResponse> resp) {

                if (resp.isSuccessful() && resp.body() != null) {

                    Prefs.get().setUserId(resp.body().id);
                    Prefs.get().setEmail(email);

                    startActivity(
                            new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this,
                            "Ошибка авторизации: " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };
    }
}
