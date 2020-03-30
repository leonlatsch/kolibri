package dev.leonlatsch.kolibri.ui.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
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

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.broker.MessageConsumer;
import dev.leonlatsch.kolibri.broker.MessageRecyclerChangeListener;
import dev.leonlatsch.kolibri.constants.Formats;
import dev.leonlatsch.kolibri.constants.Values;
import dev.leonlatsch.kolibri.database.DatabaseMapper;
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface;
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface;
import dev.leonlatsch.kolibri.database.interfaces.UserInterface;
import dev.leonlatsch.kolibri.database.model.Chat;
import dev.leonlatsch.kolibri.database.model.Contact;
import dev.leonlatsch.kolibri.database.model.Message;
import dev.leonlatsch.kolibri.database.model.MessageType;
import dev.leonlatsch.kolibri.rest.dto.Container;
import dev.leonlatsch.kolibri.rest.dto.MessageDTO;
import dev.leonlatsch.kolibri.rest.service.ChatService;
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory;
import dev.leonlatsch.kolibri.security.CryptoManager;
import dev.leonlatsch.kolibri.settings.Config;
import dev.leonlatsch.kolibri.ui.contactpage.ContactActivity;
import dev.leonlatsch.kolibri.util.Generator;
import dev.leonlatsch.kolibri.util.ImageUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The Chat Activity which mainly displays messages and sends messages to the api
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class ChatActivity extends AppCompatActivity implements MessageRecyclerChangeListener {

    /**
     * Indicates of a ChatActivity is active
     */
    public static boolean isActive;

    private Chat chat;
    private Contact contact;
    private boolean isTemp; // Indicates if this is called from the chat list or the user search

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

        MessageConsumer.setMessageRecyclerChangeListener(this);
        contactInterface = ContactInterface.getInstance();
        userInterface = UserInterface.getInstance();
        chatInterface = ChatInterface.getInstance();
        chatService = RestServiceFactory.getChatService();

        initData();
        SharedPreferences preferences = Config.getSharedPreferences(this);
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
        TextView usernameTextView = findViewById(R.id.chat_username_textview);
        ImageView profilePicImageView = findViewById(R.id.chat_profile_pic_image_view);
        ImageButton sendButton = findViewById(R.id.chat_button_send);
        sendButton.setOnClickListener(v -> onSendPressed());

        toolbar.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
            intent.putExtra(Values.INTENT_KEY_CONTACT_UID, contact.getUid());
            startActivity(intent);
        });

        messageEditText.setOnFocusChangeListener((v, b) -> {
            if (b) {
                messageRecycler.scrollToPosition(messageListAdapter.getLastPosition());
            }
        });

        if (preferences.getBoolean(Config.KEY_APP_SEND_WITH_ENTER, false)) {
            messageEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
            messageEditText.setOnEditorActionListener((v, actionId, event) -> {
                onSendPressed();
                return true;
            });
        } else {
            messageEditText.setImeOptions(EditorInfo.IME_ACTION_NONE);
        }

        Contact contact = contactInterface.getContact(chat.getUid());
        if (contact != null) {
            usernameTextView.setText(contact.getUsername());
            if (this.contact.getProfilePicTn() != null) {
                profilePicImageView.setImageBitmap(ImageUtil.createBitmap(contact.getProfilePicTn()));
            }
        } else {
            String username = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_USERNAME);
            String profilePic = (String) getIntent().getExtras().get(Values.INTENT_KEY_CHAT_PROFILE_PIC);
            usernameTextView.setText(username);
            if (profilePic != null) {
                profilePicImageView.setImageBitmap(ImageUtil.createBitmap(profilePic));
            } else {
                profilePicImageView.setImageDrawable(ImageUtil.getDefaultProfilePic(this));
            }
        }
    }

    /**
     * Called when the send button is pressed
     */
    private void onSendPressed() {
        if (!messageEditText.getText().toString().isEmpty()) {
            Message message = constructMessage();

            if (isTemp) { // If this is the first message save the temo chat and contact
                chat.setLastTimestamp(message.getTimestamp());
                chat.setLastMessage(message.getContent());
                chatInterface.saveChat(chat);
                contactInterface.save(contact);
                isTemp = false;
            }

            chat.setLastMessage(message.getContent());
            chat.setLastTimestamp(message.getTimestamp());
            chatInterface.updateChat(chat);
            MessageConsumer.notifyChatListChangedFromExternal(chat);

            // Clean up view
            messageListAdapter.add(message);
            messageEditText.setText(Values.EMPTY);
            messageRecycler.scrollToPosition(messageListAdapter.getLastPosition());
            messageEditText.requestFocus();

            MessageDTO encryptedMessage = DatabaseMapper.getInstance().toDto(message);
            encryptedMessage.setContent(CryptoManager.encryptAndEncode(encryptedMessage.getContent().getBytes(), contact.getPublicKey()));
            Call<Container<String>> call = chatService.send(userInterface.getAccessToken(), encryptedMessage);
            call.enqueue(new Callback<Container<String>>() {
                @Override
                public void onResponse(Call<Container<String>> call, Response<Container<String>> response) {
                    message.setSent(true);
                    chatInterface.saveMessage(message);
                    messageListAdapter.updateMessageStatus(message);
                }

                @Override
                public void onFailure(Call<Container<String>> call, Throwable t) {
                    message.setSent(false);
                    chatInterface.saveMessage(message);
                }
            });
        }
    }

    /**
     * Called when a new message arrives
     *
     * @param message
     */
    @Override
    public void receive(Message message) {
        if (isActive && message.getCid().equals(chat.getCid()) && !isTemp) {
            new Handler(getApplicationContext().getMainLooper()).post(() -> {
                if (messageListAdapter.isMessagePresent(message)) {
                    messageListAdapter.updateMessageStatus(message);
                } else {
                    messageListAdapter.add(message);
                    messageRecycler.scrollToPosition(messageListAdapter.getLastPosition());
                }
            }); // Invoke on main thread
        }
    }

    /**
     * Construct a message before sending it
     *
     * @return
     */
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

    /**
     * Initialize the data
     */
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
            chat = new Chat(Generator.genUUid(), this.contact.getUid(), 0, null, null);
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
    public void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }
}
