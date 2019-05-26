package de.leonlatsch.olivia.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.register.RegisterActivity;
import de.leonlatsch.olivia.rest.http.RestServiceFactory;
import de.leonlatsch.olivia.transfer.TransferObject;
import de.leonlatsch.olivia.transfer.TransferUser;
import retrofit2.Call;
import retrofit2.Response;

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
                //Test code
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Call<List<TransferUser>> result = RestServiceFactory.createUserService ().getAllUsers();
                            Response<List<TransferUser>> resp = result.execute();
                            for (TransferUser user : resp.body()) {
                                System.out.println(user.getUsername());
                            }
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }
                }).start();
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
