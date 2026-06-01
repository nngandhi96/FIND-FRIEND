package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FindFriendDao {

    // --- Search History ---
    @Query("SELECT * FROM search_history WHERE userEmail = :email ORDER BY timestamp DESC")
    fun getSearchHistory(email: String): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchHistory(id: Int)

    @Query("DELETE FROM search_history WHERE userEmail = :email")
    suspend fun clearHistoryForUser(email: String)


    // --- Saved Profiles ---
    @Query("SELECT * FROM saved_profiles WHERE userEmail = :email ORDER BY timestamp DESC")
    fun getSavedProfiles(email: String): Flow<List<SavedProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedProfile(profile: SavedProfileEntity)

    @Query("DELETE FROM saved_profiles WHERE profileId = :profileId AND userEmail = :email")
    suspend fun deleteSavedProfile(profileId: String, email: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_profiles WHERE profileId = :profileId AND userEmail = :email)")
    suspend fun isProfileSaved(profileId: String, email: String): Boolean


    // --- Abuse Reports ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbuseReport(report: AbuseReportEntity)
}
