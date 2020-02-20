package dev.leonlatsch.kolibri.main.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
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
class MessageListAdapter(private val mContext: Context, private val mMessageList: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = mMessageList.size

    val lastPosition: Int = mMessageList.size - 1

    override fun getItemViewType(position: Int): Int {
        val message = mMessageList[position]

        return if (message.from.equals(UserInterface.user?.uid)) {
            VIEW_TYPE_MESSAGE_SENT // If the message from id is equal to the logged in user
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    fun updateMessageStatus(message: Message) {
        for (i in 0 until mMessageList.size) {
            if (mMessageList[i].mid.equals(message.mid)) {
                mMessageList[i].isSent = message.isSent
                notifyDataSetChanged()
                break
            }
        }
    }

    fun isMessagePresent(message: Message): Boolean {
        for (data in mMessageList) {
            if (data.mid.equals(message.mid)) {
                return true
            }
        }

        return false
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?

        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
            SentMessageHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageHolder(view)
        }
    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    fun add(message: Message) {
        mMessageList.add(message)
        notifyItemInserted(mMessageList.size - 1)
    }

    private inner class SentMessageHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var messageBody: TextView = itemView.findViewById(R.id.sent_message_body)
        internal var messageTimestamp: TextView = itemView.findViewById(R.id.sent_message_timestamp)
        internal var sentIndicator: ImageView = itemView.findViewById(R.id.sent_message_sent_indicator)

        internal fun bind(message: Message) {
            messageBody.text = message.content
            messageTimestamp.text = message.timestamp?.substring(11, 16) // Hard code for the moment
            if (message.isSent) {
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
            messageBody.text = message.content
            messageTimestamp.text = message.timestamp?.substring(11, 16) // Hard code for the moment
        }
    }

    companion object {

        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }
}
