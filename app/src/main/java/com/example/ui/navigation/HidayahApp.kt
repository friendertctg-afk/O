package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.audio.AudioPlayerManager
import com.example.data.seed.QariCatalog
import com.example.firebase.authStateFlow
import com.example.localization.stringsFor
import com.example.ui.components.PersistentMiniAudioPlayer
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.ArabicAcademyScreen
import com.example.ui.screens.AsmaUlHusnaScreen
import com.example.ui.screens.DailyLifeTrackerScreen
import com.example.ui.screens.DuasScreen
import com.example.ui.screens.ExploreHubScreen
import com.example.ui.screens.HadithAndLibraryScreen
import com.example.ui.screens.HifzScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KidsAcademyScreen
import com.example.ui.screens.LearnHubScreen
import com.example.ui.screens.PrayerTimesScreen
import com.example.ui.screens.ProfileAndSettingsScreen
import com.example.ui.screens.QiblaCompassScreen
import com.example.ui.screens.QuranHubScreen
import com.example.ui.screens.QuranReaderScreen
import com.example.ui.screens.TajweedScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.screens.WallpapersScreen
import com.example.ui.screens.WelcomeSplashScreen
import com.example.ui.screens.ZakatCalculatorScreen
import com.example.ui.theme.AlQuranTheme
import com.example.ui.viewmodel.AiAssistantViewModel
import com.example.ui.viewmodel.MainAppViewModel
import com.example.ui.viewmodel.PrayerViewModel
import com.example.ui.viewmodel.QuranViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

object HidayahRoutes {
    const val HOME = "home"
    const val QURAN = "quran"
    const val LEARN = "learn"
    const val EXPLORE = "explore"
    const val PROFILE = "profile"
    const val QURAN_READER = "quran_reader/{surahNumber}?ayah={ayah}&page={page}"
    const val HIFZ = "hifz"
    const val TAJWEED = "tajweed"
    const val ARABIC = "arabic"
    const val KIDS = "kids"
    const val PRAYERS = "prayers"
    const val PRAYERS_WITH_TAB = "prayers?tab={tab}"
    const val QIBLA = "qibla"
    const val DUAS = "duas"
    const val TASBIH = "tasbih"
    const val LIFE_TRACKER = "life_tracker"
    const val ASMA_99 = "asma_99"
    const val ZAKAT = "zakat"
    const val HADITH_LIBRARY = "hadith_library"
    const val HADITH_LIBRARY_WITH_TAB = "hadith_library?tab={tab}"
    const val WALLPAPERS = "wallpapers"
    const val AI_ASSISTANT = "ai_assistant"

    fun quranReader(surahNumber: Int, ayah: Int = 1, page: Int = -1): String =
        "quran_reader/$surahNumber?ayah=$ayah&page=$page"
}

private data class TopTabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun HidayahApp() {
    val context = LocalContext.current
    val mainVm: MainAppViewModel = viewModel()
    val quranVm: QuranViewModel = viewModel()
    val prayerVm: PrayerViewModel = viewModel()
    val aiVm: AiAssistantViewModel = viewModel()

    val prefs by mainVm.prefs.collectAsStateWithLifecycle()
    val bookmarks by mainVm.bookmarks.collectAsStateWithLifecycle()
    val pageBookmarks by mainVm.pageBookmarks.collectAsStateWithLifecycle()
    val cachedPageCount by mainVm.cachedPageCount.collectAsStateWithLifecycle()
    val cachedAyahCount by mainVm.cachedAyahCount.collectAsStateWithLifecycle()
    val offlineBatchStatus by mainVm.offlineBatchStatus.collectAsStateWithLifecycle()
    val hifzList by mainVm.hifzProgress.collectAsStateWithLifecycle()
    val tasbihSessions by mainVm.tasbihSessions.collectAsStateWithLifecycle()
    val favDuas by mainVm.favoriteDuaIds.collectAsStateWithLifecycle()
    val favNames99 by mainVm.favoriteName99Ids.collectAsStateWithLifecycle()
    val favQaris by mainVm.favoriteQariIds.collectAsStateWithLifecycle()
    val favWallpapers by mainVm.favoriteWallpaperIds.collectAsStateWithLifecycle()
    val lessonProgress by mainVm.lessonProgress.collectAsStateWithLifecycle()
    val downloadedAudio by mainVm.downloadedAudio.collectAsStateWithLifecycle()

