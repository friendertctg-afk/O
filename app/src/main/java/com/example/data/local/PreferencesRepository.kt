package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.localization.AppLanguage
import com.example.ui.theme.ArabicFontChoice
import com.example.ui.theme.IslamicThemePreset
import com.example.ui.theme.QuranReadingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "alquran_prefs")

enum class QuranScriptType(val key: String, val label: String, val description: String) {
    UTHMANI(
        "UTHMANI",
        "Uthmani (Madani Mushaf)",
        "Standard King Fahd Complex Uthmani orthography with classic waqf marks."
    ),
    INDO_PAK(
        "INDO_PAK",
        "Indo-Pak / Simple",
        "Clear high-legibility script favored in South Asia with streamlined vowel styling."
    ),
    TAJWEED(
        "TAJWEED",
        "Tajweed Color-Coded",
        "Uthmani text annotated with color rules for Ghunnah, Idgham, Ikhfa, Iqlab, Qalqalah, and Madd."
    );

    companion object {
        fun fromKey(key: String?): QuranScriptType =
            entries.firstOrNull { it.key == key } ?: UTHMANI
    }
}

enum class QuranLayoutStyle(val key: String, val label: String) {
    AYAH_BY_AYAH("AYAH_BY_AYAH", "Ayah by Ayah"),
    MADANI_MUSHAF("MADANI_MUSHAF", "Madani Mushaf"),
    HAFIZI_PAGE("HAFIZI_PAGE", "Hafizi 15-Line"),
    SIMPLE_READING("SIMPLE_READING", "Arabic Only");

    companion object {
        fun fromKey(key: String?): QuranLayoutStyle =
            entries.firstOrNull { it.key == key } ?: AYAH_BY_AYAH
    }
}

