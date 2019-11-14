package de.leonlatsch.olivia.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
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
import de.leonlatsch.olivia.constants.Formats;
import de.leonlatsch.olivia.constants.MessageType;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.database.DatabaseMapper;
import de.leonlatsch.olivia.database.interfaces.ChatInterface;
import de.leonlatsch.olivia.database.interfaces.ContactInterface;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.database.model.Chat;
import de.leonlatsch.olivia.database.model.Contact;
import de.leonlatsch.olivia.database.model.Message;
import de.leonlatsch.olivia.rest.dto.Container;
import de.leonlatsch.olivia.rest.dto.MessageDTO;
import de.leonlatsch.olivia.rest.service.ChatService;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.security.CryptoManager;
import de.leonlatsch.olivia.util.Generator;
import de.leonlatsch.olivia.util.ImageUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements MessageListener {

    private Chat chat;
    private Contact contact;
    private boolean isTemp;

    private EditText messageEditText;
    private RecyclerView messageRecycler;

    private MessageListAdapter messageListAdapter;

    private ContactInterface contactInterface;
    private UserInterface userInterface;
    private ChatInterface chatInterface;

    private ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactInterface = ContactInterface.getInstance();
        userInterface = UserInterface.getInstance();
        chatInterface = ChatInterface.getInstance();
        chatService = RestServiceFactory.getChatService();

        initData();

        messageRecycler = findViewById(R.id.chat_recycler_view);

        List<Message> messageList;
        if (!isTemp) {
            messageList = chatInterface.getMessagesForChat(chat.getCid());
        } else {
            messageList = new ArrayList<>();
        }
        messageListAdapter = new MessageListAdapter(this, messageList);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
        messageRecycler.setAdapter(messageListAdapter);

        messageEditText = findViewById(R.id.chat_edit_text);
        TextView usernameEditText = findViewById(R.id.chat_username_textview);
        ImageView profilePicImageView = findViewById(R.id.chat_profile_pic_image_view);
        ImageButton sendButton = findViewById(R.id.chat_button_send);
        sendButton.setOnClickListener(v -> onSendPressed());

        Contact contact = contactInterface.getContact(chat.getUid());
        if (contact != null) {
            usernameEditText.setText(contact.getUsername());
            if (this.contact.getProfilePicTn() != null) {
                profilePicImageView.setImageBitmap(ImageUtil.createBitmap(contact.getProfilePicTn()));
            }
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

    private Callback<Container<String>> sendMessageCallback = new Callback<Container<String>>() {
        @Override
        public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {}

        @Override
        public void onFailure(Call<Container<String>> call, Throwable t) {
            //TODO: add message to queue if sending fails
        }
    };

    private void onSendPressed() {
        if (!messageEditText.getText().toString().isEmpty()) {
            Message message = constructMessage();
            if (isTemp) { // If this is the first message save the temo chat and contact
                chatInterface.saveChat(chat);
                contactInterface.save(contact);
                isTemp = false;
            }
            chatInterface.saveMessage(message);
            messageListAdapter.add(message);
            messageEditText.setText(Values.EMPTY);
            messageRecycler.scrollToPosition(messageListAdapter.getLastPosition());
            messageEditText.requestFocus();

            MessageDTO encryptedMessage = DatabaseMapper.getInstance().toDto(message);
            encryptedMessage.setContent(CryptoManager.encryptAndEncode(encryptedMessage.getContent().getBytes(), contact.getPublicKey()));
            Call<Container<String>> call = chatService.send(userInterface.getAccessToken(), encryptedMessage);
            call.enqueue(sendMessageCallback);
        }
    }

    private Message constructMessage() {
        String messageText = messageEditText.getText().toString();

        Message message = new Message();
        message.setCid(chat.getCid());
        message.setFrom(userInterface.getUser().getUid());
        message.setTo(contact.getUid());
        message.setMid(Generator.genUUid());
        message.setType(MessageType.TEXT);
        message.setTimestamp(Formats.DATE_FORMAT.format(new Timestamp(System.currentTimeMillis())));
        message.setContent(messageText);
        return message;
    }

    private void initData() {
        String uid = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_UID);
        String username = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_USERNAME);
        String profilePic = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_PROFILE_PIC);
        String publicKey = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_PUBLIC_KEY);

        if (uid == null) {
            throw new IllegalArgumentException("Initializing ChatActivity with uid null");
        }

        Contact contact = contactInterface.getContact(uid);
        if (contact != null) {
            this.contact = contact;
            this.chat = chatInterface.getChatForContact(uid);
            isTemp = false;
        } else {
            this.contact = new Contact();
            this.contact.setUid(uid);
            this.contact.setUsername(username);
            this.contact.setProfilePicTn(profilePic);
            this.contact.setPublicKey(publicKey);
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
    public void receive(MessageDTO messageDTO) {
        Message message = DatabaseMapper.getInstance().toModel(messageDTO);
    }
}