    val prayerState by prayerVm.uiState.collectAsStateWithLifecycle()
    val audioState by AudioPlayerManager.state.collectAsStateWithLifecycle()
    val aiState by aiVm.uiState.collectAsStateWithLifecycle()
    val currentUser by remember { Firebase.auth.authStateFlow() }
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    var pendingAiPrompt by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(prefs.audioVolume) {
        AudioPlayerManager.setVolume(prefs.audioVolume)
    }

    LaunchedEffect(
        prefs.selectedCityName,
        prefs.latitude,
        prefs.longitude,
        prefs.prayerMethodIndex,
        prefs.asrHanafi
    ) {
        prayerVm.refreshPrayerTimes(
            cityName = prefs.selectedCityName,
            latitude = prefs.latitude,
            longitude = prefs.longitude,
            methodIndex = prefs.prayerMethodIndex,
            asrHanafi = prefs.asrHanafi,
        )
    }

    val layoutDir = if (prefs.appLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val strings = stringsFor(prefs.appLanguage)

    AlQuranTheme(preset = prefs.themePreset) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
            if (!prefs.hasCompletedWelcome) {
                WelcomeSplashScreen(
                    prefs = prefs,
                    onSelectLanguage = mainVm::setAppLanguage,
                    onSelectTheme = mainVm::setThemePreset,
                    onContinue = mainVm::completeWelcome
                )
            } else {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                val topTabs = listOf(
                    TopTabItem(HidayahRoutes.HOME, strings.navHome, Icons.Filled.Home),
                    TopTabItem(HidayahRoutes.QURAN, strings.navQuran, Icons.AutoMirrored.Filled.MenuBook),
                    TopTabItem(HidayahRoutes.LEARN, strings.navLearn, Icons.Filled.School),
                    TopTabItem(HidayahRoutes.EXPLORE, strings.navExplore, Icons.Filled.Explore),
                    TopTabItem(HidayahRoutes.PROFILE, strings.navProfile, Icons.Filled.Person),
                )
                val isTopTab = currentRoute in topTabs.map { it.route }
                val isReaderRoute = currentRoute?.startsWith("quran_reader") == true

                fun navigateTopTab(route: String) {
                    navController.navigate(route) {
                        popUpTo(HidayahRoutes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val useRail = maxWidth >= 840.dp

                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        floatingActionButton = {
                            if (isTopTab) {
                                FloatingActionButton(
                                    onClick = { navController.navigate(HidayahRoutes.AI_ASSISTANT) },
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.testTag("fab_ai_assistant")
                                ) {
                                    Icon(Icons.Filled.Psychology, contentDescription = strings.aiAssistant)
                                }
                            }
                        },
                        bottomBar = {
                            Column {
                                if (!isReaderRoute) {
                                    PersistentMiniAudioPlayer(
                                        audioState = audioState,
                                        onTogglePlayPause = { AudioPlayerManager.togglePlayPause(context) },
                                        onNextAyah = { AudioPlayerManager.skipNextAyah(context) },
                                        onStop = { AudioPlayerManager.stop() },
                                        onOpenReader = { surah, ayah ->
                                            navController.navigate(HidayahRoutes.quranReader(surah, ayah))
                                        }
                                    )
                                }
                                if (isTopTab && !useRail) {
                                    NavigationBar {
                                        topTabs.forEach { tab ->
                                            NavigationBarItem(
                                                selected = currentRoute == tab.route,
                                                onClick = { navigateTopTab(tab.route) },
                                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                                label = { Text(tab.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                                modifier = Modifier.testTag("nav_tab_${tab.route}")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (isTopTab && useRail) {
                                NavigationRail {
                                    topTabs.forEach { tab ->
                                        NavigationRailItem(
                                            selected = currentRoute == tab.route,
                                            onClick = { navigateTopTab(tab.route) },
                                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                                            label = { Text(tab.label, maxLines = 1) }
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                                NavHost(
                                    navController = navController,
                                    startDestination = HidayahRoutes.HOME
                                ) {
                                    composable(HidayahRoutes.HOME) {
                                        HomeScreen(
                                            prefs = prefs,
                                            prayerState = prayerState,
                                            audioState = audioState,
                                            hifzList = hifzList,
                                            onNavigate = { route ->
                                                if (route in topTabs.map { it.route }) {
                                                    navigateTopTab(route)
                                                } else {
                                                    navController.navigate(route)
                                                }
                                            },
                                            onOpenSurah = { surah, ayah ->
                                                navController.navigate(HidayahRoutes.quranReader(surah, ayah))
                                            },
                                            onPlayContinueAudio = {
                                                AudioPlayerManager.playAyah(
                                                    context,
                                                    prefs.lastReadSurah,
                                                    prefs.lastReadAyah,
                                                    QariCatalog.findById(prefs.selectedQariId)
                                                )
                                            },
                                            onSaveDashboardConfig = mainVm::setDashboardWidgets
                                        )
                                    }

                                    composable(HidayahRoutes.QURAN) {
                                        val searchQuery by quranVm.searchQuery.collectAsStateWithLifecycle()
                                        val searchResults by quranVm.searchResults.collectAsStateWithLifecycle()
                                        val isSearching by quranVm.isSearching.collectAsStateWithLifecycle()
                                        val availMb = remember(downloadedAudio.size, cachedAyahCount) {
                                            mainVm.availableStorageMb()
                                        }

                                        QuranHubScreen(
                                            prefs = prefs,
                                            bookmarks = bookmarks,
                                            pageBookmarks = pageBookmarks,
                                            cachedPageCount = cachedPageCount,
                                            cachedAyahCount = cachedAyahCount,
                                            downloadedAudio = downloadedAudio,
                                            availableStorageMb = availMb,
                                            offlineBatchStatus = offlineBatchStatus,
                                            audioState = audioState,
                                            searchQuery = searchQuery,
                                            searchResults = searchResults,
                                            isSearching = isSearching,
                                            onSearchQueryChange = quranVm::updateSearchQuery,
                                            onExecuteSearch = { quranVm.executeSearch(prefs.translationEdition) },
                                            onOpenSurah = { surah, ayah, pageNum ->
                                                navController.navigate(
                                                    HidayahRoutes.quranReader(surah, ayah, pageNum ?: -1)
                                                )
                                            },
                                            onSelectMushafEdition = mainVm::setMushafEdition,
                                            onSetLayout = mainVm::setQuranLayout,
                                            onSetShowTranslation = mainVm::setShowTranslation,
                                            onToggleFavoriteSurah = mainVm::toggleFavoriteSurah,
                                            onToggleCompletedJuz = mainVm::toggleCompletedJuz,
                                            onIncrementKhatamPage = { mainVm.incrementKhatamPages(1) },
                                            onDeleteBookmark = mainVm::removeBookmark,
                                            onDeletePageBookmark = mainVm::removePageBookmark,
                                            onPrefetchSurahsOffline = { surahs ->
                                                mainVm.prefetchSurahRangeOffline(surahs, prefs.translationEdition)
                                            },
                                            onDownloadSurahAudio = { sNum, qari ->
                                                mainVm.downloadSurahAudio(sNum, qari) {}
                                            },
                                            onDeleteSurahAudio = mainVm::deleteDownloadedSurahAudio,
                                            onBackToHome = { navigateTopTab(HidayahRoutes.HOME) }
                                        )
                                    }

                                    composable(
                                        route = HidayahRoutes.QURAN_READER,
                                        arguments = listOf(
                                            navArgument("surahNumber") { type = NavType.IntType },
                                            navArgument("ayah") { type = NavType.IntType; defaultValue = 1 },
                                            navArgument("page") { type = NavType.IntType; defaultValue = -1 },
                                        )
                                    ) { entry ->
                                        val surahNum = entry.arguments?.getInt("surahNumber") ?: 1
                                        val ayahNum = entry.arguments?.getInt("ayah") ?: 1
                                        val explicitPage = entry.arguments?.getInt("page")?.takeIf {
                                            it in 1..com.example.data.seed.QuranMetadata.TOTAL_HAFIZI_PAGES
                                        }

                                        LaunchedEffect(surahNum, ayahNum, explicitPage, prefs.translationEdition, prefs.mushafEdition) {
                                            quranVm.openSurahAndResolvePage(
                                                surahNum = surahNum,
                                                ayahNum = ayahNum,
                                                explicitPage = explicitPage,
                                                edition = prefs.translationEdition,
                                                mushafEdition = prefs.mushafEdition
                                            )
                                        }
                                        val ayahs by quranVm.ayahsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
                                        val currentPage by quranVm.currentPage.collectAsStateWithLifecycle()
                                        val loadState by quranVm.loadState.collectAsStateWithLifecycle()

                                        QuranReaderScreen(
                                            surahNumber = surahNum,
                                            initialAyah = ayahNum,
                                            currentPage = currentPage,
                                            prefs = prefs,
                                            ayahs = ayahs,
                                            loadState = loadState,
                                            bookmarks = bookmarks,
                                            pageBookmarks = pageBookmarks,
                                            favoriteQariIds = favQaris,
                                            downloadedAudio = downloadedAudio,
                                            audioState = audioState,
                                            observePage = { p -> quranVm.observePage(p, prefs.translationEdition, prefs.mushafEdition) },
                                            onEnsurePageLoaded = { p -> quranVm.ensurePageLoaded(p, prefs.translationEdition, prefs.mushafEdition) },
                                            onJumpToPage = { p -> quranVm.selectPage(p, prefs.translationEdition, prefs.mushafEdition) },
                                            onBack = { navController.popBackStack() },
                                            onSelectSurah = { next ->
                                                navController.navigate(HidayahRoutes.quranReader(next, 1)) {
                                                    popUpTo(HidayahRoutes.QURAN)
                                                }
                                            },
                                            onUpdateLastRead = mainVm::setLastRead,
                                            onUpdateLastReadPage = mainVm::setLastReadPage,
                                            onSetMushafEdition = mainVm::setMushafEdition,
                                            onSetScript = mainVm::setQuranScript,
                                            onSetLayout = mainVm::setQuranLayout,
                                            onSetTwoPageSpread = mainVm::setTwoPageSpreadPref,
                                            onSetArabicFont = mainVm::setArabicFont,
                                            onSetArabicFontSize = mainVm::setArabicFontSize,
                                            onSetPageMarginDp = mainVm::setReaderPageMarginDp,
                                            onSetReadingMode = mainVm::setReadingMode,
                                            onSetBrightness = mainVm::setReaderBrightness,
                                            onSetKeepScreenAwake = mainVm::setKeepScreenAwake,
                                            onSetLockPortrait = mainVm::setLockPortraitOrientation,
                                            onSetAudioVolume = mainVm::setAudioVolume,
                                            onSetTranslationEdition = mainVm::setTranslationEdition,
                                            onSetShowTranslation = mainVm::setShowTranslation,
                                            onSetShowWordByWord = mainVm::setShowWordByWord,
                                            onSetShowTransliteration = mainVm::setShowTransliteration,
                                            onUpsertBookmark = mainVm::upsertBookmark,
                                            onRemoveBookmark = mainVm::removeBookmark,
                                            onTogglePageBookmark = { p, title ->
                                                val exists = pageBookmarks.any { it.pageNumber == p }
                                                if (exists) mainVm.removePageBookmark(p) else mainVm.upsertPageBookmark(p, title)
                                            },
                                            onSelectQari = mainVm::setSelectedQari,
                                            onToggleFavoriteQari = { id -> mainVm.toggleFavorite("qari", id) },
                                            onDownloadSurah = mainVm::downloadSurahAudio,
                                            onRetryLoad = quranVm::retryLoad,
                                            onAskAiAboutAyah = { prompt ->
                                                pendingAiPrompt = prompt
                                                navController.navigate(HidayahRoutes.AI_ASSISTANT)
                                            }
                                        )
                                    }

                                    composable(HidayahRoutes.LEARN) {
                                        LearnHubScreen(
                                            prefs = prefs,
                                            hifzList = hifzList,
                                            lessons = lessonProgress,
                                            onNavigate = { route -> navController.navigate(route) }
                                        )
                                    }

                                    composable(HidayahRoutes.HIFZ) {
                                        HifzScreen(
                                            prefs = prefs,
                                            hifzList = hifzList,
                                            onSetDailyTarget = mainVm::setDailyHifzTarget,
                                            onUpdateHifz = mainVm::updateHifzProgress,
                                            onOpenReader = { surah, ayah ->
                                                navController.navigate(HidayahRoutes.quranReader(surah, ayah))
                                            },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.TAJWEED) {
                                        TajweedScreen(
                                            prefs = prefs,
                                            lessonProgress = lessonProgress,
                                            onCompleteLesson = { key, score ->
                                                mainVm.saveLessonProgress("tajweed", key, true, score)
                                            },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.ARABIC) {
                                        ArabicAcademyScreen(
                                            prefs = prefs,
                                            lessonProgress = lessonProgress,
                                            onCompleteLesson = { key, score ->
                                                mainVm.saveLessonProgress("arabic", key, true, score)
                                            },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.KIDS) {
                                        KidsAcademyScreen(
                                            prefs = prefs,
                                            onOpenShortSurah = { surah ->
                                                navController.navigate(HidayahRoutes.quranReader(surah, 1))
                                            },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.EXPLORE) {
                                        ExploreHubScreen(
                                            prefs = prefs,
                                            audioState = audioState,
                                            favoriteQariIds = favQaris,
                                            onSelectQari = mainVm::setSelectedQari,
                                            onToggleFavoriteQari = { id -> mainVm.toggleFavorite("qari", id) },
                                            onNavigate = { route ->
                                                if (route in topTabs.map { it.route }) {
                                                    navigateTopTab(route)
                                                } else {
                                                    navController.navigate(route)
                                                }
                                            }
                                        )
                                    }

                                    composable(
                                        route = HidayahRoutes.PRAYERS_WITH_TAB,
                                        arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
                                    ) { entry ->
                                        val tab = entry.arguments?.getInt("tab") ?: 0
                                        PrayerTimesScreen(
                                            prefs = prefs,
                                            prayerState = prayerState,
                                            initialTab = tab,
                                            onSelectCity = mainVm::setLocation,
                                            onRequestGps = {
                                                prayerVm.detectCurrentLocation(mainVm::setLocation)
                                            },
                                            onChangeMethod = mainVm::setPrayerCalculation,
                                            onChangeNotifications = mainVm::setNotifications,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.QIBLA) {
                                        QiblaCompassScreen(
                                            prefs = prefs,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.DUAS) {
                                        DuasScreen(
                                            prefs = prefs,
                                            favoriteIds = favDuas,
                                            onToggleFavorite = { id -> mainVm.toggleFavorite("dua", id) },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.TASBIH) {
                                        TasbihScreen(
                                            prefs = prefs,
                                            sessions = tasbihSessions,
                                            onIncrementTotal = mainVm::incrementTasbihCount,
                                            onSaveSession = mainVm::saveTasbihSession,
                                            onDeleteSession = mainVm::deleteTasbihSession,
                                            onSetTasbihTheme = mainVm::setTasbihThemeIndex,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.LIFE_TRACKER) {
                                        DailyLifeTrackerScreen(
                                            prefs = prefs,
                                            onTogglePrayer = mainVm::toggleDailyPrayer,
                                            onToggleHabit = mainVm::toggleSunnahHabit,
                                            onAdjustQada = mainVm::adjustQadaPrayers,
                                            onIncrementFasting = mainVm::incrementFastingDays,
                                            onIncrementKhatam = mainVm::incrementKhatamPages,
                                            onIncrementCharity = mainVm::incrementCharityCount,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.ASMA_99) {
                                        AsmaUlHusnaScreen(
                                            prefs = prefs,
                                            favoriteIds = favNames99,
                                            onToggleFavorite = { id -> mainVm.toggleFavorite("name99", id) },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.ZAKAT) {
                                        ZakatCalculatorScreen(onBack = { navController.popBackStack() })
                                    }

                                    composable(
                                        route = HidayahRoutes.HADITH_LIBRARY_WITH_TAB,
                                        arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
                                    ) { entry ->
                                        val tab = entry.arguments?.getInt("tab") ?: 0
                                        HadithAndLibraryScreen(
                                            prefs = prefs,
                                            initialTab = tab,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.WALLPAPERS) {
                                        WallpapersScreen(
                                            prefs = prefs,
                                            favoriteIds = favWallpapers,
                                            onToggleFavorite = { id -> mainVm.toggleFavorite("wallpaper", id) },
                                            onSetDailyWallpaper = mainVm::setDailyWallpaperId,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.AI_ASSISTANT) {
                                        AiAssistantScreen(
                                            prefs = prefs,
                                            uiState = aiState,
                                            initialPrompt = pendingAiPrompt,
                                            onSelectMode = aiVm::selectMode,
                                            onSendQuestion = { q ->
                                                aiVm.sendQuestion(q, prefs.appLanguage.englishName)
                                            },
                                            onClearChat = aiVm::clearConversation,
                                            onConsumeInitialPrompt = { pendingAiPrompt = null },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }

                                    composable(HidayahRoutes.PROFILE) {
                                        ProfileAndSettingsScreen(
                                            prefs = prefs,
                                            currentUser = currentUser,
                                            bookmarks = bookmarks,
                                            hifzList = hifzList,
                                            lessons = lessonProgress,
                                            onSelectTheme = mainVm::setThemePreset,
                                            onSelectLanguage = mainVm::setAppLanguage,
                                            onFeedbackSubmitted = mainVm::recordFeedbackSubmitted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
