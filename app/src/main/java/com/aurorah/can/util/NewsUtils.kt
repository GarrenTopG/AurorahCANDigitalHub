package com.aurorah.can.util

import androidx.core.text.HtmlCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object NewsUtils {

    private const val BASE_WEB_URL = "https://aurorah-web-hub.onrender.com"

    fun resolveImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val trimmed = url.trim()
        return when {
            trimmed.startsWith("http://", ignoreCase = true) -> {
                trimmed.replaceFirst("http://", "https://", ignoreCase = true)
            }
            trimmed.startsWith("https://", ignoreCase = true) -> {
                trimmed
            }
            trimmed.startsWith("/") -> {
                "$BASE_WEB_URL$trimmed"
            }
            else -> {
                "$BASE_WEB_URL/$trimmed"
            }
        }
    }

    fun formatNewsDate(rawTimestamp: String?): String {
        if (rawTimestamp.isNullOrBlank()) return "Recent Update"
        val trimmed = rawTimestamp.trim()
        val isoFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (pattern in isoFormats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val parsedDate: Date? = sdf.parse(trimmed)
                if (parsedDate != null) {
                    val outputSdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                    return outputSdf.format(parsedDate)
                }
            } catch (_: Exception) { }
        }
        return if (trimmed.length >= 10 && trimmed.substring(0, 10).matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            trimmed.take(10)
        } else {
            trimmed
        }
    }

    fun parseHtmlContent(rawText: String?): CharSequence {
        if (rawText.isNullOrBlank()) return ""
        var textToParse = rawText.trim()
        if (textToParse.contains("&lt;") && textToParse.contains("&gt;")) {
            textToParse = textToParse
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
        }
        return try {
            HtmlCompat.fromHtml(textToParse, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } catch (_: Throwable) {
            textToParse.replace(Regex("<[^>]*>"), "")
        }
    }
}
