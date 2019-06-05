package de.leonlatsch.olivia.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.register.RegisterActivity;
import de.leonlatsch.olivia.rest.http.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserRestService;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    private UserRestService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = RestServiceFactory.createUserService();

        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);

        final Button register = findViewById(R.id.loginRegisterNowBtn);
        final Button login = findViewById(R.id.loginBtn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                cacheToIntent(intent);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userService.getAll();
            }
        });
        }


        private void cacheToIntent(Intent intent) {
        String email = emailEditText.getText().toString();

        if (email != null && !email.isEmpty()) {
            intent.putExtra(getString(R.string.loginEmail), email);
        }
    }
}
