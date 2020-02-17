package dev.leonlatsch.kolibri.main.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.constants.Regex;
import dev.leonlatsch.kolibri.constants.Responses;
import dev.leonlatsch.kolibri.constants.Values;
import dev.leonlatsch.kolibri.database.interfaces.KeyPairInterface;
import dev.leonlatsch.kolibri.database.interfaces.UserInterface;
import dev.leonlatsch.kolibri.database.model.KeyPair;
import dev.leonlatsch.kolibri.main.MainActivity;
import dev.leonlatsch.kolibri.rest.dto.Container;
import dev.leonlatsch.kolibri.rest.dto.UserDTO;
import dev.leonlatsch.kolibri.rest.service.AuthService;
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory;
import dev.leonlatsch.kolibri.rest.service.UserService;
import dev.leonlatsch.kolibri.security.CryptoManager;
import dev.leonlatsch.kolibri.security.Hash;
import dev.leonlatsch.kolibri.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for registration
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class RegisterActivity extends AppCompatActivity {

    private View progressOverlay;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private Button registerBtn;

    private UserService userService;
    private AuthService authService;
    private UserInterface userInterface;
    private KeyPairInterface keyPairInterface;

    private boolean usernameValid;
    private boolean emailValid;
    private boolean passwordValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressOverlay = findViewById(R.id.progressOverlay);
        usernameEditText = findViewById(R.id.registerUsernameEditText);
        emailEditText = findViewById(R.id.registerEmailEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        passwordConfirmEditText = findViewById(R.id.registerPasswordConfirmEditText);
        registerBtn = findViewById(R.id.registerNowBtn);

        userService = RestServiceFactory.getUserService();
        authService = RestServiceFactory.getAuthService();
        userInterface = UserInterface.getInstance();
        keyPairInterface = KeyPairInterface.getInstance();

        registerBtn.setOnClickListener(v -> register());

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateUsername();
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });

        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword();
            }
        };

        passwordEditText.addTextChangedListener(passwordTextWatcher);
        passwordConfirmEditText.addTextChangedListener(passwordTextWatcher);

        loadCachedData();
        usernameEditText.requestFocus();
    }

    /**
     * Validate the value in the password EditText wit Regex and
     * compare with the confirm EditText.
     * Show a icon at the password edit text.
     */
    private void validatePassword() {
        final String password = passwordEditText.getText().toString();
        final String passwordConfirm = passwordConfirmEditText.getText().toString();

        if (password.isEmpty() || !Pattern.matches(Regex.PASSWORD, password)) {
            showStatusIcon(passwordEditText, R.drawable.icons8_cancel_48);
            passwordValid = false;
            return;
        } else {
            showStatusIcon(passwordEditText, R.drawable.icons8_checked_48);
        }

        if (password.equals(passwordConfirm)) {
            showStatusIcon(passwordConfirmEditText, R.drawable.icons8_checked_48);
            passwordValid = true;
        } else {
            showStatusIcon(passwordConfirmEditText, R.drawable.icons8_cancel_48);
            passwordValid = false;
        }
    }

    /**
     * Validate the value in the email EditText with regex and backend.
     * Show an icon at the email EditText.
     */
    private void validateEmail() {
        final String email = emailEditText.getText().toString();

        if (email.isEmpty() || !Pattern.matches(Regex.EMAIL, email)) {
            showStatusIcon(emailEditText, R.drawable.icons8_cancel_48);
            emailValid = false;
            return;
        }

        Call<Container<String>> call = userService.checkEmail(Values.EMPTY, email);
        call.enqueue(new Callback<Container<String>>() {
            @Override
            public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    if (Responses.MSG_FREE.equals(message)) {
                        showStatusIcon(emailEditText, R.drawable.icons8_checked_48);
                        emailValid = true;
                    } else {
                        showStatusIcon(emailEditText, R.drawable.icons8_cancel_48);
                        emailValid = false;
                    }
                }
            }

            @Override
            public void onFailure(Call<Container<String>> call, Throwable t) {
                showDialog(getString(R.string.error), getString(R.string.error));
            }
        });
    }

    /**
     * Validate the values in the username EditText.
     * Show an icon at the username EditText.
     */
    private void validateUsername() {
        final String username = usernameEditText.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            showStatusIcon(usernameEditText, R.drawable.icons8_cancel_48);
            usernameValid = false;
            return;
        }

        Call<Container<String>> call = userService.checkUsername(Values.EMPTY, username);
        call.enqueue(new Callback<Container<String>>() {
            @Override
            public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    if (Responses.MSG_FREE.equals(message)) {
                        showStatusIcon(usernameEditText, R.drawable.icons8_checked_48);
                        usernameValid = true;
                    } else {
                        usernameValid = false;
                        showStatusIcon(usernameEditText, R.drawable.icons8_cancel_48);
                    }
                }
            }

            @Override
            public void onFailure(Call<Container<String>> call, Throwable t) {
                showDialog(getString(R.string.error), getString(R.string.error));
            }
        });
    }

    /**
     * Called when the register button is pressed.
     */
    private void register() {
        isLoading(true);

        // Validate all input
        validateUsername();
        validateEmail();
        validatePassword();

        if (!usernameValid || !emailValid || !passwordValid) {
            isLoading(false);
            return;
        }

        final UserDTO userDTO = new UserDTO();
        userDTO.setEmail(emailEditText.getText().toString());
        userDTO.setUsername(usernameEditText.getText().toString());
        userDTO.setPassword(Hash.createHexHash(passwordEditText.getText().toString()));

        KeyPair keyPair = CryptoManager.genKeyPair();

        Call<Container<String>> call = authService.register(userDTO, keyPair.getPublicKey());
        call.enqueue(new Callback<Container<String>>() {
            @Override
            public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                isLoading(false);
                if (response.isSuccessful() && Responses.MSG_OK.equals(response.body().getMessage())) {
                    saveUserAndStartMain(response.body().getContent(), keyPair);
                } else {
                    showDialog(getString(R.string.error), getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Container<String>> call, Throwable t) {
                isLoading(false);
                showDialog("Error", getString(R.string.error_no_internet));
            }
        });
    }

    /**
     * Save the new registered user after the registration has finished and
     * start the {@link MainActivity}.
     *
     * @param accessToken The access token from the new user
     * @param keyPair     The new generated keypair
     */
    private void saveUserAndStartMain(final String accessToken, final KeyPair keyPair) {
        Call<Container<UserDTO>> call = userService.get(accessToken);
        call.enqueue(new Callback<Container<UserDTO>>() {
            @Override
            public void onResponse(Call<Container<UserDTO>> call, Response<Container<UserDTO>> response) {
                if (response.isSuccessful()) {
                    keyPair.setUid(response.body().getContent().getUid());
                    keyPairInterface.createOrGet(keyPair);
                    userInterface.save(response.body().getContent(), accessToken);
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
     * Show an icon at a EditText
     *
     * @param editText
     * @param drawable
     */
    private void showStatusIcon(EditText editText, int drawable) {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
    }

    /**
     * Load the parsed email address from the {@link LoginActivity}
     */
    private void loadCachedData() {
        if (getIntent().getExtras() != null) {
            String cachedEmail = (String) getIntent().getExtras().get(Values.INTENT_KEY_EMAIL);

            if (cachedEmail != null) {
                emailEditText.setText(cachedEmail);
            }
        }
    }

    private void isLoading(boolean loading) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f);
        } else {
            AndroidUtils.animateView(progressOverlay, View.GONE, 0.4f);
        }
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
