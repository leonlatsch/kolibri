package de.leonlatsch.olivia.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.chatlist.ChatListActivity;
import de.leonlatsch.olivia.constants.JsonRespose;
import de.leonlatsch.olivia.dto.StringDTO;
import de.leonlatsch.olivia.dto.UserAuthDTO;
import de.leonlatsch.olivia.register.RegisterActivity;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import de.leonlatsch.olivia.security.Hash;
import de.leonlatsch.olivia.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerBtn;
    private Button loginBtn;

    private View progressOverlay;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = RestServiceFactory.createUserService();

        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEmail(s.toString());
            }
        });

        registerBtn = findViewById(R.id.loginRegisterNowBtn);
        loginBtn = findViewById(R.id.loginBtn);

        progressOverlay = findViewById(R.id.progressOverlay);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void checkPassword(String toString) {

    }

    private void checkEmail(String email) {
        Call<StringDTO> call = userService.checkEmail(email);
        call.enqueue(new Callback<StringDTO>() {
            @Override
            public void onResponse(Call<StringDTO> call, Response<StringDTO> response) {
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    if (JsonRespose.TAKEN.equals(message)) {
                        emailEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icons8_checked_48, 0);
                    } else {
                        emailEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icons8_cancel_48, 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<StringDTO> call, Throwable t) {
                //TODO errer message
            }
        });
    }

    private void login() {
        isLoading(true);
        if (!isInputValid()) {
            isLoading(false);
            return;
        }
        UserAuthDTO userAuthDTO = new UserAuthDTO(emailEditText.getText().toString(), Hash.createHexHash(passwordEditText.getText().toString()));

        Call<StringDTO> call = userService.auth(userAuthDTO);
        call.enqueue(new Callback<StringDTO>() {
            @Override
            public void onResponse(Call<StringDTO> call, Response<StringDTO> response) {
                StringDTO dto = response.body();

                if (JsonRespose.OK.equals(dto.getMessage())) {
                    Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                    startActivity(intent);
                } else {
                    System.out.println(dto.getMessage());
                }
                isLoading(false);
            }

            @Override
            public void onFailure(Call<StringDTO> call, Throwable t) {
                System.out.println(t);
                isLoading(false);
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
        //TODO Replaace wiht email regex
        if (emailEditText.getText().toString().isEmpty() || !emailEditText.getText().toString().contains("@")) {
            isValid = false;
        }

        if (passwordEditText.getText().toString().isEmpty()) {
            isValid = false;
        }

        return isValid;
    }

    private void cacheToIntent(Intent intent) {
        String email = emailEditText.getText().toString();

        if (!email.isEmpty()) {
            intent.putExtra(getString(R.string.loginEmail), email);
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
}
