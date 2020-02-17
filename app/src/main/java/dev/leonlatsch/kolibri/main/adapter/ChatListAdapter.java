package dev.leonlatsch.kolibri.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.constants.Formats;
import dev.leonlatsch.kolibri.constants.Values;
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface;
import dev.leonlatsch.kolibri.database.model.Chat;
import dev.leonlatsch.kolibri.database.model.Contact;
import dev.leonlatsch.kolibri.util.ImageUtil;

/**
 * This Adapter is used to display the chat list in the {@link dev.leonlatsch.kolibri.main.fragment.ChatFragment}
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class ChatListAdapter extends ArrayAdapter<Chat> {

    /**
     * Comparator to sort the chats after teh last received message
     */
    private static Comparator<Chat> chatComparator = (obj1, obj2) -> {
        try {
            Date obj1Date = Formats.DATE_FORMAT.parse(obj1.getLastTimestamp());
            Date obj2Date = Formats.DATE_FORMAT.parse(obj2.getLastTimestamp());
            return obj2Date.compareTo(obj1Date);
        } catch (ParseException e) {
            return 0;
        }
    };
    private List<Chat> dataset;
    private Context mContext;
    private ContactInterface contactInterface;
    private SparseBooleanArray selectedItems;

    public ChatListAdapter(@NonNull Context context, List<Chat> contactList) {
        super(context, 0, contactList);
        Collections.sort(contactList, chatComparator);
        this.dataset = contactList;
        this.mContext = context;
        this.contactInterface = ContactInterface.getInstance();
        selectedItems = new SparseBooleanArray();
    }

    /**
     * Check if a Chat is already present in the dataset
     *
     * @param chat
     * @return
     */
    public boolean isChatPresent(Chat chat) {
        for (Chat data : dataset) {
            if (data.getCid().equals(chat.getCid()) || data.getUid().equals(chat.getUid())) {
                return true;
            }
        }
        return false;
    }

    public void chatChanged(Chat chat) {
        for (int i = 0; i < dataset.size(); i++) {
            if (dataset.get(i).getCid().equals(chat.getCid())) {
                dataset.set(i, chat);
            }
        }
        Collections.sort(dataset, chatComparator);
        notifyDataSetChanged();
    }

    /**
     * Add a chat and sort the list afterwords
     *
     * @param chat
     */
    @Override
    public void add(Chat chat) {
        super.add(chat);
        Collections.sort(dataset, chatComparator);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat, parent, false);
            viewHolder.imageView = convertView.findViewById(R.id.item_chat_list_card_view).findViewById(R.id.item_chat_list_image_view);
            viewHolder.usernameTextView = convertView.findViewById(R.id.item_chat_list_username);
            viewHolder.lastMessageTextView = convertView.findViewById(R.id.item_chat_list_last_message);
            viewHolder.lastDateTextView = convertView.findViewById(R.id.item_chat_list_last_date);
            viewHolder.unreadMessagesTextView = convertView.findViewById(R.id.item_chat_list_unread_messages);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contactInterface.getContact(chat.getUid());

        if (contact.getProfilePicTn() != null) {
            viewHolder.imageView.setImageBitmap(ImageUtil.createBitmap(contact.getProfilePicTn()));
        } else {
            viewHolder.imageView.setImageDrawable(ImageUtil.getDefaultProfilePicTn(mContext));
        }

        viewHolder.usernameTextView.setText(contact.getUsername());
        viewHolder.lastMessageTextView.setText(chat.getLastMessage());
        viewHolder.lastDateTextView.setText(chat.getLastTimestamp().substring(11, 16));

        if (chat.getUnreadMessages() > 0) {
            viewHolder.unreadMessagesTextView.setVisibility(View.VISIBLE);
            viewHolder.unreadMessagesTextView.setText(String.valueOf(chat.getUnreadMessages()));
        } else {
            viewHolder.unreadMessagesTextView.setText(Values.EMPTY);
            viewHolder.unreadMessagesTextView.setVisibility(View.GONE);
        }

        if (selectedItems.get(position)) {
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    public void deleteSelectedItems() {
        for (int i = 0; i < dataset.size(); i++) {
            if (selectedItems.get(i)) {
                dataset.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            if (selectedItems.get(i)) {
                items.add(i);
            }
        }
        return items;
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItems.get(position));
    }

    public void removeSelections() {
        selectedItems = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value) {
        if (value) {
            selectedItems.put(position, true);
        } else {
            selectedItems.delete(position);
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView usernameTextView;
        TextView lastMessageTextView;
        TextView lastDateTextView;
        TextView unreadMessagesTextView;
    }
}
