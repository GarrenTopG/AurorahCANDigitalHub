package com.aurorah.can.ui.needs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aurorah.can.data.api.MockApiRepository
import com.aurorah.can.data.model.CommunityNeed
import com.aurorah.can.databinding.FragmentNeedsBinding
import kotlinx.coroutines.launch

class NeedsFragment : Fragment() {

    private var _binding: FragmentNeedsBinding? = null
    private val binding get() = _binding!!

    private val repository = MockApiRepository()
    private var allNeedsList: List<CommunityNeed> = emptyList()

    private val needsAdapter = NeedsAdapter { selectedNeed ->
        val bottomSheet = OfferAssistanceBottomSheetFragment.newInstance(
            needId = selectedNeed.needId,
            needTitle = selectedNeed.title
        )
        bottomSheet.show(childFragmentManager, "OfferAssistanceBottomSheet")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNeedsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNeeds.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = needsAdapter
        }

        setupFilterChips()
        loadNeedsData()
    }

    private fun setupFilterChips() {
        binding.cgFilterNeeds.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) {
                needsAdapter.updateData(allNeedsList)
                return@setOnCheckedStateChangeListener
            }

            val categoryFilter = when (checkedIds.first()) {
                binding.chipFood.id -> "Food"
                binding.chipClothing.id -> "Clothing"
                binding.chipEducation.id -> "Education"
                binding.chipHealthcare.id -> "Healthcare"
                else -> "All"
            }

            if (categoryFilter == "All") {
                needsAdapter.updateData(allNeedsList)
            } else {
                val filtered = allNeedsList.filter {
                    it.category.equals(categoryFilter, ignoreCase = true)
                }
                needsAdapter.updateData(filtered)
            }
        }
    }

    private fun loadNeedsData() {
        binding.pbNeeds.visibility = View.VISIBLE
        lifecycleScope.launch {
            allNeedsList = repository.getNeeds()
            binding.pbNeeds.visibility = View.GONE
            needsAdapter.updateData(allNeedsList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
