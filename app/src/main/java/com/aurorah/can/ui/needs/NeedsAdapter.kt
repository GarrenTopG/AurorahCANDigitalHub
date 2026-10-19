package com.aurorah.can.ui.needs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aurorah.can.R
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.databinding.ItemCommunityNeedBinding

class NeedsAdapter(
    private var needsList: List<CommunityNeed> = emptyList(),
    private val onOfferAssistanceClicked: (CommunityNeed) -> Unit
) : RecyclerView.Adapter<NeedsAdapter.NeedViewHolder>() {

    fun updateData(newList: List<CommunityNeed>) {
        needsList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeedViewHolder {
        val binding = ItemCommunityNeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NeedViewHolder, position: Int) {
        holder.bind(needsList[position])
    }

    override fun getItemCount(): Int = needsList.size

    inner class NeedViewHolder(private val binding: ItemCommunityNeedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommunityNeed) {
            val context = binding.root.context
            binding.tvNeedId.text = context.getString(R.string.need_id_format, item.needId)
            binding.tvCategory.text = "[ ${item.category ?: "General"} ]"
            binding.tvNeedTitle.text = item.title ?: ""
            binding.tvNeedDesc.text = item.description ?: ""
            binding.tvLocation.text = "📍 ${item.location ?: ""}"

            // Urgency Badge setup
            val priority = (item.priority ?: "Low").lowercase()
            binding.tvUrgencyBadge.text = priority.uppercase()
            val badgeDrawable = when (priority) {
                "critical" -> R.drawable.bg_badge_critical
                "high" -> R.drawable.bg_badge_high
                "medium" -> R.drawable.bg_badge_medium
                else -> R.drawable.bg_badge_low
            }
            binding.tvUrgencyBadge.background = ContextCompat.getDrawable(context, badgeDrawable)

            binding.btnOfferAssistance.setOnClickListener {
                onOfferAssistanceClicked(item)
            }
        }
    }
}
