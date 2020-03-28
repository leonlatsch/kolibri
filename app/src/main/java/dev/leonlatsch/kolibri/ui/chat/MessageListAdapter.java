package dev.leonlatsch.kolibri.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.database.interfaces.UserInterface;
import dev.leonlatsch.kolibri.database.model.Message;

/**
 * Adapter for displaying the ChatActivity's Message Recycler
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;

    private UserInterface userInterface;

    public MessageListAdapter(Context context, List<Message> messageList) {
        this.mContext = context;
        this.mMessageList = messageList;
        userInterface = UserInterface.getInstance();
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if (message.getFrom().equals(userInterface.getUser().getUid())) {
            return VIEW_TYPE_MESSAGE_SENT; // If the message from id is equal to the logged in user
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    public void updateMessageStatus(Message message) {
        for (int i = 0; i < mMessageList.size(); i++) {
            if (mMessageList.get(i).getMid().equals(message.getMid())) {
                mMessageList.get(i).setSent(message.isSent());
                notifyDataSetChanged();
                break;
            }
        }
    }

    public boolean isMessagePresent(Message message) {
        for (Message data : mMessageList) {
            if (data.getMid().equals(message.getMid())) {
                return true;
            }
        }

        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null; // should never happen case
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    public void add(Message message) {
        mMessageList.add(message);
        notifyItemInserted(mMessageList.size() - 1);
    }

    public int getLastPosition() {
        return mMessageList.size() - 1;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView messageBody, messageTimestamp;
        ImageView sentIndicator;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            messageBody = itemView.findViewById(R.id.sent_message_body);
            messageTimestamp = itemView.findViewById(R.id.sent_message_timestamp);
            sentIndicator = itemView.findViewById(R.id.sent_message_sent_indicator);
        }

        void bind(Message message) {
            messageBody.setText(message.getContent());
            messageTimestamp.setText(message.getTimestamp().substring(11, 16)); // Hard code for the moment
            if (message.isSent()) {
                sentIndicator.setImageDrawable(mContext.getDrawable(R.drawable.ic_check));
            } else {
                sentIndicator.setImageDrawable(mContext.getDrawable(R.drawable.ic_watch));
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView messageBody, messageTimestamp;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            messageBody = itemView.findViewById(R.id.received_message_body);
            messageTimestamp = itemView.findViewById(R.id.received_message_timestamp);
        }

        void bind(Message message) {
            messageBody.setText(message.getContent());
            messageTimestamp.setText(message.getTimestamp().substring(11, 16)); // Hard code for the moment
        }
    }
}
