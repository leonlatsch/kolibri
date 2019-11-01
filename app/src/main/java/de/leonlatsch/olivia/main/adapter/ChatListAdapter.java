package de.leonlatsch.olivia.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.database.interfaces.ContactInterface;
import de.leonlatsch.olivia.database.model.Chat;
import de.leonlatsch.olivia.database.model.Contact;
import de.leonlatsch.olivia.util.ImageUtil;

public class ChatListAdapter extends ArrayAdapter<Chat> {

    private List<Chat> dataset;
    private Context context;
    private ContactInterface contactInterface;

    private static class ViewHolder {
        ImageView imageView;
        TextView usernameTextView;
        TextView lastMessageTextView;
        TextView lastDateTextView;
    }

    public ChatListAdapter(@NonNull Context context, List<Chat> contactList) {
        super(context, 0, contactList);
        this.dataset = contactList;
        this.context = context;
        this.contactInterface = ContactInterface.getInstance();
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contactInterface.getContact(chat.getContactId());
        if (contact.getProfilePicTn() != null) {
            viewHolder.imageView.setImageBitmap(ImageUtil.createBitmap(contact.getProfilePicTn()));
        } else {
            viewHolder.imageView.setImageDrawable(ImageUtil.getDefaultProfilePicTn(context));
        }
        viewHolder.usernameTextView.setText(contact.getUsername());
        viewHolder.lastMessageTextView.setText("Ersetzen mit letzter NAchricht!!!"); //TODO: set last message as text
        viewHolder.lastDateTextView.setText("21.12.2019"); // TODO: Ersetzen mit datum letzter nachricht

        return convertView;
    }
}
