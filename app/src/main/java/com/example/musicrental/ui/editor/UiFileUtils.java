package com.example.musicrental.ui.editor;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

class UiFileUtils {

    static String getPath(Context ctx, Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        try (Cursor c = ctx.getContentResolver().query(uri, proj, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int col = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return c.getString(col);
            }
        }
        return null;
    }
}
