package de.leonlatsch.olivia.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.chatlist.ChatListActivity;
import de.leonlatsch.olivia.dto.StringDTO;
import de.leonlatsch.olivia.dto.UserAuthDTO;
import de.leonlatsch.olivia.register.RegisterActivity;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerBtn;
    private Button loginBtn;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = RestServiceFactory.createUserService();

        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        registerBtn = findViewById(R.id.loginRegisterNowBtn);
        loginBtn = findViewById(R.id.loginBtn);

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

    private void login() {
        if (!isInputValid()) {
            // TODO: add error message
            return;
        }
        // TODO add sha256 sum in dto or util class
        UserAuthDTO userAuthDTO = new UserAuthDTO(emailEditText.getText().toString(), passwordEditText.getText().toString());

        Call<StringDTO> call = userService.auth(userAuthDTO);
        call.enqueue(new Callback<StringDTO>() {
            @Override
            public void onResponse(Call<StringDTO> call, Response<StringDTO> response) {
                StringDTO dto = response.body();

                if (dto.getMessage() == "AUTH_OK") {
                    Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<StringDTO> call, Throwable t) {

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

        if (email != null && !email.isEmpty()) {
            intent.putExtra(getString(R.string.loginEmail), email);
        }
    }
}
