package dev.leonlatsch.olivia.login;

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

import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.boot.BootActivity;
import dev.leonlatsch.olivia.constants.Regex;
import dev.leonlatsch.olivia.constants.Responses;
import dev.leonlatsch.olivia.constants.Values;
import dev.leonlatsch.olivia.database.interfaces.KeyPairInterface;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.database.model.KeyPair;
import dev.leonlatsch.olivia.main.MainActivity;
import dev.leonlatsch.olivia.register.RegisterActivity;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.service.AuthService;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.rest.service.UserService;
import dev.leonlatsch.olivia.security.CryptoManager;
import dev.leonlatsch.olivia.security.Hash;
import dev.leonlatsch.olivia.settings.Config;
import dev.leonlatsch.olivia.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
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

        emailEditText = findViewById(R.id.loginEmailEditText);
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
        builder.setMessage(getString(R.string.are_you_sure_disconnect)).setPositiveButton(getString(R.string.yes), onClickListener)
                .setNegativeButton(getString(R.string.no), onClickListener).show();
    }

    private void login() {
        isLoading(true);
        if (!isInputValid()) {
            isLoading(false);
            displayError(getString(R.string.login_fail));
            return;
        }
        displayError(Values.EMPTY);
        final UserDTO userAuthDTO = new UserDTO();
        userAuthDTO.setEmail(emailEditText.getText().toString());
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

    private void register() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        cacheToIntent(intent);
        startActivity(intent);
    }

    private boolean isInputValid() {
        boolean isValid = true;

        if (!isEmailValid(emailEditText.getText().toString())) {
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

    private void displayIcon(EditText editText, int drawable) {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
    }

    private void cacheToIntent(Intent intent) {
        String email = emailEditText.getText().toString();

        if (!email.isEmpty()) {
            intent.putExtra(Values.INTENT_KEY_EMAIL, email);
        }
    }

    private void isLoading(boolean loading) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f);
        } else {
            AndroidUtils.animateView(progressOverlay, View.GONE, 0.4f);
        }
        emailEditText.setEnabled(!loading);
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
