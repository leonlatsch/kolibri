package dev.leonlatsch.olivia.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.chat.ChatActivity;
import dev.leonlatsch.olivia.constants.Values;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.main.adapter.UserAdapter;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.rest.service.UserService;
import dev.leonlatsch.olivia.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity to search for new users
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class UserSearchActivity extends AppCompatActivity {

    private ImageView searchBtn;
    private EditText searchBar;
    private ListView listView;
    private UserAdapter userAdapter;

    private View progressOverlay;

    private UserService userService;
    private UserInterface userInterface;
    private AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {
        Object raw = listView.getItemAtPosition(position);
        if (raw instanceof UserDTO) {
            UserDTO user = (UserDTO) raw;
            proceedUser(user);
        }
    };

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
        listView = findViewById(R.id.user_search_list_view);
        progressOverlay = findViewById(R.id.progressOverlay);

        userAdapter = new UserAdapter(this, new ArrayList<>());

        searchBtn.setOnClickListener(v -> search());
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            search();
            return true;
        });

        listView.setAdapter(userAdapter);
        listView.setOnItemClickListener(itemClickListener);

        searchBar.requestFocus();
    }

    /**
     * Called when a user is selected.
     *
     * @param user
     */
    private void proceedUser(UserDTO user) {
        Call<Container<String>> call = userService.getPublicKey(userInterface.getAccessToken(), user.getUid());
        call.enqueue(new Callback<Container<String>>() {
            @Override
            public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra(Values.INTENT_KEY_CHAT_UID, user.getUid());
                    intent.putExtra(Values.INTENT_KEY_CHAT_USERNAME, user.getUsername());
                    intent.putExtra(Values.INTENT_KEY_CHAT_PROFILE_PIC, user.getProfilePicTn());
                    intent.putExtra(Values.INTENT_KEY_CHAT_PUBLIC_KEY, response.body().getContent());
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Container<String>> call, Throwable t) {
            }
        });
    }

    /**
     * Search for users with the value from the search EditText.
     */
    private void search() {
        if (searchBar.getText().toString().length() >= 2) { // Only search if at least 2 chars where entered
            isLoading(true);
            Call<Container<List<UserDTO>>> call = userService.search(userInterface.getAccessToken(), searchBar.getText().toString());
            call.enqueue(new Callback<Container<List<UserDTO>>>() {
                @Override
                public void onResponse(Call<Container<List<UserDTO>>> call, Response<Container<List<UserDTO>>> response) {
                    if (response.isSuccessful()) {
                        Container<List<UserDTO>> container = response.body();
                        if (container.getContent() != null && !container.getContent().isEmpty()) {
                            userAdapter.clear();
                            userAdapter.addAll(container.getContent());
                        }
                    }
                    isLoading(false);
                }

                @Override
                public void onFailure(Call<Container<List<UserDTO>>> call, Throwable t) {
                    isLoading(false);
                }
            });
        }
    }

    private void isLoading(boolean loading) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f);
        } else {
            AndroidUtils.animateView(progressOverlay, View.GONE, 0.4f);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
