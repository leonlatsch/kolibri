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
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.database.model.Chat;
import de.leonlatsch.olivia.database.model.Message;

public class ChatActivity extends AppCompatActivity {

    private Chat chat;
    private String chatUid;

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

        chatUid = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_UID);

        List<Message> messageList = new ArrayList<>(); // Mock data for test
        messageList.add(new Message("qhsgfdeagde", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec3", "TEXT", new Timestamp(21426), "Hallo ich bins"));
        messageList.add(new Message("shdfgssfjgh", "abcde", "ae414f01-989a-47de-8b5f-b6642a58dec3", "be414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Moin"));
        messageList.add(new Message("dsfhdfahjdl", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Wie gehts?"));
        messageList.add(new Message("asfagdadsge", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Hast du schon das Dokument für mich? Ist sehr wichtig."));
        messageList.add(new Message("adgadgadffd", "abcde", "ae414f01-989a-47de-8b5f-b6642a58dec3", "be414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Ja hab ich"));
        messageList.add(new Message("adgadgadffd", "abcde", "ae414f01-989a-47de-8b5f-b6642a58dec3", "be414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Muss heute aber noch einkaufen. Bringe es fir später.\nBis dann."));

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
