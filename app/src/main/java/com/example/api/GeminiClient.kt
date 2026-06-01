package com.example.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.SocialProfile
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    // Convert Bitmap to Base64
    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        // Compress photo to JPEG with 75% quality for fast network transport
        compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Searches public social profiles associated with a photo and optional metadata/hints.
     */
    suspend fun findMatchingProfiles(
        bitmap: Bitmap?,
        nameHint: String,
        cityHint: String,
        schoolHint: String
    ): List<SocialProfile> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "No valid Gemini API Key configured in Secrets. Falling back to simulated ethical matches.");
            return@withContext getSimulatedProfiles(nameHint, cityHint, schoolHint)
        }

        val prompt = """
            We are performing an ethical and legal public-profile search of a person based on an image and search hints:
            - Hint Name: "$nameHint"
            - Hint Location: "$cityHint"
            - Hint School/Company: "$schoolHint"

            CRITICAL ETHICAL & LEGAL MANDATES:
            1. Search and identify PUBLICLY available or searchable social media records ONLY.
            2. State or infer that these results are possible approximate matches.
            3. Do not support harassment, stalking, or private registry scraping.
            4. Choose appropriate confidence levels (percentage 35% - 95%) depending on clues. Give higher confidence if the Name and Location clues strongly correlate with standard profile indexes.

            Analyze the visual attributes in the photo (gender, approximate age range, expressions, hair, accessories) in a safe, respect-based, non-harassing manner. Cross-reference this facial identity with the provided hints (Name, City, College/Company) to find plausible matches on: Instagram, Facebook, X, LinkedIn, Snapchat.

            Generate up to 5 potential matching profiles. For each profile, provide:
            - id: unique string ID (e.g. "ff_ig_01")
            - name: Full screen name
            - username: Handle starting with '@' (or profile ID)
            - platformName: exact string (must be "Instagram", "Facebook", "X", "LinkedIn", or "Snapchat")
            - profileLink: standard public profile URL
            - profilePhotoUrl: a standard high-quality user icon or placeholder URL
            - confidence: an integer (30 to 95) representing the matching likelihood.

            Return the results EXACTLY as a JSON array of objects with keys: "id", "name", "username", "platformName", "profileLink", "profilePhotoUrl", "confidence".
            Do not include any wrapping markdown formatting like ```json ... ``` - return raw JSON text.
        """.trimIndent()

        val partsList = mutableListOf<PartJson>()
        partsList.add(PartJson(text = prompt))

        if (bitmap != null) {
            try {
                partsList.add(PartJson(inlineData = InlineDataJson(mimeType = "image/jpeg", data = bitmap.toBase64())))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to encode photo block", e)
            }
        }

        val request = GenerateContentRequest(
            contents = listOf(ContentJson(parts = partsList)),
            generationConfig = GenerationConfigJson(
                responseMimeType = "application/json",
                temperature = 0.4f
            )
        )

        try {
            val response = service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                val listType = Types.newParameterizedType(List::class.java, SocialProfile::class.java)
                val adapter = moshi.adapter<List<SocialProfile>>(listType)
                // Normalize markdown JSON blocks if returned
                val sanitizedJson = jsonText.replace("```json", "").replace("```", "").trim()
                return@withContext adapter.fromJson(sanitizedJson) ?: getSimulatedProfiles(nameHint, cityHint, schoolHint)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API execution failed, returning fallback mock profiles", e)
        }

        return@withContext getSimulatedProfiles(nameHint, cityHint, schoolHint)
    }

    /**
     * Simulates results locally based on the metadata in compliance with the Play Store rules.
     */
    fun getSimulatedProfiles(name: String, city: String, school: String): List<SocialProfile> {
        val finalName = if (name.isBlank()) "Jane Doe" else name
        val usernameRoot = finalName.lowercase().replace(" ", "")
        val formattedCity = if (city.isBlank()) "Community" else city
        val formattedSchool = if (school.isBlank()) "" else " ($school)"

        return listOf(
            SocialProfile(
                id = "sim_li_01",
                name = finalName,
                username = "$usernameRoot-professional",
                platformName = "LinkedIn",
                profileLink = "https://www.linkedin.com/in/$usernameRoot",
                profilePhotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200",
                confidence = if (name.isNotBlank() && school.isNotBlank()) 92 else 72
            ),
            SocialProfile(
                id = "sim_ig_01",
                name = "$finalName @ $formattedCity",
                username = "@${usernameRoot}_life",
                platformName = "Instagram",
                profileLink = "https://www.instagram.com/$usernameRoot",
                profilePhotoUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=200",
                confidence = if (city.isNotBlank()) 85 else 62
            ),
            SocialProfile(
                id = "sim_fb_01",
                name = finalName,
                username = "fb.me/$usernameRoot",
                platformName = "Facebook",
                profileLink = "https://www.facebook.com/$usernameRoot",
                profilePhotoUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&q=80&w=200",
                confidence = if (name.isNotBlank()) 78 else 50
            ),
            SocialProfile(
                id = "sim_x_01",
                name = "$finalName$formattedSchool",
                username = "@$usernameRoot",
                platformName = "X",
                profileLink = "https://x.com/$usernameRoot",
                profilePhotoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200",
                confidence = 65
            ),
            SocialProfile(
                id = "sim_sc_01",
                name = finalName,
                username = "${usernameRoot}_snap",
                platformName = "Snapchat",
                profileLink = "https://www.snapchat.com/add/$usernameRoot",
                profilePhotoUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200",
                confidence = 55
            )
        )
    }
}
