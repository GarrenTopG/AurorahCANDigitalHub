package com.aurorah.can.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import coil.load
import com.aurorah.can.R
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.databinding.BottomSheetNewsDetailBinding
import com.aurorah.can.util.NewsUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewsDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetNewsDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_CATEGORY = "arg_category"
        private const val ARG_DATE = "arg_date"
        private const val ARG_AUTHOR = "arg_author"
        private const val ARG_BODY = "arg_body"
        private const val ARG_IMAGE_URL = "arg_image_url"

        fun newInstance(newsItem: NewsItem): NewsDetailBottomSheetFragment {
            val fragment = NewsDetailBottomSheetFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, newsItem.title)
                putString(ARG_CATEGORY, newsItem.category)
                putString(ARG_DATE, newsItem.timestamp)
                putString(ARG_AUTHOR, newsItem.author)
                putString(ARG_BODY, newsItem.body.ifBlank { newsItem.summary })
                putString(ARG_IMAGE_URL, newsItem.imageUrl)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetNewsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val title = args?.getString(ARG_TITLE, "") ?: ""
        val category = args?.getString(ARG_CATEGORY, "General") ?: "General"
        val date = args?.getString(ARG_DATE, "") ?: ""
        val rawAuthor = args?.getString(ARG_AUTHOR, "Aurorah CAN Team") ?: "Aurorah CAN Team"
        val author = rawAuthor.ifBlank { "Aurorah CAN Team" }
        val body = args?.getString(ARG_BODY, "") ?: ""
        val imageUrl = args?.getString(ARG_IMAGE_URL)

        binding.tvNewsDetailTitle.text = title
        binding.tvNewsDetailCategory.text = category
        binding.tvNewsDetailDate.text = NewsUtils.formatNewsDate(date)
        binding.tvNewsDetailAuthor.text = getString(R.string.author_format, author)

        val rawContent = if (body.isNotBlank()) body else title
        binding.tvNewsDetailBody.text = NewsUtils.parseHtmlContent(rawContent)

        val resolvedImageUrl = NewsUtils.resolveImageUrl(imageUrl)
        binding.cardNewsImageContainer.visibility = View.VISIBLE
        if (!resolvedImageUrl.isNullOrBlank()) {
            binding.ivNewsDetailImage.load(resolvedImageUrl) {
                placeholder(R.drawable.ic_news_placeholder)
                error(R.drawable.ic_news_placeholder)
                crossfade(true)
            }
        } else {
            binding.ivNewsDetailImage.setImageResource(R.drawable.ic_news_placeholder)
        }

        binding.tvMediaContact.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_SENDTO, "mailto:media@aurorahcan.org.za".toUri())
                startActivity(Intent.createChooser(intent, "Contact Media Team"))
            } catch (_: Exception) {
                // Fallback
            }
        }

        binding.btnGetHelp.setOnClickListener {
            val nav = parentFragment?.findNavController() ?: runCatching { findNavController() }.getOrNull()
            dismissAllowingStateLoss()
            nav?.let { navController ->
                try {
                    navController.navigate(R.id.action_home_to_get_help)
                } catch (_: Exception) {
                    try {
                        navController.navigate(R.id.navigation_get_help)
                    } catch (_: Exception) { }
                }
            }
        }

        binding.btnMakePledge.setOnClickListener {
            val nav = parentFragment?.findNavController() ?: runCatching { findNavController() }.getOrNull()
            dismissAllowingStateLoss()
            nav?.let { navController ->
                try {
                    navController.navigate(R.id.action_home_to_donation)
                } catch (_: Exception) {
                    try {
                        navController.navigate(R.id.navigation_donation)
                    } catch (_: Exception) { }
                }
            }
        }

        binding.btnVolunteer.setOnClickListener {
            val nav = parentFragment?.findNavController() ?: runCatching { findNavController() }.getOrNull()
            dismissAllowingStateLoss()
            nav?.let { navController ->
                try {
                    navController.navigate(R.id.action_home_to_volunteer)
                } catch (_: Exception) {
                    try {
                        navController.navigate(R.id.navigation_volunteer)
                    } catch (_: Exception) { }
                }
            }
        }

        binding.btnCloseNewsDetail.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
