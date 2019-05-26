package de.leonlatsch.olivia.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);

        final Button register = findViewById(R.id.loginRegisterNowBtn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                cacheToIntent(intent);
                startActivity(intent);
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
