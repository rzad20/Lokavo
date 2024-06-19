package com.lokavo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lokavo.data.remote.response.ListItem
import com.lokavo.databinding.ItemArticleBinding

class ArticleAdapter(private val onItemClick: (String?) -> Unit) :
    ListAdapter<ListItem, ArticleAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    class MyViewHolder(
        private val binding: ItemArticleBinding,
        private val onItemClick: (String?) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ListItem) {
            binding.articleTitle.text = article.headline
            binding.articleShortDetail.text = article.ringkasan
            Glide.with(binding.root)
                .load(article.gambar)
                .into(binding.articleImage)

            binding.root.setOnClickListener {
                val url = article.link
                onItemClick(url)
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
