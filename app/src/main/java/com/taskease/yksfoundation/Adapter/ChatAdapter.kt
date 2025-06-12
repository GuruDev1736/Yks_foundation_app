package com.taskease.yksfoundation.Adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.provider.CalendarContract.Colors
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Model.ChatMessage
import com.taskease.yksfoundation.R
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val context: Context , private val chatList: List<ChatMessage>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val profilePic: CircleImageView = itemView.findViewById(R.id.profilePic)
        val name: TextView = itemView.findViewById(R.id.name)
        val senderLayout: LinearLayout = itemView.findViewById(R.id.senderLayout)
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val profilePic: CircleImageView = itemView.findViewById(R.id.profilePic)
        val name: TextView = itemView.findViewById(R.id.name)
        val receiverLayout: LinearLayout = itemView.findViewById(R.id.receiverLayout)
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
            holder.name.text = chat.senderName
            holder.tvMessage.text = chat.message
            holder.tvTimestamp.text = time
            Glide.with(context).load(Constant.base64ToBitmap(chat.profilePic)).error(R.drawable.imagefalied).into(holder.profilePic)
            holder.profilePic.setOnClickListener {
                showUserDialog(context,chat.senderName,chat.designation, chat.profilePic)
            }
            if (chat.senderRole == "ROLE_SUPER_ADMIN")
            {
                holder.senderLayout.setBackgroundColor(Color.YELLOW)
            }
        } else if (holder is ReceiverViewHolder) {
            holder.name.text = chat.senderName
            holder.tvMessage.text = chat.message
            holder.tvTimestamp.text = time
            Glide.with(context).load(Constant.base64ToBitmap(chat.profilePic)).error(R.drawable.imagefalied).into(holder.profilePic)
            holder.profilePic.setOnClickListener {
                showUserDialog(context,chat.senderName,chat.designation, chat.profilePic)
            }
            if (chat.senderRole == "ROLE_SUPER_ADMIN")
            {
                holder.receiverLayout.setBackgroundColor(Color.YELLOW)
            }
        }

    }

    fun showUserDialog(
        context: Context,
        name: String,
        designation: String,
        imageResId: String?,
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_user_profile, null)

        val imageView = dialogView.findViewById<ImageView?>(R.id.imageViewProfile)
        val nameText = dialogView.findViewById<TextView?>(R.id.textViewName)
        val designationText = dialogView.findViewById<TextView?>(R.id.textViewDesignation)
        val genderUser = dialogView.findViewById<LinearLayout?>(R.id.genderLayout)
        val locationUser = dialogView.findViewById<LinearLayout?>(R.id.locationLayout)

        genderUser.visibility = View.GONE
        locationUser.visibility = View.GONE

        // Set image with null-safe Base64 decode
        val bitmap = if (!imageResId.isNullOrBlank()) Constant.base64ToBitmap(imageResId) else null
        if (bitmap != null && imageView != null) {
            Glide.with(context)
                .load(bitmap)
                .error(R.drawable.imagefalied)
                .into(imageView)
        } else {
            imageView?.setImageResource(R.drawable.imagefalied)
        }

        // Set other fields only if views are not null
        nameText?.text = name
        designationText?.text = designation

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .show()
    }


    override fun getItemCount(): Int = chatList.size
}

