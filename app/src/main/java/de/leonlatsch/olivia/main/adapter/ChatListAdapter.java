package de.leonlatsch.olivia.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import de.leonlatsch.olivia.database.interfaces.ContactInterface;
import de.leonlatsch.olivia.database.model.Chat;

public class ChatListAdapter extends ArrayAdapter<Chat> {

    private List<Chat> dataset;
    private Context context;
    private ContactInterface contactInterface;

    private static class ViewHolder {
        ImageView imageView;
        TextView usernameTextView;
        TextView lastMessageTextView;
    }

    public ChatListAdapter(@NonNull Context context, List<Chat> contactList) {
        super(context, 0, contactList);
        this.dataset = contactList;
        this.context = context;
        this.contactInterface = ContactInterface.getInstance();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null; //TODO
    }
}
