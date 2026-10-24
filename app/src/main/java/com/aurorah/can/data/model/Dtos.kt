package com.aurorah.can.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class UserDto(
    @SerializedName(value = "userId", alternate = ["user_id", "id"]) val userId: Int = 1,
    @SerializedName(value = "fullName", alternate = ["full_name", "name"]) val fullName: String = "User",
    @SerializedName(value = "email") val email: String = "",
    @SerializedName(value = "role", alternate = ["user_role"]) val role: String = "Volunteer",
    @SerializedName(value = "token", alternate = ["access_token", "jwt"]) val token: String? = null
) {
    fun toDomain(): User = User(
        userId = userId,
        fullName = fullName,
        email = email,
        role = role,
        token = token
    )
}

data class AuthResponseDto(
    @SerializedName(value = "success") val success: Boolean = true,
    @SerializedName(value = "message", alternate = ["error", "detail", "reason", "error_message", "description"]) val message: String = "",
    @SerializedName(value = "token", alternate = ["access_token", "jwt"]) val token: String? = null,
    @SerializedName(value = "user") val user: UserDto? = null
) {
    fun toDomain(): AuthResponse = AuthResponse(
        success = success,
        message = message,
        token = token,
        user = user?.toDomain()
    )
}

@JsonAdapter(NewsItemDtoAdapter::class)
data class NewsItemDto(
    @SerializedName(value = "id", alternate = ["news_id", "article_id", "articleId", "newsId", "story_id", "storyId"]) val id: Int = 0,
    @SerializedName(value = "title", alternate = ["headline", "subject", "name"]) val title: String = "",
    @SerializedName(value = "summary", alternate = ["description", "excerpt", "short_description", "shortDescription", "teaser", "snippet", "intro"]) val summary: String = "",
    @SerializedName(value = "body", alternate = ["content", "full_text", "fullText", "text", "article", "details", "full_content", "fullContent", "body_text", "bodyText", "body_html", "bodyHtml", "html_content", "htmlContent", "html", "message", "main_text", "mainText", "story"]) val body: String = "",
    @SerializedName(value = "author", alternate = ["by", "writer", "author_name", "authorName", "created_by", "createdBy", "publisher"]) val author: String = "Aurorah CAN Team",
    @SerializedName(value = "timestamp", alternate = ["created_at", "createdAt", "date", "published_at", "publishedAt", "created_date", "createdDate", "date_created", "dateCreated", "publish_date", "publishDate", "updated_at", "updatedAt"]) val timestamp: String = "",
    @SerializedName(value = "category", alternate = ["topic", "tag", "section", "category_name", "categoryName"]) val category: String = "General",
    @SerializedName(value = "image_url", alternate = ["imageUrl", "image", "media_url", "mediaUrl", "picture", "picture_url", "pictureUrl", "photo", "photo_url", "photoUrl", "cover_image", "coverImage", "featured_image", "featuredImage", "thumbnail", "thumbnail_url", "thumbnailUrl", "image_path", "imagePath", "url"]) val imageUrl: String? = null
) {
    fun toDomain(): NewsItem {
        val resolvedBody = when {
            body.isNotBlank() -> body
            summary.isNotBlank() -> summary
            else -> title
        }
        val resolvedSummary = summary.ifBlank { resolvedBody }
        return NewsItem(
            id = id,
            title = title,
            summary = resolvedSummary,
            body = resolvedBody,
            author = author,
            timestamp = timestamp,
            category = category,
            imageUrl = imageUrl
        )
    }
}

