package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: FindFriendDao) {

    fun getSearchHistory(email: String): Flow<List<SearchHistoryEntity>> {
        return dao.getSearchHistory(email)
    }

    suspend fun insertSearchHistory(history: SearchHistoryEntity) {
        dao.insertSearchHistory(history)
    }

    suspend fun deleteSearchHistory(id: Int) {
        dao.deleteSearchHistory(id)
    }

    suspend fun clearHistoryForUser(email: String) {
        dao.clearHistoryForUser(email)
    }

    fun getSavedProfiles(email: String): Flow<List<SavedProfileEntity>> {
        return dao.getSavedProfiles(email)
    }

    suspend fun insertSavedProfile(profile: SavedProfileEntity) {
        dao.insertSavedProfile(profile)
    }

    suspend fun deleteSavedProfile(profileId: String, email: String) {
        dao.deleteSavedProfile(profileId, email)
    }

    suspend fun isProfileSaved(profileId: String, email: String): Boolean {
        return dao.isProfileSaved(profileId, email)
    }

    suspend fun insertAbuseReport(report: AbuseReportEntity) {
        dao.insertAbuseReport(report)
    }
}
