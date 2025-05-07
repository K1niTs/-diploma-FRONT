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

public final class ApiClient {

    private static final String BASE_URL = "http://192.168.0.41:8081/";
    private static Retrofit retrofit;

    private ApiClient() {}

    public static Retrofit get() {
        if (retrofit != null) return retrofit;

        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request orig = chain.request();
                    if (orig.url().encodedPath().startsWith("/auth")) {
                        return chain.proceed(orig);
                    }
                    long uid = Prefs.get().getUserId();
                    Request req = orig.newBuilder()
                            .addHeader("X-User-Id", String.valueOf(uid))
                            .build();
                    return chain.proceed(req);
                })
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter())
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

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ok)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        return retrofit;
    }
}
