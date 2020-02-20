package dev.leonlatsch.kolibri.main.chat

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
import java.lang.IllegalArgumentException
import java.sql.Timestamp

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

    private var chatService: ChatService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbar = findViewById<Toolbar>(R.id.chat_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MessageConsumer.setMessageRecyclerChangeListener(this)
        chatService = RestServiceFactory.getChatService()

        initData()
        val preferences = Config.getSharedPreferences(this)
        messageRecycler = findViewById(R.id.chat_recycler_view)

        val messageList: MutableList<Message> = if (!isTemp) {
            ChatInterface.getMessagesForChat(chat!!.cid!!)
        } else {
            mutableListOf()
        }
        messageListAdapter = MessageListAdapter(this, messageList)
        messageRecycler!!.layoutManager = LinearLayoutManager(this)
        messageRecycler!!.adapter = messageListAdapter

        messageEditText = findViewById(R.id.chat_edit_text)
        val usernameTextView = findViewById<TextView>(R.id.chat_username_textview)
        val profilePicImageView = findViewById<ImageView>(R.id.chat_profile_pic_image_view)
        val sendButton = findViewById<ImageButton>(R.id.chat_button_send)
        sendButton.setOnClickListener { onSendPressed() }

        if (preferences.getBoolean(Config.KEY_APP_SEND_WITH_ENTER, false)) {
            messageEditText!!.imeOptions = EditorInfo.IME_ACTION_SEND
            messageEditText!!.setOnEditorActionListener { _, _, _ ->
                onSendPressed()
                true
            }
        } else {
            messageEditText!!.imeOptions = EditorInfo.IME_ACTION_NONE
        }

        val contact = ContactInterface.getContact(chat!!.uid!!)
        if (contact != null) {
            usernameTextView.text = contact.username
            if (this.contact!!.profilePicTn != null) {
                profilePicImageView.setImageBitmap(ImageUtil.createBitmap(contact.profilePicTn))
            }
        }
    }

    /**
     * Called when the send button is pressed
     */
    private fun onSendPressed() {
        if (messageEditText!!.text.toString().isNotEmpty()) {
            val message = constructMessage()

            if (isTemp) { // If this is the first message save the item chat and contact
                chat!!.lastTimestamp = message.timestamp
                chat!!.lastMessage = message.content
                ChatInterface.saveChat(chat!!)
                ContactInterface.save(contact)
                isTemp = false
            }

            chat!!.lastMessage = message.content
            chat!!.lastTimestamp = message.timestamp
            ChatInterface.updateChat(chat!!)
            MessageConsumer.notifyChatListChanged(chat!!)

            // Clean up view
            messageListAdapter!!.add(message)
            messageEditText!!.setText(String.empty())
            messageRecycler!!.scrollToPosition(messageListAdapter!!.lastPosition)
            messageEditText!!.requestFocus()

            val encryptedMessage = DatabaseMapper.toDto(message)
            encryptedMessage?.content = (CryptoManager.encryptAndEncode(encryptedMessage?.content!!.toByteArray(), contact!!.publicKey!!))
            val call = chatService!!.send(UserInterface.accessToken!!, encryptedMessage)
            call.enqueue(object : Callback<Container<String>> {
                override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                    message.isSent = true
                    ChatInterface.saveMessage(message)
                    messageListAdapter!!.updateMessageStatus(message)
                }

                override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                    message.isSent = false
                    ChatInterface.saveChat(chat!!)
                }
            })
        }
    }

    /**
     * Called when a new message arrives
     *
     * @param message
     */
    override fun receive(message: Message) {
        if (isActive && message.cid.equals(chat!!.cid) && !isTemp) {
            Handler(applicationContext.mainLooper).post {
                if (messageListAdapter!!.isMessagePresent(message)) {
                    messageListAdapter!!.updateMessageStatus(message)
                } else {
                    messageListAdapter!!.add(message)
                    messageRecycler!!.scrollToPosition(messageListAdapter!!.lastPosition)
                }
            } // Invoke on main thread
        }
    }

    /**
     * Construct a message before sending it
     *
     * @return
     */
    private fun constructMessage(): Message {
        val messageText = messageEditText!!.text.toString()

        val message = Message()
        message.cid = chat!!.cid
        message.from = UserInterface.user?.uid
        message.to = contact!!.uid
        message.mid = Generator.genUUid()
        message.type = MessageType.TEXT
        message.timestamp = Formats.DATE_FORMAT.format(Timestamp(System.currentTimeMillis()))
        message.content = messageText
        return message
    }

    /**
     * Initialize the data
     */
    private fun initData() {
        val uid = intent.extras?.get(Values.INTENT_KEY_CHAT_UID) as String?
        val username = intent.extras?.get(Values.INTENT_KEY_CHAT_USERNAME) as String?
        val profilePic = intent.extras?.get(Values.INTENT_KEY_CHAT_PROFILE_PIC) as String?
        val publicKey = intent.extras?.get(Values.INTENT_KEY_CHAT_PUBLIC_KEY) as String?

        requireNotNull(uid) { "ChatActivity must be initialized with a uid" }

        val contact = ContactInterface.getContact(uid)
        if (contact != null) {
            this.contact = contact
            this.chat = ChatInterface.getChatForContact(uid)
            isTemp = false
        } else {
            this.contact = Contact(uid, username, profilePic, publicKey)
            this.chat = Chat(Generator.genUUid(), this.contact!!.uid, 0, null, null)
            isTemp = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    override fun onStop() {
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
