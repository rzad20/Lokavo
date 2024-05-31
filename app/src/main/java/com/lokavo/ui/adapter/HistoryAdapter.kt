package com.lokavo.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.lokavo.data.local.entity.History
import com.lokavo.databinding.ItemHistoryBinding
import com.lokavo.ui.history.HistoryViewModel
import com.lokavo.ui.result.ResultActivity
import com.lokavo.utils.DateFormatter

class HistoryAdapter(private val historyViewModel: HistoryViewModel) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val listHistory = ArrayList<History>()

    fun setListHistory(listHistory: List<History>) {
        val diffCallback = HistoryDiffCallback(this.listHistory, listHistory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listHistory.clear()
        this.listHistory.addAll(listHistory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.tvAddress.text = history.address
            binding.tvDate.text = history.date?.let { DateFormatter.getRelativeTime(it) }
            binding.deleteButton.setOnClickListener {
                historyViewModel.delete(history)
            }
            binding.root.setOnClickListener {
                val context = it.context
                val intent = Intent(
                    context,
                    ResultActivity::class.java
                ) // Ganti DestinationActivity dengan activity tujuan Anda
                intent.putExtra(
                    ResultActivity.LOCATION,
                    history.latitude?.let { it1 ->
                        history.longitude?.let { it2 ->
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

class HistoryDiffCallback(
    private val oldHistoryList: List<History>,
    private val newHistoryList: List<History>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldHistoryList.size

    override fun getNewListSize(): Int = newHistoryList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldHistory = oldHistoryList[oldItemPosition]
        val newHistory = newHistoryList[newItemPosition]
        return oldHistory.latitude == newHistory.latitude && oldHistory.longitude == newHistory.longitude && oldHistory.address == newHistory.address
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldHistory = oldHistoryList[oldItemPosition]
        val newHistory = newHistoryList[newItemPosition]
        return oldHistory.latitude == newHistory.latitude && oldHistory.longitude == newHistory.longitude && oldHistory.address == newHistory.address
    }
}