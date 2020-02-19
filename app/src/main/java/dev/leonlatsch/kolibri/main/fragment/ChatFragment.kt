package dev.leonlatsch.kolibri.main.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
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
    private var view: View? = null
    private var listView: ListView? = null
    private var hintTextView: TextView? = null
    private var chatListAdapter: ChatListAdapter? = null
    private var chatList: List<Chat>? = null

    private var chatInterface: ChatInterface? = null
    private var contactInterface: ContactInterface? = null
    private val itemClickListener = { parent, view, position, id ->
        val raw = listView!!.getItemAtPosition(position)
        if (raw is Chat) {
            val chat = raw as Chat
            val intent = Intent(this.parent!!.getApplicationContext(), ChatActivity::class.java)
            intent.putExtra(Values.INTENT_KEY_CHAT_UID, chat.getUid())
            startActivity(intent)
            if (chat.getUnreadMessages() > 0) {
                chat.setUnreadMessages(0)
                chatInterface!!.updateChat(chat)
                chatListAdapter!!.chatChanged(chat)
            }
        }
    }

    @Nullable
    @Override
    fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup, @Nullable savedInstanceState: Bundle): View {
        view = inflater.inflate(R.layout.fragment_chats, container, false)
        parent = getActivity() as MainActivity

        chatInterface = ChatInterface.getInstance()
        contactInterface = ContactInterface.getInstance()
        MessageConsumer.setChatListChangeListener(this)

        listView = view!!.findViewById(R.id.fragment_chat_list_view)
        hintTextView = view!!.findViewById(R.id.fragment_chat_hint)

        listView!!.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL)
        listView!!.setMultiChoiceModeListener(MultiSelectHandler())

        chatList = chatInterface!!.getALl()
        setChatListVisible(!chatList!!.isEmpty())

        chatListAdapter = ChatListAdapter(parent, chatList)
        listView!!.setAdapter(chatListAdapter)
        listView!!.setOnItemClickListener(itemClickListener)

        val newChatFab = view!!.findViewById(R.id.newChatFab)
        newChatFab.setOnClickListener({ v -> newChat() })

        return view
    }

    private fun newChat() {
        val intent = Intent(parent!!.getApplicationContext(), UserSearchActivity::class.java)
        startActivity(intent)
    }

    private fun setChatListVisible(visible: Boolean) {
        if (visible) {
            hintTextView!!.setVisibility(View.GONE)
            listView!!.setVisibility(View.VISIBLE)
        } else {
            hintTextView!!.setVisibility(View.VISIBLE)
            listView!!.setVisibility(View.GONE)
        }
    }

    /**
     * Called when a chat has changed.
     * Eg. when a new message arrives or a profile picture has changed.
     *
     * @param chat
     */
    @Override
    fun chatChanged(chat: Chat) {
        if (!chatListAdapter!!.isChatPresent(chat)) {
            Handler(parent!!.getApplicationContext().getMainLooper()).post({
                chatListAdapter!!.add(chat)
                setChatListVisible(true)
            }) // Invoke in main thread
        } else {
            Handler(parent!!.getApplicationContext().getMainLooper()).post({ chatListAdapter!!.chatChanged(chat) }) // Invoke on main thread
        }
    }

    /**
     * Private class to handle multi selection
     *
     * @author Leon Latsch
     * @since 1.0.0
     */
    private inner class MultiSelectHandler : AbsListView.MultiChoiceModeListener {

        @Override
        fun onItemCheckedStateChanged(actionMode: ActionMode, i: Int, l: Long, b: Boolean) {
            chatListAdapter!!.toggleSelection(i)
        }

        @Override
        fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            actionMode.getMenuInflater().inflate(R.menu.menu_chats_action_mode, menu)
            return true
        }

        @Override
        fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
            return true
        }

        @Override
        fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
            if (menuItem.getItemId() === R.id.menu_chats_delete) {
                val onClickListener = { dialog, which ->
                    if (which === DialogInterface.BUTTON_POSITIVE) {
                        for (i in chatListAdapter!!.getSelectedItems()) {
                            contactInterface!!.delete(chatListAdapter!!.getItem(i).getUid())
                            chatInterface!!.deleteChat(chatListAdapter!!.getItem(i).getCid())
                        }
                        chatListAdapter!!.deleteSelectedItems()

                        actionMode.finish()
                        setChatListVisible(!chatList!!.isEmpty())
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

        @Override
        fun onDestroyActionMode(actionMode: ActionMode) {
            chatListAdapter!!.removeSelections()
        }
    }
}
