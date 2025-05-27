package com.taskease.yksfoundation.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Model.ChatMessage
import com.taskease.yksfoundation.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val chatList: List<ChatMessage>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].senderId == currentUserId) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENDER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_sender, parent, false)
            SenderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_receiver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chatList[position]
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(chat.timestamp))

        if (holder is SenderViewHolder) {
            holder.tvMessage.text = chat.message
            holder.tvTimestamp.text = time
        } else if (holder is ReceiverViewHolder) {
            holder.tvMessage.text = chat.message
            holder.tvTimestamp.text = time
        }
    }

    override fun getItemCount(): Int = chatList.size
}

