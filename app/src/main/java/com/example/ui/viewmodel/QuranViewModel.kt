package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.AyahDisplayItem
import com.example.data.repository.QuranRepository
import com.example.data.seed.QuranMetadata
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

sealed interface SurahLoadState {
    data object Loading : SurahLoadState
    data object Ready : SurahLoadState
    data class Error(val message: String) : SurahLoadState
}

@OptIn(ExperimentalCoroutinesApi::class)
class QuranViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).appDao()
    private val repo = QuranRepository(dao, application)

    private val _surahNumber = MutableStateFlow(1)
    val surahNumber: StateFlow<Int> = _surahNumber.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _translationEdition = MutableStateFlow("en.sahih")
    val translationEdition: StateFlow<String> = _translationEdition.asStateFlow()

    private val _loadState = MutableStateFlow<SurahLoadState>(SurahLoadState.Loading)
    val loadState: StateFlow<SurahLoadState> = _loadState.asStateFlow()

    private val _pageLoadState = MutableStateFlow<SurahLoadState>(SurahLoadState.Loading)
    val pageLoadState: StateFlow<SurahLoadState> = _pageLoadState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<AyahDisplayItem>>(emptyList())
    val searchResults: StateFlow<List<AyahDisplayItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    val ayahsFlow: Flow<List<AyahDisplayItem>> = combine(
        _surahNumber,
        _translationEdition
    ) { surah, edition ->
        surah to edition
    }.flatMapLatest { (surah, edition) ->
        repo.observeSurahAyahs(surah, edition)
    }

    val currentPageAyahsFlow: Flow<List<AyahDisplayItem>> = combine(
        _currentPage,
        _translationEdition
    ) { page, edition ->
        page to edition
    }.flatMapLatest { (page, edition) ->
        repo.observePageAyahs(page, edition)
    }

    fun observePage(page: Int, edition: String): Flow<List<AyahDisplayItem>> =
        repo.observePageAyahs(page, edition)

    fun selectSurah(surahNum: Int, edition: String) {
        val clamped = surahNum.coerceIn(1, 114)
        _surahNumber.value = clamped
        _translationEdition.value = edition
        loadCurrentSurah(clamped, edition)
    }

    fun selectPage(pageNum: Int, edition: String) {
        val clamped = pageNum.coerceIn(1, QuranMetadata.TOTAL_MUSHAF_PAGES)
        _currentPage.value = clamped
        _translationEdition.value = edition
        loadCurrentPage(clamped, edition)
    }

    fun openSurahAndResolvePage(surahNum: Int, ayahNum: Int, explicitPage: Int?, edition: String) {
        val clampedSurah = surahNum.coerceIn(1, 114)
        _surahNumber.value = clampedSurah
        _translationEdition.value = edition
        viewModelScope.launch {
            val targetPage = if (explicitPage != null && explicitPage in 1..QuranMetadata.TOTAL_MUSHAF_PAGES) {
                explicitPage
            } else {
                repo.resolvePageForAyah(clampedSurah, ayahNum)
            }
            _currentPage.value = targetPage
            loadCurrentPage(targetPage, edition)
            loadCurrentSurah(clampedSurah, edition)
        }
    }

    fun updateEdition(edition: String) {
        if (_translationEdition.value != edition) {
            _translationEdition.value = edition
            loadCurrentSurah(_surahNumber.value, edition)
            loadCurrentPage(_currentPage.value, edition)
        }
    }

    fun ensurePageLoaded(pageNum: Int, edition: String) {
        val clamped = pageNum.coerceIn(1, QuranMetadata.TOTAL_MUSHAF_PAGES)
        viewModelScope.launch {
            repo.loadPageIfNeeded(clamped, edition)
        }
    }

    fun retryLoad() {
        loadCurrentSurah(_surahNumber.value, _translationEdition.value)
        loadCurrentPage(_currentPage.value, _translationEdition.value)
    }

    private fun loadCurrentSurah(surahNum: Int, edition: String) {
        viewModelScope.launch {
            _loadState.value = SurahLoadState.Loading
            val res = repo.loadSurahIfNeeded(surahNum, edition)
            _loadState.value = res.fold(
                onSuccess = { SurahLoadState.Ready },
                onFailure = { SurahLoadState.Error(it.localizedMessage ?: "Unable to fetch Surah text") }
            )
        }
    }

    private fun loadCurrentPage(pageNum: Int, edition: String) {
        viewModelScope.launch {
            _pageLoadState.value = SurahLoadState.Loading
            val res = repo.loadPageIfNeeded(pageNum, edition)
            _pageLoadState.value = res.fold(
                onSuccess = { SurahLoadState.Ready },
                onFailure = { SurahLoadState.Error(it.localizedMessage ?: "Unable to fetch Mushaf page $pageNum") }
            )
            // Silently prefetch adjacent pages for smooth page turning & two-page spreads
            if (pageNum < QuranMetadata.TOTAL_MUSHAF_PAGES) {
                repo.loadPageIfNeeded(pageNum + 1, edition)
            }
            if (pageNum > 1) {
                repo.loadPageIfNeeded(pageNum - 1, edition)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun executeSearch(edition: String) {
        val q = _searchQuery.value.trim()
        if (q.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isSearching.value = true
            _searchResults.value = repo.searchVerses(q, edition)
            _isSearching.value = false
        }
    }
}
