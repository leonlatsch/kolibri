package de.leonlatsch.olivia.boot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.constants.Responses;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.dto.Container;
import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;
import de.leonlatsch.olivia.login.LoginActivity;
import de.leonlatsch.olivia.main.MainActivity;
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

        new Handler().postDelayed(runnable, 500);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
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
    };

    /**
     * Checks if a user is saved and checks if the saved user is still in the backend
     * Deleted the saved user and restarts this Activity if the saved user is not in the backend
     * @return
     */
    private boolean isValidUserSaved() {
        try {
            final User savedUser = userInterface.getUser();
            if (savedUser != null) {
                Call<Container<UserDTO>> call = userService.get(savedUser.getAccessToken());
                call.enqueue(new Callback<Container<UserDTO>>() {
                    @Override
                    public void onResponse(Call<Container<UserDTO>> call, Response<Container<UserDTO>> response) {
                        if (response.code() == Responses.CODE_OK) {
                            // Update saved user
                            userInterface.save(response.body().getContent(), savedUser.getAccessToken(), savedUser.getPrivateKey());
                        } else {
                            // if the saved user is not in the backend finish this boot and the chatlist and start another boot
                            userInterface.delete(savedUser);
                            Intent intent = new Intent(getApplicationContext(), BootActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Container<UserDTO>> call, Throwable t) {}
                });
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
