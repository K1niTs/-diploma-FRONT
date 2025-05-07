package com.example.musicrental.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {


    private static Prefs I;
    private final SharedPreferences sp;

    private Prefs(Context ctx) {
        sp = ctx.getApplicationContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }

    public static void init(Context ctx) {
        if (I == null) I = new Prefs(ctx);
    }
    public static Prefs get() { return I; }


    private static final String K_UID = "uid";

    public void   setUserId(long id) { sp.edit().putLong(K_UID, id).apply(); }
    public long   getUserId()        { return sp.getLong(K_UID, -1); }


    private static final String K_EMAIL = "email";

    public void   setEmail(String e) { sp.edit().putString(K_EMAIL, e).apply(); }
    public String getEmail()         { return sp.getString(K_EMAIL, ""); }


    public void clear() { sp.edit().clear().apply(); }
}
