package de.leonlatsch.olivia.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.dto.ProfilePicDTO;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import de.leonlatsch.olivia.util.AndroidUtils;
import de.leonlatsch.olivia.util.ImageUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePicActivity extends AppCompatActivity {

    private UserService userService;
    private ImageView imageView;
    private View progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic);
        Toolbar toolbar = findViewById(R.id.profile_pic_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userService = RestServiceFactory.getUserService();

        TextView title = toolbar.findViewById(R.id.profile_pic_toolbar_text);
        imageView = findViewById(R.id.profile_pic_image_view);
        progressOverlay = findViewById(R.id.progressOverlay);

        title.setText(createTitle((String) getIntent().getExtras().get(Values.INTENT_KEY_PROFILE_PIC_USERNAME)));
        int uid = (int) getIntent().getExtras().get(Values.INTENT_KEY_PROFILE_PIC_UID);

        loadProfilePic(uid);
    }

    private void loadProfilePic(int uid) {
        isLoading(true);
        Call<ProfilePicDTO> call = userService.loadProfilePic(uid);
        call.enqueue(new Callback<ProfilePicDTO>() {
            @Override
            public void onResponse(Call<ProfilePicDTO> call, Response<ProfilePicDTO> response) {
                if (response.isSuccessful()) {
                    String profilePic = response.body().getProfilePic();
                    if (profilePic != null) {
                        imageView.setImageBitmap(ImageUtil.createBitmap(profilePic));
                    }
                }
                isLoading(false);
            }

            @Override
            public void onFailure(Call<ProfilePicDTO> call, Throwable t) {
                isLoading(false);
                showDialog(getString(R.string.error), getString(R.string.error_no_internet));
            }
        });
    }

    private String createTitle(String username) {
        if (username == null) {
            return null;
        }

        String base = "${username}'s Profile Picture";
        base = base.replace("${username}", username);
        return base;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void isLoading(boolean loading) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f);
        } else {
            AndroidUtils.animateView(progressOverlay, View.GONE, 0.4f);
        }
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
