package com.lokavo.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lokavo.databinding.ItemChatBotMessageBinding
import com.lokavo.databinding.ItemUserMessageBinding

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

    class UserMessageViewHolder(val binding: ItemUserMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ChatBotMessageViewHolder(val binding: ItemChatBotMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER_MESSAGE else VIEW_TYPE_CHATBOT_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER_MESSAGE) {
            val binding =
                ItemUserMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UserMessageViewHolder(binding)
        } else {
            val binding = ItemChatBotMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ChatBotMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserMessageViewHolder) {
            holder.binding.tvUser.text = message.user
            holder.binding.tvUserMessage.text = message.text
            if (message.photo.toString().isNotEmpty()) {
                Glide.with(holder.binding.ivUser.context)
                    .load(message.photo)
                    .into(holder.binding.ivUser)
            }
        } else if (holder is ChatBotMessageViewHolder) {
            holder.binding.tvBot.text = message.bot
            holder.binding.tvBotMessage.text = message.text
        }
    }

    override fun getItemCount() = messages.size
}
