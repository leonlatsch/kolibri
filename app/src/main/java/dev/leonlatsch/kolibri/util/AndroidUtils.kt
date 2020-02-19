package dev.leonlatsch.kolibri.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
object AndroidUtils {

    /**
     * Animate a view with a specific Visibility and a alpha value
     * Used for the loading animation overlay
     *
     * @param view
     * @param toVisibility
     * @param toAlpha
     */
    fun animateView(view: View, toVisibility: Int, toAlpha: Float) {
        val show = toVisibility == View.VISIBLE
        if (show) {
            view.setAlpha(0f)
        }
        view.setVisibility(View.VISIBLE)
        view.animate()
                .alpha(if (show) toAlpha else 0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.setVisibility(toVisibility)
                    }
                })
    }

    /**
     * Create a ImageCropper Activity with specific default values
     *
     * @param title
     * @return The CropImage Activity
     */
    fun createImageCropper(title: String): CropImage.ActivityBuilder {
        return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setAllowFlipping(true)
                .setAllowRotation(true)
                .setCropMenuCropButtonTitle(title)
    }
}
