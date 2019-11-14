package de.leonlatsch.olivia.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.broker.MessageConsumer;
import de.leonlatsch.olivia.broker.MessageListener;
import de.leonlatsch.olivia.chat.ChatActivity;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.database.DatabaseMapper;
import de.leonlatsch.olivia.database.interfaces.ChatInterface;
import de.leonlatsch.olivia.database.interfaces.ContactInterface;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.database.model.Chat;
import de.leonlatsch.olivia.database.model.Contact;
import de.leonlatsch.olivia.database.model.Message;
import de.leonlatsch.olivia.main.MainActivity;
import de.leonlatsch.olivia.main.UserSearchActivity;
import de.leonlatsch.olivia.main.adapter.ChatListAdapter;
import de.leonlatsch.olivia.rest.dto.Container;
import de.leonlatsch.olivia.rest.dto.MessageDTO;
import de.leonlatsch.olivia.rest.dto.UserDTO;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import retrofit2.Call;
import retrofit2.Response;

public class ChatFragment extends Fragment implements MessageListener {

    private MainActivity parent;
    private View view;
    private ListView listView;
    private ChatListAdapter chatListAdapter;

    private UserInterface userInterface;
    private ChatInterface chatInterface;
    private ContactInterface contactInterface;
    private UserService userService;

    private DatabaseMapper databaseMapper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_chats, container, false);
        parent = (MainActivity) getActivity();
        MessageConsumer.addMessageListener(this);

        userInterface = UserInterface.getInstance();
        chatInterface = ChatInterface.getInstance();
        contactInterface = ContactInterface.getInstance();
        userService = RestServiceFactory.getUserService();

        databaseMapper = DatabaseMapper.getInstance();

        List<Chat> vea = chatInterface.getALl();
        listView = view.findViewById(R.id.fragment_chat_list_view);
        reload();
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
        }
    };

    @Override
    public void receive(MessageDTO messageDTO) {
        if (!ChatActivity.isActive) {
            Message message = DatabaseMapper.getInstance().toModel(messageDTO);
            final Call<Container<UserDTO>> userCall = userService.get(userInterface.getAccessToken(), messageDTO.getFrom());
            final Call<Container<String>> publicKeyCall = userService.getPublicKey(userInterface.getAccessToken(), message.getFrom());
            new Thread(() -> {
                try {
                    Response<Container<UserDTO>> userResponse = userCall.execute();
                    Response<Container<String>> publicKeyResponse = publicKeyCall.execute();
                    if (userResponse.code() == 200 && publicKeyResponse.code() == 200) {
                        Contact contact = databaseMapper.toContact(databaseMapper.toModel(userResponse.body().getContent()));
                        contact.setPublicKey(publicKeyResponse.body().getContent());

                        contactInterface.save(userResponse.body().getContent(), publicKeyResponse.body().getContent());
                        Chat chat = new Chat(message.getCid(), contact.getUid());
                        chatInterface.saveChat(chat);
                        chatInterface.saveMessage(message);

                        reload();
                    }
                } catch (IOException e) {
                    //Nothing
                }
            }).start();
        }
    }

    private void reload() {
        chatListAdapter = new ChatListAdapter(parent, chatInterface.getALl());
    }

    private void newChat() {
        Intent intent = new Intent(parent.getApplicationContext(), UserSearchActivity.class);
        startActivity(intent);
    }
}
