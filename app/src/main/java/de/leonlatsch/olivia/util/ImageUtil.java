package de.leonlatsch.olivia.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageUtil {

    public static Bitmap createBitmap(String base64) {
        if (base64 == null) {
            return null;
        }
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
