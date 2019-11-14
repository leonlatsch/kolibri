package de.leonlatsch.olivia.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.broker.MessageListener;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.database.interfaces.ChatInterface;
import de.leonlatsch.olivia.database.interfaces.ContactInterface;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.database.model.Chat;
import de.leonlatsch.olivia.database.model.Contact;
import de.leonlatsch.olivia.database.model.Message;
import de.leonlatsch.olivia.rest.dto.MessageDTO;
import de.leonlatsch.olivia.util.Generator;
import de.leonlatsch.olivia.util.ImageUtil;

public class ChatActivity extends AppCompatActivity implements MessageListener {

    private Chat chat;
    private Contact contact;
    private boolean isTemp;

    private EditText messageEditText;

    private MessageListAdapter messageListAdapter;

    private ContactInterface contactInterface;
    private UserInterface userInterface;
    private ChatInterface chatInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initChat((String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_UID));

        contactInterface = ContactInterface.getInstance();
        userInterface = UserInterface.getInstance();
        chatInterface = ChatInterface.getInstance();

        List<Message> messageList = new ArrayList<>(); // Mock data for test
        messageList.add(new Message("qhsgfdeagde", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec3", "TEXT", new Timestamp(21426), "Hallo ich bins"));
        messageList.add(new Message("shdfgssfjgh", "abcde", "ae414f01-989a-47de-8b5f-b6642a58dec3", "be414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Moin"));
        messageList.add(new Message("dsfhdfahjdl", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Wie gehts?"));
        messageList.add(new Message("asfagdadsge", "abcde", "be414f01-989a-47de-8b5f-b6642a58dec3", "ae414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Hast du schon das Dokument für mich? Ist sehr wichtig."));
        messageList.add(new Message("adgadgadffd", "abcde", "ae414f01-989a-47de-8b5f-b6642a58dec3", "be414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Ja hab ich"));
        messageList.add(new Message("adgadgadffd", "abcde", "ae414f01-989a-47de-8b5f-b6642a58dec3", "be414f01-989a-47de-8b5f-b6642a58dec4", "TEXT", new Timestamp(21426), "Muss heute aber noch einkaufen. Bringe es fir später.\nBis dann."));

        RecyclerView messageRecycler = findViewById(R.id.chat_recycler_view);
        messageListAdapter = new MessageListAdapter(this, messageList);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
        messageRecycler.setAdapter(messageListAdapter);

        messageEditText = findViewById(R.id.chat_edit_text);
        TextView usernameEditText = findViewById(R.id.chat_username_textview);
        ImageView profilePicImageView = findViewById(R.id.chat_profile_pic_image_view);

        Contact contact = contactInterface.getContact(chat.getUid());
        if (contact != null) {
            usernameEditText.setText(contact.getUsername());
            profilePicImageView.setImageBitmap(ImageUtil.createBitmap(contact.getProfilePicTn()));
        } else {
            String username = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_USERNAME);
            String profilePic = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_PROFILE_PIC);
            usernameEditText.setText(username);
            if (profilePic != null) {
                profilePicImageView.setImageBitmap(ImageUtil.createBitmap(profilePic));
            } else {
                profilePicImageView.setImageDrawable(ImageUtil.getDefaultProfilePic(this));
            }
        }
    }

    private Message constructMessage() {
        if (!messageEditText.getText().toString().isEmpty()) {
            String messageTExt = messageEditText.getText().toString();

            Message message = new Message();
            if (chat == null) {

            }
        } else {
            return null;
        }
        return null;
    }

    private void initChat(String uid) {
        Contact contact = contactInterface.getContact(uid);
        if (contact != null) {
            this.contact = contact;
            this.chat = chatInterface.getChatForContact(uid);
            isTemp = false;
        } else {
            this.contact = new Contact();
            this.contact.setUid(uid);
            chat = new Chat(Generator.genUUid(), this.contact.getUid());
            isTemp = true;
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

    @Override
    public void receive(MessageDTO message) {

    }
}
