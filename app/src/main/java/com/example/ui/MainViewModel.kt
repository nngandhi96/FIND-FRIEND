package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.auth.AuthManager
import com.example.auth.UserSession
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Processing : SearchState()
    data class Success(val results: List<SocialProfile>) : SearchState()
    data class Error(val message: String) : SearchState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel"

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.findFriendDao())

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val profileListType = Types.newParameterizedType(List::class.java, SocialProfile::class.java)
    private val profileListAdapter = moshi.adapter<List<SocialProfile>>(profileListType)

    // Auth Session
    val currentUser: StateFlow<UserSession?> = AuthManager.currentUser

    // Search Engine State
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    // Temporary active search parameters for result display
    private val _activePhoto = MutableStateFlow<Bitmap?>(null)
    val activePhoto: StateFlow<Bitmap?> = _activePhoto.asStateFlow()

    // To handle online photo urls or local selection
    private val _activePhotoUrl = MutableStateFlow<String?>(null)
    val activePhotoUrl: StateFlow<String?> = _activePhotoUrl.asStateFlow()

    private val _nameHint = MutableStateFlow("")
    val nameHint: StateFlow<String> = _nameHint.asStateFlow()

    private val _cityHint = MutableStateFlow("")
    val cityHint: StateFlow<String> = _cityHint.asStateFlow()

    private val _schoolHint = MutableStateFlow("")
    val schoolHint: StateFlow<String> = _schoolHint.asStateFlow()

    // Reactive streams backed by Room database and scoped to current user
    val searchHistory: StateFlow<List<SearchHistoryEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getSearchHistory(user.email)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedProfiles: StateFlow<List<SavedProfileEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getSavedProfiles(user.email)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        AuthManager.init(application)
    }

    fun setHints(name: String, city: String, school: String) {
        _nameHint.value = name
        _cityHint.value = city
        _schoolHint.value = school
    }

    fun selectPhoto(bitmap: Bitmap?, sampleUrl: String? = null) {
        _activePhoto.value = bitmap
        _activePhotoUrl.value = sampleUrl
    }

    fun clearSearchInput() {
        _activePhoto.value = null
        _activePhotoUrl.value = null
        _nameHint.value = ""
        _cityHint.value = ""
        _schoolHint.value = ""
        _searchState.value = SearchState.Idle
    }

    /**
     * Executes the ethical AI search engine matches.
     */
    fun performSearch() {
        val user = currentUser.value
        if (user == null) {
            _searchState.value = SearchState.Error("You must be logged in to perform searches.")
            return
        }

        viewModelScope.launch {
            _searchState.value = SearchState.Processing
            try {
                // Call Gemini Client matching engine
                val results = GeminiClient.findMatchingProfiles(
                    _activePhoto.value,
                    _nameHint.value,
                    _cityHint.value,
                    _schoolHint.value
                )

                _searchState.value = SearchState.Success(results)

                // Save search history in Room
                val jsonResult = profileListAdapter.toJson(results) ?: "[]"
                val history = SearchHistoryEntity(
                    userEmail = user.email,
                    timestamp = System.currentTimeMillis(),
                    imagePath = _activePhotoUrl.value ?: "custom_uploaded_photo",
                    nameHint = _nameHint.value,
                    cityHint = _cityHint.value,
                    schoolHint = _schoolHint.value,
                    resultsJson = jsonResult
                )
                repository.insertSearchHistory(history)

            } catch (e: Exception) {
                Log.e(TAG, "Search operation failed", e)
                _searchState.value = SearchState.Error(e.message ?: "An unexpected error occurred during search.")
            }
        }
    }

    /**
     * History lists database converters
     */
    fun parseResultJson(json: String): List<SocialProfile> {
        return try {
            profileListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun deleteHistoryId(id: Int) {
        viewModelScope.launch {
            repository.deleteSearchHistory(id)
        }
    }

    fun clearUserHistory() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.clearHistoryForUser(user.email)
        }
    }

    // --- Saved Profiles Management ---
    fun toggleSaveProfile(profile: SocialProfile) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val isCurrentlySaved = repository.isProfileSaved(profile.id, user.email)
            if (isCurrentlySaved) {
                repository.deleteSavedProfile(profile.id, user.email)
            } else {
                val entity = SavedProfileEntity(
                    userEmail = user.email,
                    profileId = profile.id,
                    name = profile.name,
                    username = profile.username,
                    platformName = profile.platformName,
                    profileLink = profile.profileLink,
                    profilePhotoUrl = profile.profilePhotoUrl,
                    confidence = profile.confidence
                )
                repository.insertSavedProfile(entity)
            }
        }
    }

    fun removeSavedProfile(profileId: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.deleteSavedProfile(profileId, user.email)
        }
    }

    fun isSaved(profileId: String, callback: (Boolean) -> Unit) {
        val user = currentUser.value
        if (user == null) {
            callback(false)
            return
        }
        viewModelScope.launch {
            val result = repository.isProfileSaved(profileId, user.email)
            callback(result)
        }
    }

    // --- Abuse & Support reporting ---
    fun submitAbuseReport(
        profileName: String,
        platformName: String,
        profileLink: String,
        reason: String,
        comments: String,
        onComplete: (Boolean) -> Unit
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            try {
                val report = AbuseReportEntity(
                    userEmail = user.email,
                    profileName = profileName,
                    platformName = platformName,
                    profileLink = profileLink,
                    reason = reason,
                    comments = comments
                )
                repository.insertAbuseReport(report)
                onComplete(true)
            } catch (e: Exception) {
                Log.e(TAG, "Abuse report submission failed", e)
                onComplete(false)
            }
        }
    }
}