class NewsItemDtoAdapter : JsonDeserializer<NewsItemDto>, JsonSerializer<NewsItemDto> {
    override fun serialize(src: NewsItemDto?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (src == null) return JsonNull.INSTANCE
        val obj = JsonObject()
        obj.addProperty("id", src.id)
        obj.addProperty("title", src.title)
        obj.addProperty("summary", src.summary)
        obj.addProperty("body", src.body)
        obj.addProperty("author", src.author)
        obj.addProperty("timestamp", src.timestamp)
        obj.addProperty("category", src.category)
        if (src.imageUrl != null) {
            obj.addProperty("image_url", src.imageUrl)
        }
        return obj
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NewsItemDto {
        if (!json.isJsonObject) {
            return NewsItemDto()
        }
        val obj = json.asJsonObject

        fun extractText(element: JsonElement?): String {
            if (element == null || element.isJsonNull) return ""
            if (element.isJsonPrimitive) return element.asString.trim()
            if (element.isJsonObject) {
                val o = element.asJsonObject
                val candidate = o.get("rendered")
                    ?: o.get("value")
                    ?: o.get("text")
                    ?: o.get("content")
                    ?: o.get("html")
                    ?: o.get("raw")
                if (candidate != null && !candidate.isJsonNull) {
                    return extractText(candidate)
                }
            }
            if (element.isJsonArray) {
                val arr = element.asJsonArray
                val sb = StringBuilder()
                for (item in arr) {
                    val text = extractText(item)
                    if (text.isNotBlank()) {
                        if (sb.isNotEmpty()) sb.append("\n\n")
                        sb.append(text)
                    }
                }
                return sb.toString().trim()
            }
            return ""
        }

        fun findValue(keys: List<String>): String {
            for (key in keys) {
                if (obj.has(key)) {
                    val text = extractText(obj.get(key))
                    if (text.isNotBlank()) return text
                }
            }
            return ""
        }

        val idKeys = listOf("id", "news_id", "article_id", "articleId", "newsId", "story_id", "storyId")
        var id = 0
        for (k in idKeys) {
            if (obj.has(k)) {
                val el = obj.get(k)
                if (el != null && el.isJsonPrimitive && el.asJsonPrimitive.isNumber) {
                    id = el.asInt
                    break
                } else if (el != null && el.isJsonPrimitive && el.asJsonPrimitive.isString) {
                    id = el.asString.toIntOrNull() ?: 0
                    if (id != 0) break
                }
            }
        }

        val titleKeys = listOf("title", "headline", "subject", "name")
        val title = findValue(titleKeys)

        val authorKeys = listOf("author", "by", "writer", "author_name", "authorName", "created_by", "createdBy", "publisher")
        val author = findValue(authorKeys).ifBlank { "Aurorah CAN Team" }

        val timestampKeys = listOf("timestamp", "created_at", "createdAt", "date", "published_at", "publishedAt", "created_date", "createdDate", "date_created", "dateCreated", "publish_date", "publishDate", "updated_at", "updatedAt")
        val timestamp = findValue(timestampKeys)

        val categoryKeys = listOf("category", "topic", "tag", "section", "category_name", "categoryName")
        val category = findValue(categoryKeys).ifBlank { "General" }

        val imageKeys = listOf("image_url", "imageUrl", "image", "media_url", "mediaUrl", "picture", "picture_url", "pictureUrl", "photo", "photo_url", "photoUrl", "cover_image", "coverImage", "featured_image", "featuredImage", "thumbnail", "thumbnail_url", "thumbnailUrl", "image_path", "imagePath", "url")
        val rawImageUrl = findValue(imageKeys)
        val imageUrl = if (rawImageUrl.isNotBlank()) rawImageUrl else null

        val summaryKeys = listOf("summary", "description", "excerpt", "short_description", "shortDescription", "teaser", "snippet", "intro")
        val candidateSummary = findValue(summaryKeys)

        val bodyKeys = listOf(
            "body", "content", "full_text", "fullText", "text", "article", "details",
            "full_content", "fullContent", "body_text", "bodyText", "body_html", "bodyHtml",
            "html_content", "htmlContent", "html", "message", "main_text", "mainText", "story",
            "post_content", "postContent", "article_content", "articleContent",
            "article_body", "articleBody", "news_content", "newsContent", "news_body", "newsBody",
            "story_content", "storyContent", "story_body", "storyBody", "encoded",
            "content_encoded", "contentEncoded", "full_description", "fullDescription",
            "long_description", "longDescription", "description_full", "descriptionFull",
            "details_html", "full_details", "paragraphs", "content_rendered", "body_rendered"
        )

        var candidateBody = ""
        var maxBodyLength = -1

        for (k in bodyKeys) {
            if (obj.has(k)) {
                val text = extractText(obj.get(k))
                if (text.length > maxBodyLength) {
                    maxBodyLength = text.length
                    candidateBody = text
                }
            }
        }

        for (entry in obj.entrySet()) {
            val key = entry.key
            if (key in idKeys || key in authorKeys || key in timestampKeys || key in categoryKeys || key in imageKeys || key in titleKeys) {
                continue
            }
            val text = extractText(entry.value)
            if (text.length > maxBodyLength) {
                maxBodyLength = text.length
                candidateBody = text
            }
        }

        val finalSummary = candidateSummary
        val finalBody = if (candidateBody.length >= candidateSummary.length && candidateBody.isNotBlank()) {
            candidateBody
        } else if (candidateSummary.isNotBlank()) {
            candidateSummary
        } else {
            title
        }

        return NewsItemDto(
            id = id,
            title = title,
            summary = finalSummary,
            body = finalBody,
            author = author,
            timestamp = timestamp,
            category = category,
            imageUrl = imageUrl
        )
    }
}

data class CommunityNeedDto(
    @SerializedName(value = "needId", alternate = ["need_id", "id"]) val needId: Int = 0,
    @SerializedName(value = "title") val title: String = "",
    @SerializedName(value = "description") val description: String = "",
    @SerializedName(value = "location") val location: String = "",
    @SerializedName(value = "priority") val priority: String = "Low",
    @SerializedName(value = "category") val category: String = "General",
    @SerializedName(value = "status") val status: String = "Open",
    @SerializedName(value = "createdAt", alternate = ["created_at", "date"]) val createdAt: String = ""
) {
    fun toDomain(): CommunityNeed = CommunityNeed(
        needId = needId,
        title = title,
        description = description,
        location = location,
        priority = priority,
        category = category,
        status = status,
        createdAt = createdAt
    )
}

data class DonationResponseDto(
    @SerializedName(value = "success") val success: Boolean = true,
    @SerializedName(value = "transactionRef", alternate = ["transaction_ref", "transaction_id", "ref"]) val transactionRef: String = "",
    @SerializedName(value = "message") val message: String = ""
) {
    fun toDomain(): DonationResponse = DonationResponse(
        success = success,
        transactionRef = transactionRef,
        message = message
    )
}
