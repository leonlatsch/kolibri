package de.leonlatsch.olivia.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.main.MainActivity;
import de.leonlatsch.olivia.main.UserSearchActivity;

public class ChatFragment extends Fragment {

    private MainActivity parent;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_chats, container, false);

        parent = (MainActivity) getActivity();

        FloatingActionButton newChatFab = view.findViewById(R.id.newChatFab);

        newChatFab.setOnClickListener(v -> newChat());

        return view;
    }

    private void newChat() {
        Intent intent = new Intent(parent.getApplicationContext(), UserSearchActivity.class);
        startActivity(intent);
    }
}
