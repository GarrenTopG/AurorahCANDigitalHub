package com.aurorah.can.data.model

import com.google.gson.annotations.SerializedName

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
    fun toDomain(): NewsItem = NewsItem(
        id = id,
        title = title,
        summary = summary,
        body = body,
        author = author,
        timestamp = timestamp,
        category = category,
        imageUrl = imageUrl
    )
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
