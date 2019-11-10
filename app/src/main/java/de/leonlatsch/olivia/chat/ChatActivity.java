package de.leonlatsch.olivia.chat;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.database.model.Message;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView messageRecycler;
    private MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Message> messageList = new ArrayList<>(); // Mock data for test
        messageList.add(new Message("qhsgfdeagde", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec3", "TEXT", new Timestamp(21426), "Hallo ich bims"));
        messageList.add(new Message("shdfgssfjgh", "abcde", "ae414f01-989a-47de-8b5f-b6642a58dec3", "be414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Ich auch"));
        messageList.add(new Message("dsfhdfahjdl", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Cool"));
        messageList.add(new Message("asfagdadsge", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Ich bin ein fisch"));

        messageRecycler = findViewById(R.id.chat_recycler_view);
        messageListAdapter = new MessageListAdapter(this, messageList);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
        messageRecycler.setAdapter(messageListAdapter);
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
