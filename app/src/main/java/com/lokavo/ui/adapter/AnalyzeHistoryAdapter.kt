package com.lokavo.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.lokavo.data.local.entity.AnalyzeHistory
import com.lokavo.databinding.ItemSearchHistoryBinding
import com.lokavo.ui.analyzeHistory.AnalyzeHistoryViewModel
import com.lokavo.ui.result.ResultActivity
import com.lokavo.utils.DateFormatter
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection

class AnalyzeHistoryAdapter(private val analyzeHistoryViewModel: AnalyzeHistoryViewModel) :
    RecyclerView.Adapter<AnalyzeHistoryAdapter.HistoryViewHolder>() {

    private val listAnalyzeHistory = ArrayList<AnalyzeHistory>()

    fun setListHistory(listAnalyzeHistory: List<AnalyzeHistory>) {
        val diffCallback = HistoryDiffCallback(this.listAnalyzeHistory, listAnalyzeHistory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listAnalyzeHistory.clear()
        this.listAnalyzeHistory.addAll(listAnalyzeHistory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =
            ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listAnalyzeHistory[position])
    }

    override fun getItemCount(): Int {
        return listAnalyzeHistory.size
    }

    inner class HistoryViewHolder(private val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(analyzeHistory: AnalyzeHistory) {
            binding.tvAddress.text = analyzeHistory.address
            binding.tvDate.text = analyzeHistory.date?.let { DateFormatter.getRelativeTime(it) }
            binding.deleteButton.setOnClickListener {
                analyzeHistoryViewModel.delete(analyzeHistory)
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

class HistoryDiffCallback(
    private val oldAnalyzeHistoryList: List<AnalyzeHistory>,
    private val newAnalyzeHistoryList: List<AnalyzeHistory>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldAnalyzeHistoryList.size

    override fun getNewListSize(): Int = newAnalyzeHistoryList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldHistory = oldAnalyzeHistoryList[oldItemPosition]
        val newHistory = newAnalyzeHistoryList[newItemPosition]
        return oldHistory.latitude == newHistory.latitude && oldHistory.longitude == newHistory.longitude && oldHistory.address == newHistory.address
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldHistory = oldAnalyzeHistoryList[oldItemPosition]
        val newHistory = newAnalyzeHistoryList[newItemPosition]
        return oldHistory.latitude == newHistory.latitude && oldHistory.longitude == newHistory.longitude && oldHistory.address == newHistory.address
    }
}