data class UserPreferencesState(
    val themePreset: IslamicThemePreset = IslamicThemePreset.EMERALD_GOLD,
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val arabicFont: ArabicFontChoice = ArabicFontChoice.SCHEHERAZADE,
    val arabicFontSizeSp: Float = 28f,
    val readingMode: QuranReadingMode = QuranReadingMode.LIGHT,
    val quranScript: QuranScriptType = QuranScriptType.UTHMANI,
    val quranLayout: QuranLayoutStyle = QuranLayoutStyle.AYAH_BY_AYAH,
    val translationEdition: String = "en.sahih",
    val showTranslation: Boolean = true,
    val showWordByWord: Boolean = false,
    val showTransliteration: Boolean = true,
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val selectedQariId: String = "ar.alafasy",
    val selectedCityName: String = "Dhaka",
    val latitude: Double = 23.8103,
    val longitude: Double = 90.4125,
    val prayerMethodIndex: Int = 1, // 1 = Karachi, 2 = ISNA, 3 = MWL, 4 = Makkah, 5 = Egyptian
    val asrHanafi: Boolean = false,
    val prayerNotificationsEnabled: Boolean = true,
    val adhanNotificationEnabled: Boolean = false,
    val jummahReminderEnabled: Boolean = true,
    val totalTasbihCount: Long = 0L,
    val tasbihThemeIndex: Int = 0,
    val dailyHifzTarget: Int = 5,
    val dailyWallpaperId: String = "kaaba_twilight",
    val hasCompletedWelcome: Boolean = false,
    val showDashboardPrayerCard: Boolean = true,
    val showDashboardContinueReading: Boolean = true,
    val showDashboardDailyInspirations: Boolean = true,
    val showDashboardQuickActions: Boolean = true,
    val showDashboardWorshipProgress: Boolean = true,
    val completedDailyPrayers: Set<String> = emptySet(),
    val completedSunnahHabits: Set<String> = emptySet(),
    val qadaPrayersRemaining: Int = 0,
    val fastingDaysCompleted: Int = 0,
    val khatamPagesRead: Int = 0,
    val charityTotalCount: Int = 0,
    val submittedFeedbackCount: Int = 0,
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val ARABIC_FONT = stringPreferencesKey("arabic_font")
        val ARABIC_FONT_SIZE = floatPreferencesKey("arabic_font_size")
        val READING_MODE = stringPreferencesKey("reading_mode")
        val QURAN_SCRIPT = stringPreferencesKey("quran_script")
        val QURAN_LAYOUT = stringPreferencesKey("quran_layout")
        val TRANSLATION_EDITION = stringPreferencesKey("translation_edition")
        val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
        val SHOW_WORD_BY_WORD = booleanPreferencesKey("show_word_by_word")
        val SHOW_TRANSLITERATION = booleanPreferencesKey("show_transliteration")
        val LAST_READ_SURAH = intPreferencesKey("last_read_surah")
        val LAST_READ_AYAH = intPreferencesKey("last_read_ayah")
        val SELECTED_QARI = stringPreferencesKey("selected_qari")
        val CITY_NAME = stringPreferencesKey("city_name")
        val LATITUDE = floatPreferencesKey("latitude")
        val LONGITUDE = floatPreferencesKey("longitude")
        val PRAYER_METHOD = intPreferencesKey("prayer_method")
        val ASR_HANAFI = booleanPreferencesKey("asr_hanafi")
        val PRAYER_NOTIF = booleanPreferencesKey("prayer_notif")
        val ADHAN_NOTIF = booleanPreferencesKey("adhan_notif")
        val JUMMAH_REMINDER = booleanPreferencesKey("jummah_reminder")
        val TOTAL_TASBIH = longPreferencesKey("total_tasbih")
        val TASBIH_THEME = intPreferencesKey("tasbih_theme")
        val DAILY_HIFZ_TARGET = intPreferencesKey("daily_hifz_target")
        val DAILY_WALLPAPER = stringPreferencesKey("daily_wallpaper")
        val HAS_COMPLETED_WELCOME = booleanPreferencesKey("has_completed_welcome")
        val SHOW_DASH_PRAYER = booleanPreferencesKey("show_dash_prayer")
        val SHOW_DASH_CONTINUE = booleanPreferencesKey("show_dash_continue")
        val SHOW_DASH_DAILY = booleanPreferencesKey("show_dash_daily")
        val SHOW_DASH_QUICK = booleanPreferencesKey("show_dash_quick")
        val SHOW_DASH_PROGRESS = booleanPreferencesKey("show_dash_progress")
        val DAILY_PRAYER_DATE = stringPreferencesKey("daily_prayer_date")
        val COMPLETED_PRAYERS = stringSetPreferencesKey("completed_prayers")
        val COMPLETED_HABITS = stringSetPreferencesKey("completed_habits")
        val QADA_REMAINING = intPreferencesKey("qada_remaining")
        val FASTING_DAYS = intPreferencesKey("fasting_days")
        val KHATAM_PAGES = intPreferencesKey("khatam_pages")
        val CHARITY_COUNT = intPreferencesKey("charity_count")
        val FEEDBACK_COUNT = intPreferencesKey("feedback_count")
    }

    val preferencesFlow: Flow<UserPreferencesState> = context.dataStore.data.map { prefs ->
        val lang = AppLanguage.fromCode(prefs[Keys.APP_LANGUAGE])
        UserPreferencesState(
            themePreset = IslamicThemePreset.fromKey(prefs[Keys.THEME_PRESET]),
            appLanguage = lang,
            arabicFont = ArabicFontChoice.fromKey(prefs[Keys.ARABIC_FONT]),
            arabicFontSizeSp = prefs[Keys.ARABIC_FONT_SIZE] ?: 28f,
            readingMode = QuranReadingMode.fromKey(prefs[Keys.READING_MODE]),
            quranScript = QuranScriptType.fromKey(prefs[Keys.QURAN_SCRIPT]),
            quranLayout = QuranLayoutStyle.fromKey(prefs[Keys.QURAN_LAYOUT]),
            translationEdition = prefs[Keys.TRANSLATION_EDITION] ?: lang.defaultAlQuranEdition,
            showTranslation = prefs[Keys.SHOW_TRANSLATION] ?: true,
            showWordByWord = prefs[Keys.SHOW_WORD_BY_WORD] ?: false,
            showTransliteration = prefs[Keys.SHOW_TRANSLITERATION] ?: true,
            lastReadSurah = prefs[Keys.LAST_READ_SURAH] ?: 1,
            lastReadAyah = prefs[Keys.LAST_READ_AYAH] ?: 1,
            selectedQariId = prefs[Keys.SELECTED_QARI] ?: "ar.alafasy",
            selectedCityName = prefs[Keys.CITY_NAME] ?: "Dhaka",
            latitude = (prefs[Keys.LATITUDE] ?: 23.8103f).toDouble(),
            longitude = (prefs[Keys.LONGITUDE] ?: 90.4125f).toDouble(),
            prayerMethodIndex = prefs[Keys.PRAYER_METHOD] ?: 1,
            asrHanafi = prefs[Keys.ASR_HANAFI] ?: false,
            prayerNotificationsEnabled = prefs[Keys.PRAYER_NOTIF] ?: true,
            adhanNotificationEnabled = prefs[Keys.ADHAN_NOTIF] ?: false,
            jummahReminderEnabled = prefs[Keys.JUMMAH_REMINDER] ?: true,
            totalTasbihCount = prefs[Keys.TOTAL_TASBIH] ?: 0L,
            tasbihThemeIndex = prefs[Keys.TASBIH_THEME] ?: 0,
            dailyHifzTarget = prefs[Keys.DAILY_HIFZ_TARGET] ?: 5,
            dailyWallpaperId = prefs[Keys.DAILY_WALLPAPER] ?: "kaaba_twilight",
            hasCompletedWelcome = prefs[Keys.HAS_COMPLETED_WELCOME] ?: false,
            showDashboardPrayerCard = prefs[Keys.SHOW_DASH_PRAYER] ?: true,
            showDashboardContinueReading = prefs[Keys.SHOW_DASH_CONTINUE] ?: true,
            showDashboardDailyInspirations = prefs[Keys.SHOW_DASH_DAILY] ?: true,
            showDashboardQuickActions = prefs[Keys.SHOW_DASH_QUICK] ?: true,
            showDashboardWorshipProgress = prefs[Keys.SHOW_DASH_PROGRESS] ?: true,
            completedDailyPrayers = if (prefs[Keys.DAILY_PRAYER_DATE] == todayDateKey()) {
                prefs[Keys.COMPLETED_PRAYERS] ?: emptySet()
            } else {
                emptySet()
            },
            completedSunnahHabits = if (prefs[Keys.DAILY_PRAYER_DATE] == todayDateKey()) {
                prefs[Keys.COMPLETED_HABITS] ?: emptySet()
            } else {
                emptySet()
            },
            qadaPrayersRemaining = prefs[Keys.QADA_REMAINING] ?: 0,
            fastingDaysCompleted = prefs[Keys.FASTING_DAYS] ?: 0,
            khatamPagesRead = prefs[Keys.KHATAM_PAGES] ?: 0,
            charityTotalCount = prefs[Keys.CHARITY_COUNT] ?: 0,
            submittedFeedbackCount = prefs[Keys.FEEDBACK_COUNT] ?: 0,
        )
    }

    private fun todayDateKey(): String {
        val cal = java.util.Calendar.getInstance()
        return "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
    }

    suspend fun setThemePreset(preset: IslamicThemePreset) {
        context.dataStore.edit { it[Keys.THEME_PRESET] = preset.key }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        context.dataStore.edit {
            it[Keys.APP_LANGUAGE] = language.code
            it[Keys.TRANSLATION_EDITION] = language.defaultAlQuranEdition
        }
    }

    suspend fun setArabicFont(font: ArabicFontChoice) {
        context.dataStore.edit { it[Keys.ARABIC_FONT] = font.key }
    }

    suspend fun setArabicFontSize(sizeSp: Float) {
        context.dataStore.edit { it[Keys.ARABIC_FONT_SIZE] = sizeSp.coerceIn(18f, 46f) }
    }

    suspend fun setReadingMode(mode: QuranReadingMode) {
        context.dataStore.edit { it[Keys.READING_MODE] = mode.key }
    }

    suspend fun setQuranScript(script: QuranScriptType) {
        context.dataStore.edit { it[Keys.QURAN_SCRIPT] = script.key }
    }

    suspend fun setQuranLayout(layout: QuranLayoutStyle) {
        context.dataStore.edit { it[Keys.QURAN_LAYOUT] = layout.key }
    }

    suspend fun setTranslationEdition(edition: String) {
        context.dataStore.edit { it[Keys.TRANSLATION_EDITION] = edition }
    }

    suspend fun setShowTranslation(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_TRANSLATION] = show }
    }

    suspend fun setShowWordByWord(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_WORD_BY_WORD] = show }
    }

    suspend fun setShowTransliteration(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_TRANSLITERATION] = show }
    }

    suspend fun setLastRead(surah: Int, ayah: Int) {
        context.dataStore.edit {
            it[Keys.LAST_READ_SURAH] = surah.coerceIn(1, 114)
            it[Keys.LAST_READ_AYAH] = ayah.coerceAtLeast(1)
        }
    }

    suspend fun setSelectedQari(qariId: String) {
        context.dataStore.edit { it[Keys.SELECTED_QARI] = qariId }
    }

    suspend fun setLocation(cityName: String, lat: Double, lon: Double) {
        context.dataStore.edit {
            it[Keys.CITY_NAME] = cityName
            it[Keys.LATITUDE] = lat.toFloat()
            it[Keys.LONGITUDE] = lon.toFloat()
        }
    }

    suspend fun setPrayerCalculation(methodIndex: Int, asrHanafi: Boolean) {
        context.dataStore.edit {
            it[Keys.PRAYER_METHOD] = methodIndex
            it[Keys.ASR_HANAFI] = asrHanafi
        }
    }

    suspend fun setNotifications(prayerEnabled: Boolean, adhanEnabled: Boolean, jummahEnabled: Boolean) {
        context.dataStore.edit {
            it[Keys.PRAYER_NOTIF] = prayerEnabled
            it[Keys.ADHAN_NOTIF] = adhanEnabled
            it[Keys.JUMMAH_REMINDER] = jummahEnabled
        }
    }

    suspend fun incrementTotalTasbih(delta: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_TASBIH] ?: 0L
            prefs[Keys.TOTAL_TASBIH] = (current + delta).coerceAtLeast(0L)
        }
    }

    suspend fun setTotalTasbih(total: Long) {
        context.dataStore.edit { it[Keys.TOTAL_TASBIH] = total.coerceAtLeast(0L) }
    }

    suspend fun setTasbihThemeIndex(idx: Int) {
        context.dataStore.edit { it[Keys.TASBIH_THEME] = idx }
    }

    suspend fun setDailyHifzTarget(target: Int) {
        context.dataStore.edit { it[Keys.DAILY_HIFZ_TARGET] = target.coerceIn(1, 50) }
    }

    suspend fun setDailyWallpaperId(id: String) {
        context.dataStore.edit { it[Keys.DAILY_WALLPAPER] = id }
    }

    suspend fun setHasCompletedWelcome(done: Boolean) {
        context.dataStore.edit { it[Keys.HAS_COMPLETED_WELCOME] = done }
    }

    suspend fun setDashboardWidgets(
        showPrayer: Boolean,
        showContinue: Boolean,
        showDaily: Boolean,
        showQuick: Boolean,
        showProgress: Boolean,
    ) {
        context.dataStore.edit {
            it[Keys.SHOW_DASH_PRAYER] = showPrayer
            it[Keys.SHOW_DASH_CONTINUE] = showContinue
            it[Keys.SHOW_DASH_DAILY] = showDaily
            it[Keys.SHOW_DASH_QUICK] = showQuick
            it[Keys.SHOW_DASH_PROGRESS] = showProgress
        }
    }

    suspend fun toggleDailyPrayer(prayerKey: String) {
        val today = todayDateKey()
        context.dataStore.edit { prefs ->
            val base = if (prefs[Keys.DAILY_PRAYER_DATE] == today) {
                prefs[Keys.COMPLETED_PRAYERS] ?: emptySet()
            } else {
                emptySet()
            }
            prefs[Keys.DAILY_PRAYER_DATE] = today
            prefs[Keys.COMPLETED_PRAYERS] = if (prayerKey in base) base - prayerKey else base + prayerKey
        }
    }

    suspend fun toggleSunnahHabit(habitId: String) {
        val today = todayDateKey()
        context.dataStore.edit { prefs ->
            val base = if (prefs[Keys.DAILY_PRAYER_DATE] == today) {
                prefs[Keys.COMPLETED_HABITS] ?: emptySet()
            } else {
                emptySet()
            }
            prefs[Keys.DAILY_PRAYER_DATE] = today
            prefs[Keys.COMPLETED_HABITS] = if (habitId in base) base - habitId else base + habitId
        }
    }

    suspend fun adjustQadaPrayers(delta: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.QADA_REMAINING] ?: 0
            prefs[Keys.QADA_REMAINING] = (current + delta).coerceAtLeast(0)
        }
    }

    suspend fun incrementFastingDays(delta: Int = 1) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.FASTING_DAYS] ?: 0
            prefs[Keys.FASTING_DAYS] = (current + delta).coerceIn(0, 3650)
        }
    }

    suspend fun incrementKhatamPages(delta: Int = 1) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.KHATAM_PAGES] ?: 0
            prefs[Keys.KHATAM_PAGES] = (current + delta).coerceIn(0, 604)
        }
    }

    suspend fun incrementCharityCount() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.CHARITY_COUNT] ?: 0
            prefs[Keys.CHARITY_COUNT] = current + 1
        }
    }

    suspend fun recordFeedbackSubmitted() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.FEEDBACK_COUNT] ?: 0
            prefs[Keys.FEEDBACK_COUNT] = current + 1
        }
    }
}
