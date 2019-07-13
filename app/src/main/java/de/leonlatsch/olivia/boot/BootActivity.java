package de.leonlatsch.olivia.boot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.main.MainActivity;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;
import de.leonlatsch.olivia.login.LoginActivity;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BootActivity extends AppCompatActivity {

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        ActiveAndroid.initialize(this);
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        userService = RestServiceFactory.getUserService();

        Intent intent = null;

        if (isValidUserSaved()) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }

        startActivity(intent);
        // Make it so you cant go back to this activity
        finish();
    }

    /**
     * Checks if a user is saved and checks if the saved user is still in the backend
     * Deleted the saved user and restarts this Activity if the saved user is not in the backend
     * @return
     */
    private boolean isValidUserSaved() {
        try {
            List<User> list = new Select().from(User.class).execute();
            if (!list.isEmpty()) {
                final User savedUser = list.get(0);;
                Call<UserDTO> call = userService.getbyUid(savedUser.getUid());
                call.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.code() != 200) {
                            // if the saved user is not in the backend finish this boot and the chatlist and start another boot
                            savedUser.delete();
                            Intent intent = new Intent(getApplicationContext(), BootActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {}
                });
            }
            return !list.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // May be useful later
    private boolean isFirstBoot() {
        SharedPreferences sharedPreferences = getSharedPreferences(Values.PREF_FIRST_BOOT, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(Values.PREF_FIRST_BOOT, true)) {
            editor.putBoolean(Values.PREF_FIRST_BOOT, false);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }
}
