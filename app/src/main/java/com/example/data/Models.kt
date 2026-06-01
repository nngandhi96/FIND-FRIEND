package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocialProfile(
    val id: String,
    val name: String,
    val username: String,
    val platformName: String, // "Instagram", "Facebook", "X", "LinkedIn", "Snapchat", etc.
    val profileLink: String,
    val profilePhotoUrl: String,
    val confidence: Int     // 0-100 percentage
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val timestamp: Long,
    val imagePath: String, // String representation or placeholder drawable
    val nameHint: String,
    val cityHint: String,
    val schoolHint: String,
    val resultsJson: String // Serialized List<SocialProfile>
)

@Entity(tableName = "saved_profiles")
data class SavedProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val profileId: String,
    val name: String,
    val username: String,
    val platformName: String,
    val profileLink: String,
    val profilePhotoUrl: String,
    val confidence: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "abuse_reports")
data class AbuseReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val profileName: String,
    val platformName: String,
    val profileLink: String,
    val reason: String,
    val comments: String,
    val timestamp: Long = System.currentTimeMillis()
)
