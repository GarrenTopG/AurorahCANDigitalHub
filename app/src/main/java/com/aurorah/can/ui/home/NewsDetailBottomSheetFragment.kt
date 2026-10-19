package com.aurorah.can.ui.home

import android.content.Intent
import androidx.core.net.toUri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.aurorah.can.R
import com.aurorah.can.data.model.NewsItem
import com.aurorah.can.databinding.BottomSheetNewsDetailBinding
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

        fun newInstance(newsItem: NewsItem): NewsDetailBottomSheetFragment {
            val fragment = NewsDetailBottomSheetFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, newsItem.title)
                putString(ARG_CATEGORY, newsItem.category)
                putString(ARG_DATE, newsItem.timestamp)
                putString(ARG_AUTHOR, newsItem.author)
                putString(ARG_BODY, newsItem.body.ifBlank { newsItem.summary })
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
        val author = args?.getString(ARG_AUTHOR, "Aurorah CAN Team") ?: "Aurorah CAN Team"
        val body = args?.getString(ARG_BODY, "") ?: ""

        binding.tvNewsDetailTitle.text = title
        binding.tvNewsDetailCategory.text = category
        binding.tvNewsDetailDate.text = if (date.isNotBlank()) date else "Recent Update"
        binding.tvNewsDetailAuthor.text = getString(R.string.author_format, author)
        binding.tvNewsDetailBody.text = if (body.isNotBlank()) body else title

        binding.tvMediaContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, "mailto:media@aurorahcan.org.za".toUri())
            try {
                startActivity(Intent.createChooser(intent, "Contact Media Team"))
            } catch (_: Exception) {
                // Fallback
            }
        }

        binding.btnGetHelp.setOnClickListener {
            val navController = parentFragment?.findNavController() ?: findNavController()
            dismiss()
            navController.navigate(R.id.navigation_get_help)
        }

        binding.btnMakePledge.setOnClickListener {
            val navController = parentFragment?.findNavController() ?: findNavController()
            dismiss()
            navController.navigate(R.id.navigation_donation)
        }

        binding.btnVolunteer.setOnClickListener {
            val navController = parentFragment?.findNavController() ?: findNavController()
            dismiss()
            navController.navigate(R.id.navigation_volunteer)
        }

        binding.btnCloseNewsDetail.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
