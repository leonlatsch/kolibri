package de.leonlatsch.olivia.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.esafirm.imagepicker.features.ImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.leonlatsch.olivia.R;

public class AndroidUtils {

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

    public static CropImage.ActivityBuilder createImageCropper(String title) {
        return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setAllowFlipping(true)
                .setAllowRotation(true)
                .setCropMenuCropButtonTitle(title);
    }
}
