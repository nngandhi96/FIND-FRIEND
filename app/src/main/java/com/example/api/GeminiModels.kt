package com.example.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<ContentJson>,
    val generationConfig: GenerationConfigJson? = null,
    val systemInstruction: ContentJson? = null
)

@JsonClass(generateAdapter = true)
data class ContentJson(
    val parts: List<PartJson>
)

@JsonClass(generateAdapter = true)
data class PartJson(
    val text: String? = null,
    val inlineData: InlineDataJson? = null
)

@JsonClass(generateAdapter = true)
data class InlineDataJson(
    val mimeType: String,
    val data: String // Base64 encoded string
)

@JsonClass(generateAdapter = true)
data class GenerationConfigJson(
    val responseMimeType: String? = null, // e.g. "application/json"
    val temperature: Float? = null,
    val topP: Float? = null,
    val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<CandidateJson>?
)

@JsonClass(generateAdapter = true)
data class CandidateJson(
    val content: ContentJson?
)
