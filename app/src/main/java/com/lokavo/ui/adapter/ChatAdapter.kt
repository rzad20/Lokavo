package com.lokavo.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lokavo.R

data class Message(
    val isUser: Boolean,
    val user: String?,
    val bot: String?,
    val text: String,
    val photo: Uri?
)

class ChatAdapter(
    private val messages: List<Message>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER_MESSAGE = 1
        private const val VIEW_TYPE_CHATBOT_MESSAGE = 2
    }

    class UserMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUser: TextView = view.findViewById(R.id.tvUser)
        val tvMessage: TextView = view.findViewById(R.id.tvUserMessage)
        val ivUser: ImageView = view.findViewById(R.id.ivUser)
    }

    class ChatBotMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBot: TextView = view.findViewById(R.id.tvBot)
        val tvMessage: TextView = view.findViewById(R.id.tvBotMessage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER_MESSAGE else VIEW_TYPE_CHATBOT_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_message, parent, false)
            UserMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatbot_message, parent, false)
            ChatBotMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserMessageViewHolder) {
            holder.tvUser.text = message.user
            holder.tvMessage.text = message.text
            Glide.with(holder.ivUser.context)
                .load(message.photo)
                .into(holder.ivUser)
        } else if (holder is ChatBotMessageViewHolder) {
            holder.tvBot.text = message.bot
            holder.tvMessage.text = message.text
        }
    }

    override fun getItemCount() = messages.size
}
