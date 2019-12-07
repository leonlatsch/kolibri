package dev.leonlatsch.olivia.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.constants.Values;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.rest.service.UserService;
import dev.leonlatsch.olivia.util.AndroidUtils;
import dev.leonlatsch.olivia.util.ImageUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity to show the full profile picture of a user.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class ProfilePicActivity extends AppCompatActivity {

    private UserInterface userInterface;

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

        userInterface = UserInterface.getInstance();
        userService = RestServiceFactory.getUserService();

        TextView title = toolbar.findViewById(R.id.profile_pic_toolbar_text);
        imageView = findViewById(R.id.profile_pic_image_view);
        progressOverlay = findViewById(R.id.progressOverlay);

        title.setText((String) getIntent().getExtras().get(Values.INTENT_KEY_PROFILE_PIC_USERNAME));
        String uid = (String) getIntent().getExtras().get(Values.INTENT_KEY_PROFILE_PIC_UID);

        loadProfilePic(uid);
    }

    /**
     * Load and display a profile picture from the backend.
     *
     * @param uid The users uid
     */
    private void loadProfilePic(String uid) {
        isLoading(true);
        final Context context = this;
        Call<Container<String>> call = userService.loadProfilePic(userInterface.getAccessToken(), uid);
        call.enqueue(new Callback<Container<String>>() {
            @Override
            public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                if (response.isSuccessful()) {
                    String profilePic = response.body().getContent();
                    if (profilePic != null) {
                        imageView.setImageBitmap(ImageUtil.createBitmap(profilePic));
                    } else {
                        imageView.setImageDrawable(ImageUtil.getDefaultProfilePic(context));
                    }
                }
                isLoading(false);
            }

            @Override
            public void onFailure(Call<Container<String>> call, Throwable t) {
                isLoading(false);
                showDialog(getString(R.string.error), getString(R.string.error_no_internet));
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
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
