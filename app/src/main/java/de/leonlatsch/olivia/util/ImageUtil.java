package de.leonlatsch.olivia.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageUtil {

    public static Bitmap createBitmap(String base64) {
        if (base64 == null) {
            return null;
        }
        byte[] bytes = Base64.toBytes(base64);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String createBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] bytes = bos.toByteArray();
        return Base64.toBase64(bytes);
    }
}
