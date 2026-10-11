package com.aurorah.can.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aurorah.can.data.api.MockApiRepository
import com.aurorah.can.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository = MockApiRepository()
    private val newsAdapter = NewsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }

        loadNewsData()
    }

    private fun loadNewsData() {
        binding.pbHome.visibility = View.VISIBLE
        lifecycleScope.launch {
            val news = repository.getNews()
            binding.pbHome.visibility = View.GONE
            newsAdapter.updateData(news)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
