package dev.leonlatsch.olivia.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

import dev.leonlatsch.olivia.R;

/**
 * A util class to load and convert images, drawables and bitmaps
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class ImageUtil {

    /**
     * Generate a Bitmap from a base64 String
     *
     * @param base64
     * @return The generated Bitmap
     */
    public static Bitmap createBitmap(String base64) {
        if (base64 == null) {
            return null;
        }
        byte[] bytes = Base64.toBytes(base64);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Generate a base64 String from a Bitmap
     *
     * @param bitmap
     * @return The generated base64 String
     */
    public static String createBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] bytes = bos.toByteArray();
        return Base64.toBase64(bytes);
    }

    /**
     * Load the default profile picture thumbnail as a Drawable
     *
     * @param context
     * @return The default profile picture thumbnail
     */
    public static Drawable getDefaultProfilePicTn(Context context) {
        return context.getResources().getDrawable(R.drawable.default_profile_pic_tn, context.getTheme());
    }

    /**
     * Load the default profile picture as a Drawable
     *
     * @param context
     * @return The default profile picture
     */
    public static Drawable getDefaultProfilePic(Context context) {
        return context.getResources().getDrawable(R.drawable.default_profile_pic, context.getTheme());
    }
}
