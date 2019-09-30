package de.leonlatsch.olivia.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;


import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.dto.Container;
import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchActivity extends AppCompatActivity {

    private ImageView searchBtn;
    private EditText searchBar;

    private UserService userService;
    private UserInterface userInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        Toolbar toolbar = findViewById(R.id.user_search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userService = RestServiceFactory.getUserService();
        userInterface = UserInterface.getInstance();

        searchBtn = findViewById(R.id.userSearchBtn);
        searchBar = findViewById(R.id.userSearchEditText);

        searchBtn.setOnClickListener(v -> search());
    }

    private void search() {
        Call<Container<List<UserDTO>>> call = userService.search(userInterface.getAccessToken(), searchBar.getText().toString());
        call.enqueue(new Callback<Container<List<UserDTO>>>() {
            @Override
            public void onResponse(Call<Container<List<UserDTO>>> call, Response<Container<List<UserDTO>>> response) {
                if (response.isSuccessful()) {
                    Container<List<UserDTO>> container = response.body();
                    System.out.println(container.getCode());
                    System.out.println(container.getMessage());
                    for (UserDTO dto : container.getContent()) {
                        System.out.println(dto.getUid());
                        System.out.println(dto.getUsername());
                    }
                }
            }

            @Override
            public void onFailure(Call<Container<List<UserDTO>>> call, Throwable t) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
