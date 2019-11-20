package de.leonlatsch.olivia.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.KeyPair;
import java.util.regex.Pattern;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.rest.dto.Container;
import de.leonlatsch.olivia.main.MainActivity;
import de.leonlatsch.olivia.constants.Responses;
import de.leonlatsch.olivia.constants.Regex;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.rest.dto.UserDTO;
import de.leonlatsch.olivia.register.RegisterActivity;
import de.leonlatsch.olivia.rest.service.AuthService;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import de.leonlatsch.olivia.security.Hash;
import de.leonlatsch.olivia.security.CryptoManager;
import de.leonlatsch.olivia.util.AndroidUtils;
import de.leonlatsch.olivia.util.Base64;
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

    private UserService userService;
    private AuthService authService;
    private UserInterface userInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = RestServiceFactory.getUserService();
        authService = RestServiceFactory.getAuthService();
        userInterface = UserInterface.getInstance();

        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        registerBtn = findViewById(R.id.loginRegisterNowBtn);
        loginBtn = findViewById(R.id.loginBtn);
        progressOverlay = findViewById(R.id.progressOverlay);
        errorText = findViewById(R.id.loginErrorTextView);

        registerBtn.setOnClickListener(v -> register());

        loginBtn.setOnClickListener(v -> login());
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            login();
            return true;
        });
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
                    KeyPair newKeyPair = CryptoManager.genKeyPair();
                    userInterface.save(response.body().getContent(), accessToken, Base64.toBase64(newKeyPair.getPrivate().getEncoded()));
                    updatePublicKey(Base64.toBase64(newKeyPair.getPublic().getEncoded()));
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
