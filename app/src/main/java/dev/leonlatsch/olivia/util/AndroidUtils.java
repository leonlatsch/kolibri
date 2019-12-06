package dev.leonlatsch.olivia.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AndroidUtils {

    /**
     * Animate a view with a specific Visibility and a alpha value
     * Used for the loading animation overlay
     *
     * @param view
     * @param toVisibility
     * @param toAlpha
     */
    public static void animateView(final View view, final int toVisibility, float toAlpha) {
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }

    /**
     * Create a ImageCropper Activity with specific default values
     *
     * @param title
     * @return The CropImage Activity
     */
    public static CropImage.ActivityBuilder createImageCropper(String title) {
        return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setAllowFlipping(true)
                .setAllowRotation(true)
                .setCropMenuCropButtonTitle(title);
    }
}
