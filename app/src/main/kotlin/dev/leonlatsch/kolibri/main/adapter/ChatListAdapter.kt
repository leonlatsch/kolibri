package dev.leonlatsch.kolibri.main.adapter

import android.content.Context
import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.constants.Formats
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.model.Chat
import dev.leonlatsch.kolibri.util.ImageUtil
import dev.leonlatsch.kolibri.util.empty
import java.text.ParseException
import java.util.Collections
import kotlin.Boolean
import kotlin.Comparator
import kotlin.Int
import kotlin.String

/**
 * This Adapter is used to display the chat list in the [dev.leonlatsch.kolibri.main.fragment.ChatFragment]
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class ChatListAdapter(@NonNull private val mContext: Context, private val dataset: MutableList<Chat>) : ArrayAdapter<Chat>(mContext, 0, dataset) {
    private var selectedItems: SparseBooleanArray? = null

    /**
     * Comparator to sort the chats after teh last received message
     */
    private val chatComparator = Comparator<Chat> { chat1: Chat, chat2: Chat ->
        try {
            val obj1Date = Formats.DATE_FORMAT.parse(chat1.lastTimestamp!!)
            val obj2Date = Formats.DATE_FORMAT.parse(chat2.lastTimestamp!!)
            obj2Date!!.compareTo(obj1Date)
        } catch (e: ParseException) {
            0
        }
    }

    init {
        Collections.sort(dataset, chatComparator)
        selectedItems = SparseBooleanArray()
    }

    /**
     * Check if a Chat is already present in the dataset
     *
     * @param chat
     * @return
     */
    fun isChatPresent(chat: Chat): Boolean {
        for (data in dataset) {
            if (data.cid.equals(chat.cid) || data.uid.equals(chat.uid)) {
                return true
            }
        }
        return false
    }

    fun chatChanged(chat: Chat) {
        for (i in 0 until dataset.size) {
            if (dataset[i].cid.equals(chat.cid)) {
                dataset[i] = chat
            }
        }
        Collections.sort(dataset, chatComparator)
        notifyDataSetChanged()
    }

    /**
     * Add a chat and sort the list afterwords
     *
     * @param chat
     */
    @Override
    fun add(chat: Chat) {
        super.add(chat)
        Collections.sort(dataset, chatComparator)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val chat = getItem(position)
        val viewHolder: ViewHolder

        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)
            viewHolder.imageView = view!!.findViewById<CardView>(R.id.item_chat_list_card_view).findViewById(R.id.item_chat_list_image_view)
            viewHolder.usernameTextView = view.findViewById(R.id.item_chat_list_username)
            viewHolder.lastMessageTextView = view.findViewById(R.id.item_chat_list_last_message)
            viewHolder.lastDateTextView = view.findViewById(R.id.item_chat_list_last_date)
            viewHolder.unreadMessagesTextView = view.findViewById(R.id.item_chat_list_unread_messages)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val contact = ContactInterface.getContact(chat?.uid!!)

        if (contact?.profilePicTn != null) {
            viewHolder.imageView!!.setImageBitmap(ImageUtil.createBitmap(contact.profilePicTn))
        } else {
            viewHolder.imageView!!.setImageDrawable(ImageUtil.getDefaultProfilePicTn(mContext))
        }

        viewHolder.usernameTextView!!.text = contact?.username
        viewHolder.lastMessageTextView!!.text = chat.lastMessage
        viewHolder.lastDateTextView!!.text = chat.lastTimestamp?.substring(11, 16)

        if (chat.unreadMessages > 0) {
            viewHolder.unreadMessagesTextView!!.visibility = View.VISIBLE
            viewHolder.unreadMessagesTextView!!.text = chat.unreadMessages.toString()
        } else {
            viewHolder.unreadMessagesTextView!!.text = String.empty()
            viewHolder.unreadMessagesTextView!!.visibility = View.GONE
        }

        if (selectedItems!!.get(position)) {
            view.setBackgroundColor(Color.LTGRAY)
        } else {
            view.setBackgroundColor(Color.WHITE)
        }

        return view
    }

    fun deleteSelectedItems() {
        for (i in 0 until dataset.size) {
            if (selectedItems!!.get(i)) {
                dataset.removeAt(i)
            }
        }
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<Int> {
        val items = mutableListOf<Int>()
        for (i in 0 until dataset.size) {
            if (selectedItems!!.get(i)) {
                items.add(i)
            }
        }
        return items
    }

    fun toggleSelection(position: Int) {
        selectView(position, !selectedItems!!.get(position))
    }

    fun removeSelections() {
        selectedItems = SparseBooleanArray()
        notifyDataSetChanged()
    }

    private fun selectView(position: Int, value: Boolean) {
        if (value) {
            selectedItems!!.put(position, true)
        } else {
            selectedItems!!.delete(position)
        }
        notifyDataSetChanged()
    }

    private class ViewHolder {
        internal var imageView: ImageView? = null
        internal var usernameTextView: TextView? = null
        internal var lastMessageTextView: TextView? = null
        internal var lastDateTextView: TextView? = null
        internal var unreadMessagesTextView: TextView? = null
    }
}
