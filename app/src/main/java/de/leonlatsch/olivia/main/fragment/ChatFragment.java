package de.leonlatsch.olivia.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.broker.ChatListChangeListener;
import de.leonlatsch.olivia.broker.MessageConsumer;
import de.leonlatsch.olivia.chat.ChatActivity;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.database.interfaces.ChatInterface;
import de.leonlatsch.olivia.database.model.Chat;
import de.leonlatsch.olivia.main.MainActivity;
import de.leonlatsch.olivia.main.UserSearchActivity;
import de.leonlatsch.olivia.main.adapter.ChatListAdapter;

public class ChatFragment extends Fragment implements ChatListChangeListener {

    private MainActivity parent;
    private View view;
    private ListView listView;
    private TextView hintTextView;
    private ChatListAdapter chatListAdapter;

    private ChatInterface chatInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_chats, container, false);
        parent = (MainActivity) getActivity();

        chatInterface = ChatInterface.getInstance();
        MessageConsumer.setChatListChangeListener(this);

        listView = view.findViewById(R.id.fragment_chat_list_view);
        hintTextView = view.findViewById(R.id.fragment_chat_hint);
        chatListAdapter = new ChatListAdapter(parent, chatInterface.getALl());
        listView.setAdapter(chatListAdapter);
        listView.setOnItemClickListener(itemClickListener);

        FloatingActionButton newChatFab = view.findViewById(R.id.newChatFab);
        newChatFab.setOnClickListener(v -> newChat());

        return view;
    }

    private AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {
        Object raw = listView.getItemAtPosition(position);
        if (raw instanceof Chat) {
            Chat chat = (Chat) raw;
            Intent intent = new Intent(this.parent.getApplicationContext(), ChatActivity.class);
            intent.putExtra(Values.INTENT_KEY_CHAT_UID, chat.getUid());
            startActivity(intent);
            if (chat.getUnreadMessages() > 0) {
                chat.setUnreadMessages(0);
                chatInterface.updateChat(chat);
                chatListAdapter.chatChanged(chat);
            }
        }
    };

    private void newChat() {
        Intent intent = new Intent(parent.getApplicationContext(), UserSearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void addChat(Chat chat) {
        if (!chatListAdapter.chatIsPresent(chat)) {
            new Handler(parent.getApplicationContext().getMainLooper()).post(() -> chatListAdapter.add(chat)); // Invoke in main thread
        } else {
            new Handler(parent.getApplicationContext().getMainLooper()).post(() -> chatListAdapter.chatChanged(chat)); // Invoke on main thread
        }
    }
}
