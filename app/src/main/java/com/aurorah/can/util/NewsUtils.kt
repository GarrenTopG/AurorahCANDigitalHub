package com.aurorah.can.util

import androidx.core.text.HtmlCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object NewsUtils {

    private const val BASE_BACKEND_URL = "https://aurorah-web-hub.onrender.com"

    fun resolveImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val trimmed = url.trim()
        return when {
            trimmed.startsWith("https://", ignoreCase = true) -> {
                if (trimmed.contains("aurorahcan.org.za/static/", ignoreCase = true) ||
                    trimmed.contains("aurorahcan.org.za/uploads/", ignoreCase = true) ||
                    trimmed.contains("aurorahcan.org.za/images/", ignoreCase = true)) {
                    trimmed.replace("aurorahcan.org.za", "aurorah-web-hub.onrender.com", ignoreCase = true)
                } else {
                    trimmed
                }
            }
            trimmed.startsWith("http://", ignoreCase = true) -> {
                val httpsUrl = trimmed.replaceFirst("http://", "https://", ignoreCase = true)
                if (httpsUrl.contains("aurorahcan.org.za/static/", ignoreCase = true) ||
                    httpsUrl.contains("aurorahcan.org.za/uploads/", ignoreCase = true) ||
                    httpsUrl.contains("aurorahcan.org.za/images/", ignoreCase = true)) {
                    httpsUrl.replace("aurorahcan.org.za", "aurorah-web-hub.onrender.com", ignoreCase = true)
                } else {
                    httpsUrl
                }
            }
            trimmed.startsWith("//") -> {
                "https:$trimmed"
            }
            trimmed.startsWith("/") -> {
                "$BASE_BACKEND_URL$trimmed"
            }
            else -> {
                "$BASE_BACKEND_URL/$trimmed"
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

        // Unescape double-escaped or entity-encoded HTML strings
        while (textToParse.contains("&lt;") && textToParse.contains("&gt;")) {
            val previous = textToParse
            textToParse = textToParse
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
            if (previous == textToParse) break
        }

        // If the text has no HTML paragraph or break tags, but contains newlines, convert to HTML paragraphs
        if (!textToParse.contains("<p", ignoreCase = true) &&
            !textToParse.contains("<br", ignoreCase = true) &&
            !textToParse.contains("<div", ignoreCase = true) &&
            textToParse.contains("\n")
        ) {
            val paragraphs = textToParse.split(Regex("\r?\n\r?\n"))
                .filter { it.isNotBlank() }
                .joinToString("") { "<p>${it.trim().replace("\n", "<br/>")}</p>" }
            if (paragraphs.isNotBlank()) {
                textToParse = paragraphs
            }
        }

        return try {
            HtmlCompat.fromHtml(textToParse, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } catch (_: Throwable) {
            textToParse.replace(Regex("<[^>]*>"), "")
        }
    }
}
