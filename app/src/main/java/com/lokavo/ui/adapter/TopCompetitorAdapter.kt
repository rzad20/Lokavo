package com.lokavo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lokavo.R
import com.lokavo.data.remote.response.PoiMapItem
import com.lokavo.databinding.PlaceItemCardBinding
import com.lokavo.utils.isOnline
import com.lokavo.utils.showSnackbarOnNoConnection

class TopCompetitorAdapter(
    private val competitors: List<PoiMapItem?>?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TopCompetitorAdapter.CompetitorViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(placeId: String, button: Button)
    }

    class CompetitorViewHolder(val binding: PlaceItemCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitorViewHolder {
        val binding =
            PlaceItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompetitorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompetitorViewHolder, position: Int) {
        val competitor = competitors?.get(position)
        if (competitor != null) {
            holder.binding.txtPlaceName.text = competitor.name
            holder.binding.txtRating.text = competitor.rating.toString()
            holder.binding.txtReviewCount.text =
                holder.itemView.context.getString(R.string.ulasan, competitor.reviews)
            holder.binding.txtKategori.text = competitor.mainCategory
            Glide.with(holder.itemView.context).load(competitor.featuredImage)
                .into(holder.binding.placeImage)
            holder.binding.btnDetail.setOnClickListener {
                if (!it.context.isOnline()) {
                    holder.binding.root.showSnackbarOnNoConnection(holder.itemView.context)
                } else {
                    competitor.placeId?.let { it1 ->
                        listener.onItemClick(
                            it1,
                            holder.binding.btnDetail
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = competitors?.size ?: 0
}
