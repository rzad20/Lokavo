package com.lokavo.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.lokavo.data.local.entity.ChatBotHistory
import com.lokavo.databinding.ItemSearchHistoryBinding
import com.lokavo.ui.chatBotHistory.ChatBotHistoryViewModel
import com.lokavo.ui.result.ResultActivity
import com.lokavo.utils.DateFormatter
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection

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
        val binding = ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        fun bind(analyzeHistory: ChatBotHistory) {
            binding.tvAddress.text = analyzeHistory.address
            binding.tvDate.text = analyzeHistory.date?.let { DateFormatter.getRelativeTime(it) }
            binding.deleteButton.setOnClickListener {
                chatBotHistoryViewModel.delete(analyzeHistory)
            }

            binding.root.setOnClickListener {
                if (!binding.root.context.isOnline()) {
                    binding.root.showSnackbarOnNoConnection(binding.root.context)
                } else {
                    val context = it.context
                    val intent = Intent(
                        context,
                        ResultActivity::class.java
                    )
                    intent.putExtra(
                        ResultActivity.LOCATION,
                        analyzeHistory.latitude?.let { it1 ->
                            analyzeHistory.longitude?.let { it2 ->
                                LatLng(
                                    it1,
                                    it2
                                )
                            }
                        })
                    context.startActivity(intent)
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