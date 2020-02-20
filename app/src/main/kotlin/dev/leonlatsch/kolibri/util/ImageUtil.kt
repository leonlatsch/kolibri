package dev.leonlatsch.kolibri.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import dev.leonlatsch.kolibri.R
import java.io.ByteArrayOutputStream

/**
 * A util class to load and convert images, drawables and bitmaps
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
object ImageUtil {

    /**
     * Generate a Bitmap from a base64 String
     *
     * @param base64
     * @return The generated Bitmap
     */
    fun createBitmap(base64: String?): Bitmap? {
        if (base64 == null) {
            return null
        }
        val bytes = Base64.toBytes(base64)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * Generate a base64 String from a Bitmap
     *
     * @param bitmap
     * @return The generated base64 String
     */
    fun createBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) {
            return null
        }
        val bos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, bos) //TODO: Seperate this into its own function
        val bytes = bos.toByteArray()
        return Base64.toBase64(bytes)
    }

    /**
     * Load the default profile picture thumbnail as a Drawable
     *
     * @param context
     * @return The default profile picture thumbnail
     */
    fun getDefaultProfilePicTn(context: Context): Drawable {
        return context.getResources().getDrawable(R.drawable.default_profile_pic_tn, context.getTheme())
    }

    /**
     * Load the default profile picture as a Drawable
     *
     * @param context
     * @return The default profile picture
     */
    fun getDefaultProfilePic(context: Context): Drawable {
        return context.getResources().getDrawable(R.drawable.default_profile_pic, context.getTheme())
    }
}
