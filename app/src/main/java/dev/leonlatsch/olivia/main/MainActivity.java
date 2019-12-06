package dev.leonlatsch.olivia.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.broker.MessageConsumer;
import dev.leonlatsch.olivia.broker.queue.MessageQueue;
import dev.leonlatsch.olivia.database.EntityChangedListener;
import dev.leonlatsch.olivia.database.interfaces.ChatInterface;
import dev.leonlatsch.olivia.database.interfaces.ContactInterface;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.database.model.User;
import dev.leonlatsch.olivia.login.LoginActivity;
import dev.leonlatsch.olivia.main.fragment.ChatFragment;
import dev.leonlatsch.olivia.main.fragment.ProfileFragment;
import dev.leonlatsch.olivia.main.fragment.SettingsFragment;
import dev.leonlatsch.olivia.util.ImageUtil;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EntityChangedListener<User> {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View progressOverlay;
    private TextView titleTextView;

    private UserInterface userInterface;
    private ContactInterface contactInterface;
    private ChatInterface chatInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userInterface = UserInterface.getInstance();
        userInterface.addEntityChangedListener(this);
        contactInterface = ContactInterface.getInstance();
        chatInterface = ChatInterface.getInstance();

        MessageConsumer.start(this);
        MessageQueue.start();

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        progressOverlay = findViewById(R.id.progressOverlay);
        titleTextView = findViewById(R.id.main_toolbar_title);

        navigationView.setNavigationItemSelectedListener(this);

        final TextView logout = navigationView.findViewById(R.id.nav_logout);
        logout.setOnClickListener(v -> logout());

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mapUserToDrawer(userInterface.getUser());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ChatFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_chat);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
                titleTextView.setText(getString(R.string.app_name));

                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                titleTextView.setText(getString(R.string.profile));
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                titleTextView.setText(getString(R.string.settings));
                break;

            case R.id.nav_help:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void mapUserToDrawer(User user) {
        if (user == null) {
            return;
        }
        View header = navigationView.getHeaderView(0);
        ImageView profilePic = header.findViewById(R.id.nav_profile_pic_card).findViewById(R.id.nav_profile_pic);
        TextView username = header.findViewById(R.id.nav_username);
        TextView email = header.findViewById(R.id.nav_email);

        if (user.getProfilePicTn() != null) {
            profilePic.setImageBitmap(ImageUtil.createBitmap(user.getProfilePicTn()));
        }
        username.setText(user.getUsername());
        email.setText(user.getEmail());
    }

    public void logout() {
        User user = userInterface.getUser();
        if (user != null) {
            userInterface.delete(user);
        }
        contactInterface.deleteAll();
        chatInterface.deleteAll();
        MessageConsumer.stop();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    public void entityChanged(User newEntity) {
        if (newEntity != null) {
            mapUserToDrawer(newEntity);
        }
    }

    public void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public View getProgressOverlay() {
        return progressOverlay;
    }
}
