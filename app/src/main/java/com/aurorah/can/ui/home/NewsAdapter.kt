package com.aurorah.can.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.aurorah.can.R
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.databinding.ItemNewsBinding
import com.aurorah.can.util.NewsUtils

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
            val cleanSummary = (if (item.summary.isNotBlank()) item.summary else item.body)
                .let { NewsUtils.parseHtmlContent(it).toString().trim() }
            binding.tvNewsSummary.text = cleanSummary
            binding.tvNewsTimestamp.text = NewsUtils.formatNewsDate(item.timestamp)
            binding.tvNewsCategory.text = item.category.ifBlank { "General" }

            binding.ivNewsImage.visibility = View.VISIBLE
            val resolvedUrl = NewsUtils.resolveImageUrl(item.imageUrl)
            if (!resolvedUrl.isNullOrBlank()) {
                binding.ivNewsImage.load(resolvedUrl) {
                    placeholder(R.drawable.ic_news_placeholder)
                    error(R.drawable.ic_news_placeholder)
                    crossfade(true)
                }
            } else {
                binding.ivNewsImage.setImageResource(R.drawable.ic_news_placeholder)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
