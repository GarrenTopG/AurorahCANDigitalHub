package com.aurorah.can

import com.aurorah.can.data.model.*
import com.aurorah.can.util.NewsUtils
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelsSerializationTest {

    private val gson = Gson()

    @Test
    fun user_deserializesSnakeCaseAndCamelCase() {
        val snakeCaseJson = """
            {
                "user_id": 42,
                "full_name": "Jane Doe",
                "email": "jane@aurorahcan.org",
                "user_role": "Donor",
                "access_token": "token-abc"
            }
        """.trimIndent()

        val userFromSnake = gson.fromJson(snakeCaseJson, User::class.java)
        assertEquals(42, userFromSnake.userId)
        assertEquals("Jane Doe", userFromSnake.fullName)
        assertEquals("jane@aurorahcan.org", userFromSnake.email)
        assertEquals("Donor", userFromSnake.role)
        assertEquals("token-abc", userFromSnake.token)

        val camelCaseJson = """
            {
                "userId": 10,
                "fullName": "John Smith",
                "email": "john@aurorahcan.org",
                "role": "Volunteer",
                "token": "token-123"
            }
        """.trimIndent()

        val userFromCamel = gson.fromJson(camelCaseJson, User::class.java)
        assertEquals(10, userFromCamel.userId)
        assertEquals("John Smith", userFromCamel.fullName)
    }

    @Test
    fun communityNeed_deserializesSnakeCaseAndCamelCase() {
        val json = """
            {
                "need_id": 101,
                "title": "Food Parcel Request",
                "description": "Family in need of staple foods",
                "location": "Soweto, Johannesburg",
                "priority": "High",
                "category": "Food",
                "status": "Open",
                "created_at": "2026-03-30"
            }
        """.trimIndent()

        val need = gson.fromJson(json, CommunityNeed::class.java)
        assertEquals(101, need.needId)
        assertEquals("Food Parcel Request", need.title)
        assertEquals("Soweto, Johannesburg", need.location)
        assertEquals("2026-03-30", need.createdAt)
    }

    @Test
    fun donationResponse_deserializesSnakeCase() {
        val json = """
            {
                "success": true,
                "transaction_ref": "TXN-2026-999",
                "message": "Donation received successfully"
            }
        """.trimIndent()

        val donationResponse = gson.fromJson(json, DonationResponse::class.java)
        assertTrue(donationResponse.success)
        assertEquals("TXN-2026-999", donationResponse.transactionRef)
        assertEquals("Donation received successfully", donationResponse.message)
    }

    @Test
    fun assistanceRequest_serializesAndDeserializesCorrectly() {
        val request = AssistanceRequest(
            category = "Food",
            urgency = "Critical",
            areaLocation = "Alexandra, Johannesburg",
            contactPhone = "+27821112222",
            description = "Emergency food parcel required for family of 5",
            privacyConsent = true,
            userEmail = "applicant@aurorahcan.org"
        )
        val json = gson.toJson(request)
        val deserialized = gson.fromJson(json, AssistanceRequest::class.java)

        assertEquals("Food", deserialized.category)
        assertEquals("Critical", deserialized.urgency)
        assertEquals("Alexandra, Johannesburg", deserialized.areaLocation)
        assertEquals("+27821112222", deserialized.contactPhone)
        assertTrue(deserialized.privacyConsent)
    }

    @Test
    fun donationRequest_inKindAndMonetary_serializesAndDeserializesCorrectly() {
        val monetary = DonationRequest(
            donationType = "Monetary",
            category = "General Fund",
            amount = 250.0,
            paymentMethod = "Credit Card",
            userEmail = "donor@aurorahcan.org"
        )
        val monetaryJson = gson.toJson(monetary)
        val deserializedMonetary = gson.fromJson(monetaryJson, DonationRequest::class.java)
        assertEquals("Monetary", deserializedMonetary.donationType)
        assertEquals(250.0, deserializedMonetary.amount!!, 0.01)

        val inKind = DonationRequest(
            donationType = "In-Kind",
            category = "Clothing",
            itemDescription = "10 winter coats and thermal blankets",
            preference = "Drop-off at Hub",
            userEmail = "donor@aurorahcan.org"
        )
        val inKindJson = gson.toJson(inKind)
        val deserializedInKind = gson.fromJson(inKindJson, DonationRequest::class.java)
        assertEquals("In-Kind", deserializedInKind.donationType)
        assertEquals("10 winter coats and thermal blankets", deserializedInKind.itemDescription)
        assertEquals("Drop-off at Hub", deserializedInKind.preference)
    }

    @Test
    fun newsItem_deserializesAuthorAndBody() {
        val json = """
            {
                "id": 5,
                "title": "Community Health Drive",
                "summary": "Free wellness checks scheduled",
                "content": "Full detailed report on community health initiatives across sectors.",
                "author": "Dr. Sarah Jenkins",
                "timestamp": "2026-03-31",
                "category": "Healthcare",
                "image_url": "https://example.com/health.jpg"
            }
        """.trimIndent()

        val news = gson.fromJson(json, NewsItem::class.java)
        assertEquals("Community Health Drive", news.title)
        assertEquals("Full detailed report on community health initiatives across sectors.", news.body)
        assertEquals("Dr. Sarah Jenkins", news.author)
        assertEquals("https://example.com/health.jpg", news.imageUrl)
    }

    @Test
    fun newsItem_deserializesAzureSqlDbVariations() {
        val azureSqlJson = """
            {
                "news_id": 12,
                "headline": "Celebrating Mandela Day and Four Years of the Community Hotspot Library",
                "description": "Aurorah marked Mandela Day and four years of its Community Hotspot Library with a day of service, learning and connection.",
                "details": "<p>Aurorah marked Mandela Day and four years of its Community Hotspot Library with a day of service, learning and connection.</p><p>Volunteers and youth gathered to celebrate this milestone.</p>",
                "author_name": "Aurorah CAN Team",
                "created_at": "2026-10-18T08:22:22.497000",
                "cover_image": "images/mandela-day.jpg"
            }
        """.trimIndent()

        val news = gson.fromJson(azureSqlJson, NewsItem::class.java)
        assertEquals("Celebrating Mandela Day and Four Years of the Community Hotspot Library", news.title)
        assertEquals("Aurorah marked Mandela Day and four years of its Community Hotspot Library with a day of service, learning and connection.", news.summary)
        assertTrue(news.body.contains("Volunteers and youth gathered"))
        assertEquals("Aurorah CAN Team", news.author)
        assertEquals("2026-10-18T08:22:22.497000", news.timestamp)
        assertEquals("images/mandela-day.jpg", news.imageUrl)

        val formattedDate = NewsUtils.formatNewsDate(news.timestamp)
        assertEquals("18 Oct 2026", formattedDate)

        val resolvedImage = NewsUtils.resolveImageUrl(news.imageUrl)
        assertEquals("https://aurorah-web-hub.onrender.com/images/mandela-day.jpg", resolvedImage)

        val parsedBody = NewsUtils.parseHtmlContent(news.body).toString()
        assertTrue(parsedBody.contains("Aurorah marked Mandela Day"))
    }

    @Test
    fun newsItem_resolvesRelativeImageUrlsAndHttpToHttps() {
        val relativeLeadingSlash = "/static/images/news/example.png"
        assertEquals(
            "https://aurorah-web-hub.onrender.com/static/images/news/example.png",
            NewsUtils.resolveImageUrl(relativeLeadingSlash)
        )

        val relativeNoLeadingSlash = "static/images/news/example.png"
        assertEquals(
            "https://aurorah-web-hub.onrender.com/static/images/news/example.png",
            NewsUtils.resolveImageUrl(relativeNoLeadingSlash)
        )

        val httpUrl = "http://aurorahcan.org.za/static/images/news/example.png"
        assertEquals(
            "https://aurorah-web-hub.onrender.com/static/images/news/example.png",
            NewsUtils.resolveImageUrl(httpUrl)
        )

        val httpsUrl = "https://aurorahcan.org.za/static/images/news/example.png"
        assertEquals(
            "https://aurorah-web-hub.onrender.com/static/images/news/example.png",
            NewsUtils.resolveImageUrl(httpsUrl)
        )

        val externalHttps = "https://images.unsplash.com/photo-12345"
        assertEquals(
            "https://images.unsplash.com/photo-12345",
            NewsUtils.resolveImageUrl(externalHttps)
        )

        val protocolRelative = "//aurorah-web-hub.onrender.com/static/img.png"
        assertEquals(
            "https://aurorah-web-hub.onrender.com/static/img.png",
            NewsUtils.resolveImageUrl(protocolRelative)
        )

        val nullUrl: String? = null
        assertEquals(null, NewsUtils.resolveImageUrl(nullUrl))
        assertEquals(null, NewsUtils.resolveImageUrl("   "))
    }

    @Test
    fun updateProfileRequest_serializesAndDeserializesCorrectly() {
        val request = UpdateProfileRequest(
            fullName = "Sarah Connor",
            phone = "+27839998888",
            email = "sarah@aurorahcan.org"
        )
        val json = gson.toJson(request)
        val deserialized = gson.fromJson(json, UpdateProfileRequest::class.java)
        assertEquals("Sarah Connor", deserialized.fullName)
        assertEquals("+27839998888", deserialized.phone)
        assertEquals("sarah@aurorahcan.org", deserialized.email)
    }
}
