package de.leonlatsch.olivia.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import de.leonlatsch.olivia.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsernameEditText;
    private EditText registerEmailEditText;
    private EditText registerPasswordEditText;
    private EditText registerPasswordConfirmEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUsernameEditText = findViewById(R.id.registerUsernameEditText);
        registerEmailEditText = findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);
        registerPasswordConfirmEditText = findViewById(R.id.registerPasswordConfirmEditText);

        loadCachedData();

        registerUsernameEditText.requestFocus();
    }

    private void loadCachedData() {
        if (getIntent().getExtras() != null) {
            String cachedEmail = (String) getIntent().getExtras().get(getString(R.string.loginEmail));

            if (cachedEmail != null) {
                registerEmailEditText.setText(cachedEmail);
            }
        }
    }
}
