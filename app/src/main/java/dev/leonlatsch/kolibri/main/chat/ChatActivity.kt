package dev.leonlatsch.kolibri.main.chat

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import java.sql.Timestamp
import java.util.ArrayList

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.broker.MessageConsumer
import dev.leonlatsch.kolibri.broker.MessageRecyclerChangeListener
import dev.leonlatsch.kolibri.constants.Formats
import dev.leonlatsch.kolibri.constants.Values
import dev.leonlatsch.kolibri.database.DatabaseMapper
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.database.model.Chat
import dev.leonlatsch.kolibri.database.model.Contact
import dev.leonlatsch.kolibri.database.model.Message
import dev.leonlatsch.kolibri.database.model.MessageType
import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.dto.MessageDTO
import dev.leonlatsch.kolibri.rest.service.ChatService
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.security.CryptoManager
import dev.leonlatsch.kolibri.settings.Config
import dev.leonlatsch.kolibri.util.Generator
import dev.leonlatsch.kolibri.util.ImageUtil
import dev.leonlatsch.kolibri.util.empty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * The Chat Activity which mainly displays messages and sends messages to the api
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class ChatActivity : AppCompatActivity(), MessageRecyclerChangeListener {

    private var chat: Chat? = null
    private var contact: Contact? = null
    private var isTemp: Boolean = false // Indicates if this is called from the chat list or the user search

    private var messageEditText: EditText? = null
    private var messageRecycler: RecyclerView? = null

    private var messageListAdapter: MessageListAdapter? = null

    private var contactInterface: ContactInterface? = null
    private var userInterface: UserInterface? = null
    private var chatInterface: ChatInterface? = null

    private var chatService: ChatService? = null

    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbar = findViewById(R.id.chat_toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar().setDisplayShowTitleEnabled(false)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)

        MessageConsumer.setMessageRecyclerChangeListener(this)
        contactInterface = ContactInterface.getInstance()
        userInterface = UserInterface.getInstance()
        chatInterface = ChatInterface.getInstance()
        chatService = RestServiceFactory.getChatService()

        initData()
        val preferences = Config.getSharedPreferences(this)
        messageRecycler = findViewById(R.id.chat_recycler_view)

        val messageList: List<Message>
        if (!isTemp) {
            messageList = chatInterface!!.getMessagesForChat(chat!!.getCid())
        } else {
            messageList = ArrayList()
        }
        messageListAdapter = MessageListAdapter(this, messageList)
        messageRecycler!!.setLayoutManager(LinearLayoutManager(this))
        messageRecycler!!.setAdapter(messageListAdapter)

        messageEditText = findViewById(R.id.chat_edit_text)
        val usernameTextView = findViewById(R.id.chat_username_textview)
        val profilePicImageView = findViewById(R.id.chat_profile_pic_image_view)
        val sendButton = findViewById(R.id.chat_button_send)
        sendButton.setOnClickListener({ v -> onSendPressed() })

        if (preferences.getBoolean(Config.KEY_APP_SEND_WITH_ENTER, false)) {
            messageEditText!!.setImeOptions(EditorInfo.IME_ACTION_SEND)
            messageEditText!!.setOnEditorActionListener({ v, actionId, event ->
                onSendPressed()
                true
            })
        } else {
            messageEditText!!.setImeOptions(EditorInfo.IME_ACTION_NONE)
        }

        val contact = contactInterface!!.getContact(chat!!.getUid())
        if (contact != null) {
            usernameTextView.setText(contact!!.getUsername())
            if (this.contact!!.getProfilePicTn() != null) {
                profilePicImageView.setImageBitmap(ImageUtil.createBitmap(contact!!.getProfilePicTn()))
            }
        } else {
            val username = getIntent().getExtras().get(Values.INTENT_KEY_CHAT_USERNAME) as String
            val profilePic = getIntent().getExtras().get(Values.INTENT_KEY_CHAT_PROFILE_PIC) as String
            usernameTextView.setText(username)
            if (profilePic != null) {
                profilePicImageView.setImageBitmap(ImageUtil.createBitmap(profilePic))
            } else {
                profilePicImageView.setImageDrawable(ImageUtil.getDefaultProfilePic(this))
            }
        }
    }

    /**
     * Called when the send button is pressed
     */
    private fun onSendPressed() {
        if (!messageEditText!!.getText().toString().isEmpty()) {
            val message = constructMessage()

            if (isTemp) { // If this is the first message save the temo chat and contact
                chat!!.setLastTimestamp(message.getTimestamp())
                chat!!.setLastMessage(message.getContent())
                chatInterface!!.saveChat(chat)
                contactInterface!!.save(contact)
                isTemp = false
            }

            chat!!.setLastMessage(message.getContent())
            chat!!.setLastTimestamp(message.getTimestamp())
            chatInterface!!.updateChat(chat)
            MessageConsumer.notifyChatListChangedFromExternal(chat)

            // Clean up view
            messageListAdapter!!.add(message)
            messageEditText!!.setText(String.empty())
            messageRecycler!!.scrollToPosition(messageListAdapter!!.getLastPosition())
            messageEditText!!.requestFocus()

            val encryptedMessage = DatabaseMapper.getInstance().toDto(message)
            encryptedMessage.setContent(CryptoManager.encryptAndEncode(encryptedMessage.getContent().getBytes(), contact!!.getPublicKey()))
            val call = chatService!!.send(userInterface!!.getAccessToken(), encryptedMessage)
            call.enqueue(object : Callback<Container<String>>() {
                @Override
                fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                    message.setSent(true)
                    chatInterface!!.saveMessage(message)
                    messageListAdapter!!.updateMessageStatus(message)
                }

                @Override
                fun onFailure(call: Call<Container<String>>, t: Throwable) {
                    message.setSent(false)
                    chatInterface!!.saveMessage(message)
                }
            })
        }
    }

    /**
     * Called when a new message arrives
     *
     * @param message
     */
    @Override
    fun receive(message: Message) {
        if (isActive && message.getCid().equals(chat!!.getCid()) && !isTemp) {
            Handler(getApplicationContext().getMainLooper()).post({
                if (messageListAdapter!!.isMessagePresent(message)) {
                    messageListAdapter!!.updateMessageStatus(message)
                } else {
                    messageListAdapter!!.add(message)
                    messageRecycler!!.scrollToPosition(messageListAdapter!!.getLastPosition())
                }
            }) // Invoke on main thread
        }
    }

    /**
     * Construct a message before sending it
     *
     * @return
     */
    private fun constructMessage(): Message {
        val messageText = messageEditText!!.getText().toString()

        val message = Message()
        message.setCid(chat!!.getCid())
        message.setFrom(userInterface!!.getUser().getUid())
        message.setTo(contact!!.getUid())
        message.setMid(Generator.genUUid())
        message.setType(MessageType.TEXT)
        message.setTimestamp(Formats.DATE_FORMAT.format(Timestamp(System.currentTimeMillis())))
        message.setContent(messageText)
        return message
    }

    /**
     * Initialize the data
     */
    private fun initData() {
        val uid = getIntent().getExtras().get(Values.INTENT_KEY_CHAT_UID) as String
        val username = getIntent().getExtras().get(Values.INTENT_KEY_CHAT_USERNAME) as String
        val profilePic = getIntent().getExtras().get(Values.INTENT_KEY_CHAT_PROFILE_PIC) as String
        val publicKey = getIntent().getExtras().get(Values.INTENT_KEY_CHAT_PUBLIC_KEY) as String

        if (uid == null) {
            throw IllegalArgumentException("Initializing ChatActivity with uid null")
        }

        val contact = contactInterface!!.getContact(uid)
        if (contact != null) {
            this.contact = contact
            this.chat = chatInterface!!.getChatForContact(uid)
            isTemp = false
        } else {
            this.contact = Contact()
            this.contact!!.setUid(uid)
            this.contact!!.setUsername(username)
            this.contact!!.setProfilePicTn(profilePic)
            this.contact!!.setPublicKey(publicKey)
            chat = Chat(Generator.genUUid(), this.contact!!.getUid(), 0, null, null)
            isTemp = true
        }
    }

    @Override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            onBackPressed()
            finish()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    @Override
    fun onStart() {
        super.onStart()
        isActive = true
    }

    @Override
    fun onStop() {
        super.onStop()
        isActive = false
    }

    companion object {

        /**
         * Indicates of a ChatActivity is active
         */
        var isActive: Boolean = false
    }
}
