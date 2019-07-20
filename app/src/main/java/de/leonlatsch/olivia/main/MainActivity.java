package de.leonlatsch.olivia.main;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.database.EntityChangedListener;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.entity.User;
import de.leonlatsch.olivia.login.LoginActivity;
import de.leonlatsch.olivia.main.fragment.ChatFragment;
import de.leonlatsch.olivia.main.fragment.ProfileFragment;
import de.leonlatsch.olivia.util.ImageUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EntityChangedListener<User> {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private UserInterface userInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userInterface = UserInterface.getInstance();
        userInterface.addEntityChangedListener(this);

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(this);

        final TextView logout = navigationView.findViewById(R.id.nav_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

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
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
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

        profilePic.setImageBitmap(ImageUtil.createBitmap(user.getProfilePicTn()));
        username.setText(user.getUsername());
        email.setText(user.getEmail());
    }

    public void logout() {
        User user = userInterface.getUser();
        if (user != null) {
            userInterface.deleteUser(user);
        }
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    /////////////////////// IGNORE FOR NOW ///////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_secondary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
