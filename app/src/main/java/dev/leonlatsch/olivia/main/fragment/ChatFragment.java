package dev.leonlatsch.olivia.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.broker.ChatListChangeListener;
import dev.leonlatsch.olivia.broker.MessageConsumer;
import dev.leonlatsch.olivia.chat.ChatActivity;
import dev.leonlatsch.olivia.constants.Values;
import dev.leonlatsch.olivia.database.interfaces.ChatInterface;
import dev.leonlatsch.olivia.database.model.Chat;
import dev.leonlatsch.olivia.main.MainActivity;
import dev.leonlatsch.olivia.main.UserSearchActivity;
import dev.leonlatsch.olivia.main.adapter.ChatListAdapter;

/**
 * Fragment to show a list of chats
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
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
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        parent = (MainActivity) getActivity();

        chatInterface = ChatInterface.getInstance();
        MessageConsumer.setChatListChangeListener(this);

        listView = view.findViewById(R.id.fragment_chat_list_view);
        hintTextView = view.findViewById(R.id.fragment_chat_hint);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiSelectHandler());

        List<Chat> chatList = chatInterface.getALl();
        setChatListVisible(!chatList.isEmpty());

        chatListAdapter = new ChatListAdapter(parent, chatList);
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

    private void setChatListVisible(boolean visible) {
        if (visible) {
            hintTextView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            hintTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    /**
     * Called when a chat has changed.
     * Eg. when a new message arrives or a profile picture has changed.
     *
     * @param chat
     */
    @Override
    public void chatChanged(Chat chat) {
        if (!chatListAdapter.isChatPresent(chat)) {
            new Handler(parent.getApplicationContext().getMainLooper()).post(() -> {
                chatListAdapter.add(chat);
                setChatListVisible(true);
            }); // Invoke in main thread
        } else {
            new Handler(parent.getApplicationContext().getMainLooper()).post(() -> chatListAdapter.chatChanged(chat)); // Invoke on main thread
        }
    }

    /**
     * Private class to handle multi selection
     *
     * @author Leon Latsch
     * @since 1.0.0
     */
    private class MultiSelectHandler implements AbsListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            parent.onCreateOptionsMenu(menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    }
}
