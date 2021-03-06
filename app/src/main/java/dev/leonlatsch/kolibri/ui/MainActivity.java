package dev.leonlatsch.kolibri.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.broker.MessageConsumer;
import dev.leonlatsch.kolibri.broker.queue.MessageQueue;
import dev.leonlatsch.kolibri.database.EntityChangedListener;
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface;
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface;
import dev.leonlatsch.kolibri.database.interfaces.UserInterface;
import dev.leonlatsch.kolibri.database.model.User;
import dev.leonlatsch.kolibri.ui.chatlist.ChatFragment;
import dev.leonlatsch.kolibri.ui.profile.ProfileFragment;
import dev.leonlatsch.kolibri.ui.settings.SettingsFragment;
import dev.leonlatsch.kolibri.ui.login.LoginActivity;
import dev.leonlatsch.kolibri.util.ImageUtil;

/**
 * The main activity which holds the main menu and the main fragments
 *
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
                displayFragment(new ChatFragment(), getString(R.string.app_name));
                break;

            case R.id.nav_profile:
                displayFragment(new ProfileFragment(), getString(R.string.profile));
                break;

            case R.id.nav_settings:
                displayFragment(new SettingsFragment(), getString(R.string.settings));
                break;

            case R.id.nav_info:
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
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
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ChatFragment) {
                super.onBackPressed();
            } else {
                displayFragment(new ChatFragment(), getString(R.string.app_name));
                navigationView.setCheckedItem(R.id.nav_chat);
            }
        }
    }

    private void displayFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
        titleTextView.setText(title);
    }

    /**
     * Display a user in the drawer view
     *
     * @param user
     */
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

    public void logout(boolean withDialog) {
        if (withDialog) {
            DialogInterface.OnClickListener onClickListener = (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    doLogout();
                }
            };

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setMessage(getString(R.string.logout_confirm))
                    .setPositiveButton(getString(R.string.yes), onClickListener)
                    .setNegativeButton(getString(R.string.no), onClickListener)
                    .show();
        } else {
            doLogout();
        }
    }

    /**
     * Called when the logout button is pressed
     * <p>
     * Delete the logged in user, all chats and contacts
     */
    public void logout() {
        logout(true);
    }

    private void doLogout() {
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

    /**
     * Called when the logged in user has changed
     *
     * @param newEntity
     */
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
