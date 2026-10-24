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
    fun userDto_deserializesSnakeCaseAndCamelCase_andMapsToDomain() {
        val snakeCaseJson = """
            {
                "user_id": 42,
                "full_name": "Jane Doe",
                "email": "jane@aurorahcan.org",
                "user_role": "Donor",
                "access_token": "token-abc"
            }
        """.trimIndent()

        val userDtoFromSnake = gson.fromJson(snakeCaseJson, UserDto::class.java)
        assertEquals(42, userDtoFromSnake.userId)
        assertEquals("Jane Doe", userDtoFromSnake.fullName)
        assertEquals("jane@aurorahcan.org", userDtoFromSnake.email)
        assertEquals("Donor", userDtoFromSnake.role)
        assertEquals("token-abc", userDtoFromSnake.token)

        val userDomainFromSnake = userDtoFromSnake.toDomain()
        assertEquals(42, userDomainFromSnake.userId)
        assertEquals("Jane Doe", userDomainFromSnake.fullName)
        assertEquals("jane@aurorahcan.org", userDomainFromSnake.email)

        val camelCaseJson = """
            {
                "userId": 10,
                "fullName": "John Smith",
                "email": "john@aurorahcan.org",
                "role": "Volunteer",
                "token": "token-123"
            }
        """.trimIndent()

        val userDtoFromCamel = gson.fromJson(camelCaseJson, UserDto::class.java)
        assertEquals(10, userDtoFromCamel.userId)
        assertEquals("John Smith", userDtoFromCamel.fullName)

        val userDomainFromCamel = userDtoFromCamel.toDomain()
        assertEquals(10, userDomainFromCamel.userId)
        assertEquals("John Smith", userDomainFromCamel.fullName)
    }

    @Test
    fun communityNeedDto_deserializesSnakeCaseAndCamelCase_andMapsToDomain() {
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

        val needDto = gson.fromJson(json, CommunityNeedDto::class.java)
        assertEquals(101, needDto.needId)
        assertEquals("Food Parcel Request", needDto.title)
        assertEquals("Soweto, Johannesburg", needDto.location)
        assertEquals("2026-03-30", needDto.createdAt)

        val needDomain = needDto.toDomain()
        assertEquals(101, needDomain.needId)
        assertEquals("Food Parcel Request", needDomain.title)
    }

    @Test
    fun donationResponseDto_deserializesSnakeCase_andMapsToDomain() {
        val json = """
            {
                "success": true,
                "transaction_ref": "TXN-2026-999",
                "message": "Donation received successfully"
            }
        """.trimIndent()

        val donationResponseDto = gson.fromJson(json, DonationResponseDto::class.java)
        assertTrue(donationResponseDto.success)
        assertEquals("TXN-2026-999", donationResponseDto.transactionRef)
        assertEquals("Donation received successfully", donationResponseDto.message)

        val domain = donationResponseDto.toDomain()
        assertTrue(domain.success)
        assertEquals("TXN-2026-999", domain.transactionRef)
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
    fun newsItemDto_deserializesAuthorAndBody_andMapsToDomain() {
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

        val newsDto = gson.fromJson(json, NewsItemDto::class.java)
        assertEquals("Community Health Drive", newsDto.title)
        assertEquals("Full detailed report on community health initiatives across sectors.", newsDto.body)
        assertEquals("Dr. Sarah Jenkins", newsDto.author)
        assertEquals("https://example.com/health.jpg", newsDto.imageUrl)

        val newsDomain = newsDto.toDomain()
        assertEquals("Community Health Drive", newsDomain.title)
        assertEquals("Full detailed report on community health initiatives across sectors.", newsDomain.body)
        assertEquals("Dr. Sarah Jenkins", newsDomain.author)
    }

    @Test
    fun newsItemDto_deserializesAzureSqlDbVariations() {
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

        val newsDto = gson.fromJson(azureSqlJson, NewsItemDto::class.java)
        val news = newsDto.toDomain()
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
    fun newsItemDto_deserializesWordPressRenderedObjects() {
        val wpJson = """
            {
                "id": 99,
                "title": { "rendered": "A Win Worth Celebrating: Three More Youth Secure Full-Time Contracts" },
                "excerpt": { "rendered": "Three more young people secured one-year full-time contracts in Aurorah's youth programme, bringing the number who had moved forward to eight." },
                "content": { "rendered": "<p>Aurorah is celebrating three more young people who secured one-year full-time contracts.</p><p>The programme began with 50 young people on 15 July. With these contracts, eight had moved forward into employment opportunities, while 42 remained on the journey. The update celebrated each step and encouraged the young people still participating to keep showing up, learning and growing.</p><p>Congratulations to the three young people. Their progress shows what can happen when young people are given opportunities, support and a chance to believe in themselves.</p>" },
                "author_name": "Aurorah CAN Team",
                "date": "2026-10-18T11:59:00"
            }
        """.trimIndent()

        val newsDto = gson.fromJson(wpJson, NewsItemDto::class.java)
        val domain = newsDto.toDomain()

        assertEquals("A Win Worth Celebrating: Three More Youth Secure Full-Time Contracts", domain.title)
        assertTrue(domain.summary.contains("Three more young people secured one-year full-time contracts"))
        assertTrue(domain.body.contains("Aurorah is celebrating three more young people"))
        assertTrue(domain.body.contains("The programme began with 50 young people"))
        assertTrue(domain.body.contains("Congratulations to the three young people"))

        val parsedCharSequence = NewsUtils.parseHtmlContent(domain.body)
        val parsedString = parsedCharSequence.toString()
        assertTrue(parsedString.contains("Aurorah is celebrating"))
        assertTrue(parsedString.contains("The programme began with 50 young people"))
    }

    @Test
    fun newsItemUtils_formatsPlainTextParagraphsWithSpacing() {
        val plainText = "Paragraph 1 is here.\n\nParagraph 2 is here."
        val parsed = NewsUtils.parseHtmlContent(plainText).toString()
        assertTrue(parsed.contains("Paragraph 1 is here."))
        assertTrue(parsed.contains("Paragraph 2 is here."))
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
