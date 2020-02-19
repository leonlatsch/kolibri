package dev.leonlatsch.kolibri.main.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.database.model.Message

/**
 * Adapter for displaying the ChatActivity's Message Recycler
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class MessageListAdapter(private val mContext: Context, private val mMessageList: List<Message>) : RecyclerView.Adapter() {

    private val userInterface: UserInterface

    val itemCount: Int
        @Override
        get() = mMessageList.size()

    val lastPosition: Int
        get() = mMessageList.size() - 1

    init {
        userInterface = UserInterface.getInstance()
    }

    @Override
    fun getItemViewType(position: Int): Int {
        val message = mMessageList[position]

        return if (message.getFrom().equals(userInterface.getUser().getUid())) {
            VIEW_TYPE_MESSAGE_SENT // If the message from id is equal to the logged in user
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    fun updateMessageStatus(message: Message) {
        for (i in 0 until mMessageList.size()) {
            if (mMessageList[i].getMid().equals(message.getMid())) {
                mMessageList[i].setSent(message.isSent())
                notifyDataSetChanged()
                break
            }
        }
    }

    fun isMessagePresent(message: Message): Boolean {
        for (data in mMessageList) {
            if (data.getMid().equals(message.getMid())) {
                return true
            }
        }

        return false
    }

    @NonNull
    @Override
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        var view: View? = null

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false)
            return SentMessageHolder(view)
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false)
            return ReceivedMessageHolder(view)
        }

        return null // should never happen case
    }

    @Override
    fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList[position]

        when (holder.getItemViewType()) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    fun add(message: Message) {
        mMessageList.add(message)
        notifyItemInserted(mMessageList.size() - 1)
    }

    private inner class SentMessageHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var messageBody: TextView
        internal var messageTimestamp: TextView
        internal var sentIndicator: ImageView

        init {

            messageBody = itemView.findViewById(R.id.sent_message_body)
            messageTimestamp = itemView.findViewById(R.id.sent_message_timestamp)
            sentIndicator = itemView.findViewById(R.id.sent_message_sent_indicator)
        }

        internal fun bind(message: Message) {
            messageBody.setText(message.getContent())
            messageTimestamp.setText(message.getTimestamp().substring(11, 16)) // Hard code for the moment
            if (message.isSent()) {
                sentIndicator.setImageDrawable(mContext.getDrawable(R.drawable.ic_check))
            } else {
                sentIndicator.setImageDrawable(mContext.getDrawable(R.drawable.ic_watch))
            }
        }
    }

    private inner class ReceivedMessageHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var messageBody: TextView
        internal var messageTimestamp: TextView

        init {

            messageBody = itemView.findViewById(R.id.received_message_body)
            messageTimestamp = itemView.findViewById(R.id.received_message_timestamp)
        }

        internal fun bind(message: Message) {
            messageBody.setText(message.getContent())
            messageTimestamp.setText(message.getTimestamp().substring(11, 16)) // Hard code for the moment
        }
    }

    companion object {

        private val VIEW_TYPE_MESSAGE_SENT = 1
        private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }
}
