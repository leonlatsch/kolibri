package de.leonlatsch.olivia.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.broker.MessageConsumer;
import de.leonlatsch.olivia.broker.MessageListener;
import de.leonlatsch.olivia.rest.dto.MessageDTO;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
