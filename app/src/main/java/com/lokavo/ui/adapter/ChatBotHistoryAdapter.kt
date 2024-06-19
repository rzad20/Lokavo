package com.lokavo.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lokavo.data.local.entity.ChatBotHistory
import com.lokavo.data.remote.response.ChatBotMessageResponse
import com.lokavo.databinding.ItemSearchHistoryBinding
import com.lokavo.ui.chatBotHistory.ChatBotHistoryViewModel
import com.lokavo.ui.historyChatbotDetail.HistoryChatbotDetail
import com.lokavo.utils.DateFormatter
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatBotHistoryAdapter(private val chatBotHistoryViewModel: ChatBotHistoryViewModel) :
    RecyclerView.Adapter<ChatBotHistoryAdapter.HistoryViewHolder>() {

    private val listChatBotHistory = ArrayList<ChatBotHistory>()

    fun setListHistory(listChatBotHistory: List<ChatBotHistory>) {
        val diffCallback = HistoryDiffCallback2(this.listChatBotHistory, listChatBotHistory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listChatBotHistory.clear()
        this.listChatBotHistory.addAll(listChatBotHistory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =
            ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listChatBotHistory[position])
    }

    override fun getItemCount(): Int {
        return listChatBotHistory.size
    }

    inner class HistoryViewHolder(private val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatbotHistory: ChatBotHistory) {
            binding.tvAddress.text = chatbotHistory.address
            binding.tvDate.text = chatbotHistory.date?.let { DateFormatter.getRelativeTime(it) }
            binding.deleteButton.setOnClickListener {
                chatBotHistoryViewModel.delete(chatbotHistory)
            }

            binding.root.setOnClickListener {
                if (!binding.root.context.isOnline()) {
                    binding.root.showSnackbarOnNoConnection(binding.root.context)
                } else {
                    val context = it.context
                    val messages = mutableListOf<ChatBotMessageResponse>()
                    CoroutineScope(Dispatchers.IO).launch {
                        val existingHistory = chatbotHistory.userId?.let { it1 ->
                            chatbotHistory.latitude?.let { it2 ->
                                chatbotHistory.longitude?.let { it3 ->
                                    chatBotHistoryViewModel.findByLatLong(
                                        it1,
                                        it2,
                                        it3
                                    )
                                }
                            }
                        }
                        existingHistory?.details?.forEach { detail ->
                            val message = ChatBotMessageResponse(
                                answer = detail.answer,
                                question = detail.question,
                            )
                            messages.add(message)
                        }
                        withContext(Dispatchers.Main) {
                            val intent =
                                Intent(binding.root.context, HistoryChatbotDetail::class.java)
                            intent.putParcelableArrayListExtra(
                                HistoryChatbotDetail.MESSAGES,
                                ArrayList(messages)
                            )
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

class HistoryDiffCallback2(
    private val oldChatBotHistoryList: List<ChatBotHistory>,
    private val newChatBotHistoryList: List<ChatBotHistory>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldChatBotHistoryList.size

    override fun getNewListSize(): Int = newChatBotHistoryList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldHistory = oldChatBotHistoryList[oldItemPosition]
        val newHistory = newChatBotHistoryList[newItemPosition]
        return oldHistory.latitude == newHistory.latitude && oldHistory.longitude == newHistory.longitude && oldHistory.address == newHistory.address
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldHistory = oldChatBotHistoryList[oldItemPosition]
        val newHistory = newChatBotHistoryList[newItemPosition]
        return oldHistory.latitude == newHistory.latitude && oldHistory.longitude == newHistory.longitude && oldHistory.address == newHistory.address
    }
}