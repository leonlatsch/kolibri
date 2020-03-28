package dev.leonlatsch.kolibri.ui.contactpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.constants.Values;
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface;
import dev.leonlatsch.kolibri.database.model.Contact;
import dev.leonlatsch.kolibri.ui.ProfilePicActivity;
import dev.leonlatsch.kolibri.util.ImageUtil;

/**
 * Activity to display information about a contact.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView profilePicImageView = findViewById(R.id.contact_profile_pic_card).findViewById(R.id.contact_profile_pic);
        TextView usernameTextView = findViewById(R.id.contact_username);

        String uid = (String) getIntent().getExtras().get(Values.INTENT_KEY_CONTACT_UID);
        Contact contact = ContactInterface.getInstance().getContact(uid);

        if (contact != null) {
            usernameTextView.setText(contact.getUsername());
            String profilePicTn = contact.getProfilePicTn();
            if (profilePicTn != null) {
                profilePicImageView.setImageBitmap(ImageUtil.createBitmap(profilePicTn));
            }

            profilePicImageView.setOnClickListener(v -> showProfilePic(contact.getUid(), contact.getUsername()));
        } else {
            finish();
        }
    }

    private void showProfilePic(String uid, String username) {
        Intent intent = new Intent(getApplicationContext(), ProfilePicActivity.class);
        intent.putExtra(Values.INTENT_KEY_PROFILE_PIC_UID, uid);
        intent.putExtra(Values.INTENT_KEY_PROFILE_PIC_USERNAME, username);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
