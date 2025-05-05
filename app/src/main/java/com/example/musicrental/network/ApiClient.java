// app/src/main/java/com/example/musicrental/network/ApiClient.java
package com.example.musicrental.network;

import com.example.musicrental.util.Prefs;
import com.squareup.moshi.*;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/** Singleton-клиент Retrofit + Moshi. */
public final class ApiClient {

    private static final String BASE_URL = "http://192.168.0.41:8081/";
    private static Retrofit retrofit;

    private ApiClient() {}                           // ↯ нельзя создавать экземпляры

    /* ------------------------------------------------------------------ */
    public static Retrofit get() {
        if (retrofit != null) return retrofit;

        /* ---------- OkHttp ---------- */
        OkHttpClient ok = new OkHttpClient.Builder()
                // добавляем X-User-Id во все запросы, кроме /auth/*
                .addInterceptor(chain -> {
                    Request orig = chain.request();
                    if (orig.url().encodedPath().startsWith("/auth")) {
                        return chain.proceed(orig);
                    }
                    long uid = Prefs.get().getUserId();                // хранится в SharedPrefs
                    Request req = orig.newBuilder()
                            .addHeader("X-User-Id", String.valueOf(uid))
                            .build();
                    return chain.proceed(req);
                })
                // подробный лог HTTP-трафика (удалите на production)
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        /* ---------- Moshi ---------- */
        Moshi moshi = new Moshi.Builder()
                // java.util.Date → RFC-3339
                .add(Date.class, new Rfc3339DateJsonAdapter())
                // java.time.LocalDate → "yyyy-MM-dd"
                .add(LocalDate.class, new JsonAdapter<LocalDate>() {
                    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
                    @Override public LocalDate fromJson(JsonReader r) throws java.io.IOException {
                        return LocalDate.parse(r.nextString(), fmt);
                    }
                    @Override public void toJson(JsonWriter w, LocalDate v) throws java.io.IOException {
                        w.value(v.format(fmt));
                    }
                })
                .build();

        /* ---------- Retrofit ---------- */
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ok)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        return retrofit;
    }
}
