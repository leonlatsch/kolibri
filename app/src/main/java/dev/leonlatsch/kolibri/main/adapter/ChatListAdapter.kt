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

import java.text.ParseException
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.Date

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.constants.Formats
import dev.leonlatsch.kolibri.constants.Values
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.model.Chat
import dev.leonlatsch.kolibri.database.model.Contact
import dev.leonlatsch.kolibri.util.ImageUtil

/**
 * This Adapter is used to display the chat list in the [dev.leonlatsch.kolibri.main.fragment.ChatFragment]
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class ChatListAdapter(@param:NonNull private val mContext: Context, private val dataset: List<Chat>) : ArrayAdapter<Chat>(mContext, 0, dataset) {
    private val contactInterface: ContactInterface
    private var selectedItems: SparseBooleanArray? = null

    init {
        Collections.sort(dataset, chatComparator)
        this.contactInterface = ContactInterface.getInstance()
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
            if (data.getCid().equals(chat.getCid()) || data.getUid().equals(chat.getUid())) {
                return true
            }
        }
        return false
    }

    fun chatChanged(chat: Chat) {
        for (i in 0 until dataset.size()) {
            if (dataset[i].getCid().equals(chat.getCid())) {
                dataset.set(i, chat)
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

    @Override
    fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val chat = getItem(position)
        val viewHolder: ViewHolder

        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat, parent, false)
            viewHolder.imageView = convertView!!.findViewById(R.id.item_chat_list_card_view).findViewById(R.id.item_chat_list_image_view)
            viewHolder.usernameTextView = convertView!!.findViewById(R.id.item_chat_list_username)
            viewHolder.lastMessageTextView = convertView!!.findViewById(R.id.item_chat_list_last_message)
            viewHolder.lastDateTextView = convertView!!.findViewById(R.id.item_chat_list_last_date)
            viewHolder.unreadMessagesTextView = convertView!!.findViewById(R.id.item_chat_list_unread_messages)
            convertView!!.setTag(viewHolder)
        } else {
            viewHolder = convertView!!.getTag()
        }

        val contact = contactInterface.getContact(chat.getUid())

        if (contact.getProfilePicTn() != null) {
            viewHolder.imageView!!.setImageBitmap(ImageUtil.createBitmap(contact.getProfilePicTn()))
        } else {
            viewHolder.imageView!!.setImageDrawable(ImageUtil.getDefaultProfilePicTn(mContext))
        }

        viewHolder.usernameTextView!!.setText(contact.getUsername())
        viewHolder.lastMessageTextView!!.setText(chat.getLastMessage())
        viewHolder.lastDateTextView!!.setText(chat.getLastTimestamp().substring(11, 16))

        if (chat.getUnreadMessages() > 0) {
            viewHolder.unreadMessagesTextView!!.setVisibility(View.VISIBLE)
            viewHolder.unreadMessagesTextView!!.setText(String.valueOf(chat.getUnreadMessages()))
        } else {
            viewHolder.unreadMessagesTextView!!.setText(Values.EMPTY)
            viewHolder.unreadMessagesTextView!!.setVisibility(View.GONE)
        }

        if (selectedItems!!.get(position)) {
            convertView!!.setBackgroundColor(Color.LTGRAY)
        } else {
            convertView!!.setBackgroundColor(Color.WHITE)
        }

        return convertView
    }

    fun deleteSelectedItems() {
        for (i in 0 until dataset.size()) {
            if (selectedItems!!.get(i)) {
                dataset.remove(i)
            }
        }
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<Integer> {
        val items = ArrayList()
        for (i in 0 until dataset.size()) {
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

    companion object {

        /**
         * Comparator to sort the chats after teh last received message
         */
        private val chatComparator = { obj1, obj2 ->
            try {
                val obj1Date = Formats.DATE_FORMAT.parse(obj1.getLastTimestamp())
                val obj2Date = Formats.DATE_FORMAT.parse(obj2.getLastTimestamp())
                return obj2Date.compareTo(obj1Date)
            } catch (e: ParseException) {
                return 0
            }
        }
    }
}
