package com.aurorah.can.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.databinding.ItemNewsBinding

class NewsAdapter(
    private var newsList: List<NewsItem> = emptyList(),
    private val onItemClick: (NewsItem) -> Unit = {}
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    fun updateData(newList: List<NewsItem>) {
        newsList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position], onItemClick)
    }

    override fun getItemCount(): Int = newsList.size

    class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewsItem, onItemClick: (NewsItem) -> Unit) {
            binding.tvNewsTitle.text = item.title
            binding.tvNewsSummary.text = item.summary
            binding.tvNewsTimestamp.text = item.timestamp
            binding.tvNewsCategory.text = item.category.ifBlank { "General" }
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
