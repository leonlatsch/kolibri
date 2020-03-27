package dev.leonlatsch.kolibri.ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.boot.BootActivity;
import dev.leonlatsch.kolibri.constants.Regex;
import dev.leonlatsch.kolibri.constants.Responses;
import dev.leonlatsch.kolibri.constants.Values;
import dev.leonlatsch.kolibri.database.interfaces.KeyPairInterface;
import dev.leonlatsch.kolibri.database.interfaces.UserInterface;
import dev.leonlatsch.kolibri.database.model.KeyPair;
import dev.leonlatsch.kolibri.ui.MainActivity;
import dev.leonlatsch.kolibri.rest.dto.Container;
import dev.leonlatsch.kolibri.rest.dto.UserDTO;
import dev.leonlatsch.kolibri.rest.service.AuthService;
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory;
import dev.leonlatsch.kolibri.rest.service.UserService;
import dev.leonlatsch.kolibri.security.CryptoManager;
import dev.leonlatsch.kolibri.security.Hash;
import dev.leonlatsch.kolibri.settings.Config;
import dev.leonlatsch.kolibri.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This Activity is used for logging in and directing to the Registration
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerBtn;
    private Button loginBtn;
    private TextView errorText;
    private View progressOverlay;
    private ImageView disconnectButton;

    private UserService userService;
    private AuthService authService;
    private UserInterface userInterface;
    private KeyPairInterface keyPairInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = RestServiceFactory.getUserService();
        authService = RestServiceFactory.getAuthService();
        userInterface = UserInterface.getInstance();
        keyPairInterface = KeyPairInterface.getInstance();

        usernameEditText = findViewById(R.id.loginUsernameEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        registerBtn = findViewById(R.id.loginRegisterNowBtn);
        loginBtn = findViewById(R.id.loginBtn);
        progressOverlay = findViewById(R.id.progressOverlay);
        errorText = findViewById(R.id.loginErrorTextView);
        disconnectButton = findViewById(R.id.disconnectButton);

        registerBtn.setOnClickListener(v -> register());
        disconnectButton.setOnClickListener(v -> disconnect());

        loginBtn.setOnClickListener(v -> login());
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            login();
            return true;
        });
    }

    /**
     * Empty the backend config and start a new {@link BootActivity}
     */
    private void disconnect() {
        DialogInterface.OnClickListener onClickListener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                SharedPreferences.Editor editor = Config.getSharedPreferences(this).edit();
                editor.remove(Config.KEY_BACKEND_HTTP_BASEURL);
                editor.remove(Config.KEY_BACKEND_BROKER_HOST);
                editor.remove(Config.KEY_BACKEND_BROKER_PORT);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), BootActivity.class));
                finish();
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(getString(R.string.are_you_sure_disconnect))
                .setPositiveButton(getString(R.string.yes), onClickListener)
                .setNegativeButton(getString(R.string.no), onClickListener)
                .show();
    }

    /**
     * Called when the login button is pressed
     */
    private void login() {
        isLoading(true);
        if (!isInputValid()) {
            isLoading(false);
            displayError(getString(R.string.login_fail));
            return;
        }
        displayError(Values.EMPTY);
        final UserDTO userAuthDTO = new UserDTO();
        userAuthDTO.setUsername(usernameEditText.getText().toString());
        userAuthDTO.setPassword(Hash.createHexHash(passwordEditText.getText().toString()));

        Call<Container<String>> call = authService.login(userAuthDTO);
        call.enqueue(new Callback<Container<String>>() {
            @Override
            public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                if (Responses.CODE_UNAUTHORIZED != response.code()) {
                    saveUserAndStartMain(response.body().getContent());
                } else {
                    displayError(getString(R.string.login_fail));
                    isLoading(false);
                }
            }

            @Override
            public void onFailure(Call<Container<String>> call, Throwable t) {
                isLoading(false);
                showDialog(getString(R.string.error), getString(R.string.error_no_internet));
            }
        });
    }

    /**
     * Save the new logged in user and start the {@link MainActivity}
     *
     * @param accessToken
     */
    private void saveUserAndStartMain(final String accessToken) {
        Call<Container<UserDTO>> call = userService.get(accessToken);
        call.enqueue(new Callback<Container<UserDTO>>() {
            @Override
            public void onResponse(Call<Container<UserDTO>> call, Response<Container<UserDTO>> response) {
                if (response.isSuccessful()) {
                    KeyPair newKeyPair = keyPairInterface.createOrGet(CryptoManager.genKeyPair(), response.body().getContent().getUid());
                    userInterface.save(response.body().getContent(), accessToken);
                    updatePublicKey(newKeyPair.getPublicKey());
                    isLoading(false);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Container<UserDTO>> call, Throwable t) {
                showDialog(getString(R.string.error), getString(R.string.error_no_internet));
            }
        });
    }

    /**
     * Update the public key in the backend
     *
     * @param publicKey
     */
    private void updatePublicKey(final String publicKey) {
        Call<Container<String>> call = userService.updatePublicKey(userInterface.getAccessToken(), publicKey);
        call.enqueue(new Callback<Container<String>>() {
            @Override
            public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                if (response.isSuccessful()) {
                    if (!Responses.MSG_OK.equals(response.body().getMessage())) {
                        showDialog(getString(R.string.error), getString(R.string.error));
                    }
                }
            }

            @Override
            public void onFailure(Call<Container<String>> call, Throwable t) {
                showDialog(getString(R.string.error), getString(R.string.error_no_internet));
            }
        });
    }

    /**
     * Route to the registration
     */
    private void register() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        cacheToIntent(intent);
        startActivity(intent);
    }

    /**
     * Validate all input
     *
     * @return true/false
     */
    private boolean isInputValid() {
        boolean isValid = true;

        if (!isEmailValid(usernameEditText.getText().toString())) {
            isValid = false;
        }

        if (passwordEditText.getText().toString().isEmpty()) {
            isValid = false;
        }

        return isValid;
    }

    private void displayError(String message) {
        errorText.setText(message);
    }

    private boolean isEmailValid(String email) {
        return !email.isEmpty() || !Pattern.matches(Regex.EMAIL, email);
    }

    private void cacheToIntent(Intent intent) {
        String username = usernameEditText.getText().toString();

        if (!username.isEmpty()) {
            intent.putExtra(Values.INTENT_KEY_USERNAME, username);
        }
    }

    private void isLoading(boolean loading) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f);
        } else {
            AndroidUtils.animateView(progressOverlay, View.GONE, 0.4f);
        }
        usernameEditText.setEnabled(!loading);
        passwordEditText.setEnabled(!loading);
        loginBtn.setEnabled(!loading);
        registerBtn.setEnabled(!loading);
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
