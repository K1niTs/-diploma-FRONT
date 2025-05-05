package com.example.musicrental.network;

import com.example.musicrental.data.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/register")
    Call<AuthResponse> register(@Body AuthRequest req);

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest req);
}
