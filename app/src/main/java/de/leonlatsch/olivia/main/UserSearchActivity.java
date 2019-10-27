package de.leonlatsch.olivia.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.database.DatabaseMapper;
import de.leonlatsch.olivia.database.interfaces.ContactInterface;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.database.model.Contact;
import de.leonlatsch.olivia.rest.dto.Container;
import de.leonlatsch.olivia.rest.dto.UserDTO;
import de.leonlatsch.olivia.main.adapter.ContactAdapter;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import de.leonlatsch.olivia.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchActivity extends AppCompatActivity {

    private ImageView searchBtn;
    private EditText searchBar;
    private ListView listView;
    private ContactAdapter contactAdapter;
    private DatabaseMapper databaseMapper;

    private View progressOverlay;

    private UserService userService;
    private UserInterface userInterface;
    private ContactInterface contactInterface;

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
        contactInterface = ContactInterface.getInstance();

        searchBtn = findViewById(R.id.userSearchBtn);
        searchBar = findViewById(R.id.userSearchEditText);
        listView = findViewById(R.id.user_search_list_view);
        progressOverlay = findViewById(R.id.progressOverlay);

        contactAdapter = new ContactAdapter(this, new ArrayList<>());
        databaseMapper = DatabaseMapper.getInstance();

        searchBtn.setOnClickListener(v -> search());
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            search();
            return true;
        });

        listView.setAdapter(contactAdapter);
        listView.setOnItemClickListener(itemClickListener);
    }

    private AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {
        Object raw = listView.getItemAtPosition(position);
        if (raw instanceof Contact) {
            Contact user = (Contact) raw;
            proceedUser(user);
        }
    };

    private void proceedUser(Contact user) {
        Call<Container<String>> call = userService.getPublicKey(userInterface.getAccessToken(), user.getUid());
        call.enqueue(new Callback<Container<String>>() {
            @Override
            public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                if (response.isSuccessful()) {
                    user.setPublicKey(response.body().getContent());
                    contactInterface.save(user);
                }
            }

            @Override
            public void onFailure(Call<Container<String>> call, Throwable t) {}
        });
    }

    private void search() {
        if (searchBar.getText().toString().length() >= 2) {
            isLoading(true);
            Call<Container<List<UserDTO>>> call = userService.search(userInterface.getAccessToken(), searchBar.getText().toString());
            call.enqueue(new Callback<Container<List<UserDTO>>>() {
                @Override
                public void onResponse(Call<Container<List<UserDTO>>> call, Response<Container<List<UserDTO>>> response) {
                    if (response.isSuccessful()) {
                        Container<List<UserDTO>> container = response.body();
                        if (container.getContent() != null && !container.getContent().isEmpty()) {
                            contactAdapter.clear();
                            for (UserDTO user : container.getContent()) {
                                if (!user.getUid().equals(userInterface.getUser().getUid())) {
                                    contactAdapter.add(databaseMapper.toContact(user)); // TODO: start new chat activity and save contact at first message
                                }
                            }
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
