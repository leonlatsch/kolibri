package de.leonlatsch.olivia.boot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
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
    private UserInterface userInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        ActiveAndroid.initialize(this);
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        userService = RestServiceFactory.getUserService();
        userInterface = UserInterface.getInstance();

        userInterface.loadUser();

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
            final User savedUser = userInterface.getUser();
            if (savedUser != null) {
                Call<UserDTO> call = userService.getbyUid(savedUser.getUid());
                call.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.code() == 204) {
                            // if the saved user is not in the backend finish this boot and the chatlist and start another boot
                            userInterface.deleteUser(savedUser);
                            Intent intent = new Intent(getApplicationContext(), BootActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 200) {
                            // Update saved user
                            userInterface.deleteUser(savedUser);
                            userInterface.saveUser(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {}
                });
                return true;
            } else {
                return false;
            }
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
