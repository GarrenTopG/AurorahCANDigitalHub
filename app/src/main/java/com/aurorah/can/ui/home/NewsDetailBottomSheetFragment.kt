package com.aurorah.can.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import coil.load
import com.aurorah.can.R
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.databinding.BottomSheetNewsDetailBinding
import com.aurorah.can.util.NewsUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewsDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetNewsDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_CATEGORY = "arg_category"
        private const val ARG_DATE = "arg_date"
        private const val ARG_AUTHOR = "arg_author"
        private const val ARG_CONTENT = "arg_content"
        private const val ARG_SUMMARY = "arg_summary"
        private const val ARG_BODY = "arg_body"
        private const val ARG_IMAGE_URL = "arg_image_url"

        fun newInstance(newsItem: NewsItem): NewsDetailBottomSheetFragment {
            val fragment = NewsDetailBottomSheetFragment()
            val args = Bundle().apply {
                putInt(ARG_ID, newsItem.id)
                putString(ARG_TITLE, newsItem.title)
                putString(ARG_CATEGORY, newsItem.category)
                putString(ARG_DATE, newsItem.timestamp)
                putString(ARG_AUTHOR, newsItem.author)
                putString(ARG_CONTENT, newsItem.content)
                putString(ARG_SUMMARY, newsItem.summary)
                putString(ARG_BODY, newsItem.body)
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
        val newsId = args?.getInt(ARG_ID, 0) ?: 0
        val title = args?.getString(ARG_TITLE, "") ?: ""
        val category = args?.getString(ARG_CATEGORY, "General") ?: "General"
        val date = args?.getString(ARG_DATE, "") ?: ""
        val rawAuthor = args?.getString(ARG_AUTHOR, "Aurorah CAN Team") ?: "Aurorah CAN Team"
        val author = rawAuthor.ifBlank { "Aurorah CAN Team" }
        val content = args?.getString(ARG_CONTENT, "") ?: ""
        val summary = args?.getString(ARG_SUMMARY, "") ?: ""
        val body = args?.getString(ARG_BODY, "") ?: ""
        val imageUrl = args?.getString(ARG_IMAGE_URL)

        binding.tvNewsDetailTitle.text = title
        binding.tvNewsDetailCategory.text = category
        binding.tvNewsDetailDate.text = NewsUtils.formatNewsDate(date)
        binding.tvNewsDetailAuthor.text = getString(R.string.author_format, author)

        val rawContent = when {
            body.isNotBlank() -> body
            content.isNotBlank() -> content
            summary.isNotBlank() -> summary
            else -> title
        }
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

        val webUrl = if (newsId > 0) {
            "https://aurorah-web-hub.onrender.com/news/$newsId"
        } else {
            "https://aurorah-web-hub.onrender.com/news"
        }

        binding.btnShareArticle.setOnClickListener {
            try {
                val shareText = "$title\n\nRead more on Aurorah CAN: $webUrl"
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_article)))
            } catch (_: Exception) {
                // Fallback
            }
        }

        binding.btnOpenFullStoryWebsite.setOnClickListener {
            try {
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                    .build()
                customTabsIntent.launchUrl(requireContext(), webUrl.toUri())
            } catch (_: Exception) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, webUrl.toUri())
                    startActivity(intent)
                } catch (_: Exception) {
                    // Fallback
                }
            }
        }

        binding.btnCloseNewsDetail.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetDialog = dialog as? BottomSheetDialog
        bottomSheetDialog?.let { dialog ->
            val sheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            sheet?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isFitToContents = true
                bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
