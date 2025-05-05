// app/src/main/java/com/example/musicrental/util/Prefs.java
package com.example.musicrental.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Централизованное хранилище небольших настроек / данных авторизации. */
public final class Prefs {

    /* ---------- singleton ---------- */

    private static Prefs I;                     // instance
    private final SharedPreferences sp;

    private Prefs(Context ctx) {
        sp = ctx.getApplicationContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }

    /** Вызываем один раз (например, в Application или первой Activity). */
    public static void init(Context ctx) {
        if (I == null) I = new Prefs(ctx);
    }
    public static Prefs get() { return I; }

    /* ---------- userId ---------- */

    private static final String K_UID = "uid";

    public void   setUserId(long id) { sp.edit().putLong(K_UID, id).apply(); }
    public long   getUserId()        { return sp.getLong(K_UID, -1); }

    /* ---------- email ---------- */

    private static final String K_EMAIL = "email";

    public void   setEmail(String e) { sp.edit().putString(K_EMAIL, e).apply(); }
    public String getEmail()         { return sp.getString(K_EMAIL, ""); }

    /* ---------- reset ---------- */

    /** Полная очистка всех сохранённых данных (выход из аккаунта). */
    public void clear() { sp.edit().clear().apply(); }
}
