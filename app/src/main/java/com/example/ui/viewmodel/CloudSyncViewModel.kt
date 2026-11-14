package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.local.HifzProgressEntity
import com.example.data.local.LessonProgressEntity
import com.example.data.local.UserPreferencesState
import com.example.firebase.CloudHifzProgress
import com.example.firebase.CloudLearningProgress
import com.example.firebase.CloudQuranBookmark
import com.example.firebase.CloudSyncRepository
import com.example.firebase.UserSyncProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class CloudSyncUiState(
    val profile: UserSyncProfile? = null,
    val cloudBookmarks: List<CloudQuranBookmark> = emptyList(),
    val cloudHifz: List<CloudHifzProgress> = emptyList(),
    val cloudLessons: List<CloudLearningProgress> = emptyList(),
    val isSyncing: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
)

/**
 * Only instantiated inside the authenticated UI gate (`if (currentUser != null) key(currentUser.uid)`).
 */
class CloudSyncViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = CloudSyncRepository(application)
    private val _state = MutableStateFlow(CloudSyncUiState())
    val state: StateFlow<CloudSyncUiState> = _state.asStateFlow()

    init {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            observeCloudCollections(uid)
        }
    }

    private fun observeCloudCollections(uid: String) {
        viewModelScope.launch {
            repo.observeUserProfile(uid)
                .catch { e -> _state.value = _state.value.copy(errorMessage = e.localizedMessage) }
                .collect { profile -> _state.value = _state.value.copy(profile = profile) }
        }
        viewModelScope.launch {
            repo.observeBookmarks(uid)
                .catch { e -> _state.value = _state.value.copy(errorMessage = e.localizedMessage) }
                .collect { items -> _state.value = _state.value.copy(cloudBookmarks = items) }
        }
        viewModelScope.launch {
            repo.observeHifzProgress(uid)
                .catch { e -> _state.value = _state.value.copy(errorMessage = e.localizedMessage) }
                .collect { items -> _state.value = _state.value.copy(cloudHifz = items) }
        }
        viewModelScope.launch {
            repo.observeLearningProgress(uid)
                .catch { e -> _state.value = _state.value.copy(errorMessage = e.localizedMessage) }
                .collect { items -> _state.value = _state.value.copy(cloudLessons = items) }
        }
    }

    fun syncNow(
        displayName: String,
        prefs: UserPreferencesState,
        localBookmarks: List<BookmarkEntity>,
        localHifz: List<HifzProgressEntity>,
        localLessons: List<LessonProgressEntity>,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSyncing = true, statusMessage = null, errorMessage = null)
            val totalHifzAyahs = localHifz.sumOf { it.memorizedAyahs }
            val profileRes = repo.upsertUserProfile(
                displayName = displayName,
                lastSurah = prefs.lastReadSurah,
                lastAyah = prefs.lastReadAyah,
                totalTasbihCount = prefs.totalTasbihCount,
                hifzAyahsCount = totalHifzAyahs,
                preferredTheme = prefs.themePreset.key,
                preferredLanguage = prefs.appLanguage.code,
            )
            localBookmarks.forEach { b ->
                repo.saveBookmark(b.surahNumber, b.ayahNumber, b.surahName, b.note.ifBlank { null })
            }
            localHifz.forEach { h ->
                repo.saveHifzProgress(h.surahNumber, h.memorizedAyahs, h.targetAyahs, h.status)
            }
            localLessons.forEach { l ->
                repo.saveLearningProgress(l.moduleType, l.lessonKey, l.completed, l.quizScore)
            }
            _state.value = _state.value.copy(
                isSyncing = false,
                statusMessage = if (profileRes.isSuccess) {
                    "Synced ${localBookmarks.size} bookmarks & ${localHifz.size} Hifz records to Cloud."
                } else {
                    profileRes.exceptionOrNull()?.localizedMessage ?: "Cloud sync encountered an issue."
                }
            )
        }
    }

    fun deleteCloudBookmark(surahNumber: Int, ayahNumber: Int) {
        viewModelScope.launch {
            repo.deleteBookmark("s${surahNumber}_a${ayahNumber}")
        }
    }
}
