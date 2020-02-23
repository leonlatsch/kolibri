package dev.leonlatsch.kolibri.main.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.broker.ChatListChangeListener
import dev.leonlatsch.kolibri.broker.MessageConsumer
import dev.leonlatsch.kolibri.constants.Values
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.model.Chat
import dev.leonlatsch.kolibri.main.MainActivity
import dev.leonlatsch.kolibri.main.UserSearchActivity
import dev.leonlatsch.kolibri.main.adapter.ChatListAdapter
import dev.leonlatsch.kolibri.main.chat.ChatActivity

/**
 * Fragment to show a list of chats
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class ChatFragment : Fragment(), ChatListChangeListener {

    private var parent: MainActivity? = null
    private var listView: ListView? = null
    private var hintTextView: TextView? = null
    private var chatListAdapter: ChatListAdapter? = null
    private var chatList: List<Chat>? = null

    private val itemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        val chat = listView!!.getItemAtPosition(position)
        if (chat is Chat) {
            val intent = Intent(this.parent!!.applicationContext, ChatActivity::class.java)
            intent.putExtra(Values.INTENT_KEY_CHAT_UID, chat.uid)
            startActivity(intent)
            if (chat.unreadMessages > 0) {
                chat.unreadMessages = 0
                ChatInterface.updateChat(chat)
                chatListAdapter!!.chatChanged(chat)
            }
        }
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        parent = activity as MainActivity

        MessageConsumer.setChatListChangeListener(this)

        listView = view!!.findViewById(R.id.fragment_chat_list_view)
        hintTextView = view.findViewById(R.id.fragment_chat_hint)

        listView!!.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        listView!!.setMultiChoiceModeListener(MultiSelectHandler())

        chatList = ChatInterface.all
        setChatListVisible(chatList!!.isNotEmpty())

        chatListAdapter = ChatListAdapter(parent!!, chatList as MutableList<Chat>)
        listView!!.adapter = chatListAdapter
        listView!!.onItemClickListener = itemClickListener

        val newChatFab = view.findViewById<FloatingActionButton>(R.id.newChatFab)
        newChatFab.setOnClickListener { newChat() }

        return view
    }

    private fun newChat() {
        val intent = Intent(parent!!.applicationContext, UserSearchActivity::class.java)
        startActivity(intent)
    }

    private fun setChatListVisible(visible: Boolean) {
        if (visible) {
            hintTextView!!.visibility = View.GONE
            listView!!.visibility = View.VISIBLE
        } else {
            hintTextView!!.visibility = View.VISIBLE
            listView!!.visibility = View.GONE
        }
    }

    /**
     * Called when a chat has changed.
     * Eg. when a new message arrives or a profile picture has changed.
     *
     * @param chat
     */
    override fun chatChanged(chat: Chat) {
        if (!chatListAdapter!!.isChatPresent(chat)) {
            Handler(parent!!.applicationContext.mainLooper).post {
                chatListAdapter!!.add(chat)
                setChatListVisible(true)
            } // Invoke in main thread
        } else {
            Handler(parent!!.applicationContext.mainLooper).post { chatListAdapter!!.chatChanged(chat) } // Invoke on main thread
        }
    }

    /**
     * Private class to handle multi selection
     *
     * @author Leon Latsch
     * @since 1.0.0
     */
    private inner class MultiSelectHandler : AbsListView.MultiChoiceModeListener {

        override fun onItemCheckedStateChanged(actionMode: ActionMode, i: Int, l: Long, b: Boolean) {
            chatListAdapter!!.toggleSelection(i)
        }

        override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            actionMode.menuInflater.inflate(R.menu.menu_chats_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
            if (menuItem.itemId == R.id.menu_chats_delete) {
                val onClickListener = DialogInterface.OnClickListener { _, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        for (i in chatListAdapter!!.getSelectedItems()) {
                            ContactInterface.delete(chatListAdapter!!.getItem(i)?.uid!!)
                            ChatInterface.deleteChat(chatListAdapter!!.getItem(i)?.cid!!)
                        }
                        chatListAdapter!!.deleteSelectedItems()

                        actionMode.finish()
                        setChatListVisible(chatList!!.isNotEmpty())
                    }
                }

                val builder = AlertDialog.Builder(parent, R.style.AlertDialogCustom)
                builder.setMessage(getString(R.string.are_you_sure_delete_chats))
                        .setPositiveButton(getString(R.string.yes), onClickListener)
                        .setNegativeButton(getString(R.string.no), onClickListener)
                        .show()
            }
            return false
        }

        override fun onDestroyActionMode(actionMode: ActionMode) {
            chatListAdapter!!.removeSelections()
        }
    }
}
