package com.example.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MonthlyPrayerDay
import com.example.data.repository.PrayerTimesRepository
import com.example.utils.DailyPrayerSchedule
import com.example.utils.PrayerAndQiblaUtils
import com.example.utils.PrayerMethod
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

data class PrayerUiState(
    val schedule: DailyPrayerSchedule = PrayerAndQiblaUtils.calculateOfflinePrayerSchedule(
        cityName = "Dhaka",
        latitude = 23.8103,
        longitude = 90.4125,
        method = PrayerMethod.KARACHI,
        asrHanafi = false,
    ),
    val monthlyCalendar: List<MonthlyPrayerDay> = emptyList(),
    val nextPrayerName: String = "Fajr",
    val nextPrayerTime: String = "",
    val countdownText: String = "--:--:--",
    val isFetchingLocation: Boolean = false,
    val locationStatus: String? = null,
)

class PrayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PrayerTimesRepository()
    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                updateCountdown(_uiState.value.schedule)
                delay(1_000L)
            }
        }
    }

    fun refreshPrayerTimes(
        cityName: String,
        latitude: Double,
        longitude: Double,
        methodIndex: Int,
        asrHanafi: Boolean,
    ) {
        val method = PrayerMethod.fromIndex(methodIndex)
        viewModelScope.launch {
            val schedule = repo.getDailySchedule(cityName, latitude, longitude, method, asrHanafi)
            _uiState.value = _uiState.value.copy(schedule = schedule)
            updateCountdown(schedule)
            val monthly = repo.getMonthlyCalendar(cityName, latitude, longitude, method, asrHanafi)
            _uiState.value = _uiState.value.copy(monthlyCalendar = monthly)
        }
    }

    @SuppressLint("MissingPermission")
    fun detectCurrentLocation(onLocationResolved: (city: String, lat: Double, lon: Double) -> Unit) {
        val ctx = getApplication<Application>()
        _uiState.value = _uiState.value.copy(isFetchingLocation = true, locationStatus = null)
        val client = LocationServices.getFusedLocationProviderClient(ctx)
        client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    viewModelScope.launch {
                        val city = resolveCityName(loc.latitude, loc.longitude)
                        _uiState.value = _uiState.value.copy(
                            isFetchingLocation = false,
                            locationStatus = "Location updated: $city"
                        )
                        onLocationResolved(city, loc.latitude, loc.longitude)
                    }
                } else {
                    client.lastLocation.addOnSuccessListener { last ->
                        if (last != null) {
                            viewModelScope.launch {
                                val city = resolveCityName(last.latitude, last.longitude)
                                _uiState.value = _uiState.value.copy(
                                    isFetchingLocation = false,
                                    locationStatus = "Location updated: $city"
                                )
                                onLocationResolved(city, last.latitude, last.longitude)
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isFetchingLocation = false,
                                locationStatus = "GPS unavailable — select city manually."
                            )
                        }
                    }.addOnFailureListener {
                        _uiState.value = _uiState.value.copy(
                            isFetchingLocation = false,
                            locationStatus = "Unable to read GPS; choose a city manually."
                        )
                    }
                }
            }
            .addOnFailureListener {
                _uiState.value = _uiState.value.copy(
                    isFetchingLocation = false,
                    locationStatus = "Location access unavailable; select city manually."
                )
            }
    }

    @Suppress("DEPRECATION")
    private suspend fun resolveCityName(lat: Double, lon: Double): String = withContext(Dispatchers.IO) {
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    var resultCity: String? = null
                    val latch = java.util.concurrent.CountDownLatch(1)
                    geocoder.getFromLocation(lat, lon, 1) { list ->
                        resultCity = list.firstOrNull()?.let { it.locality ?: it.subAdminArea ?: it.countryName }
                        latch.countDown()
                    }
                    latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
                    if (!resultCity.isNullOrBlank()) return@withContext resultCity!!
                } else {
                    val list = geocoder.getFromLocation(lat, lon, 1)
                    val name = list?.firstOrNull()?.let { it.locality ?: it.subAdminArea ?: it.countryName }
                    if (!name.isNullOrBlank()) return@withContext name
                }
            }
        } catch (_: Exception) {
        }
        String.format(Locale.US, "%.2f°, %.2f°", lat, lon)
    }

    private fun updateCountdown(schedule: DailyPrayerSchedule) {
        val now = Calendar.getInstance()
        val nowSec = now.get(Calendar.HOUR_OF_DAY) * 3600 +
            now.get(Calendar.MINUTE) * 60 +
            now.get(Calendar.SECOND)

        val slots = listOf(
            Triple("Fajr", schedule.fajrMinutes * 60, schedule.fajr),
            Triple("Dhuhr", schedule.dhuhrMinutes * 60, schedule.dhuhr),
            Triple("Asr", schedule.asrMinutes * 60, schedule.asr),
            Triple("Maghrib", schedule.maghribMinutes * 60, schedule.maghrib),
            Triple("Isha", schedule.ishaMinutes * 60, schedule.isha),
        )
        val next = slots.firstOrNull { it.second > nowSec }
        val (name, targetSec, timeStr) = if (next != null) {
            next
        } else {
            Triple("Fajr", schedule.fajrMinutes * 60 + 86400, schedule.fajr)
        }
        val diff = (targetSec - nowSec).coerceAtLeast(0)
        val h = diff / 3600
        val m = (diff % 3600) / 60
        val s = diff % 60
        val countdown = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
        _uiState.value = _uiState.value.copy(
            nextPrayerName = name,
            nextPrayerTime = timeStr,
            countdownText = countdown,
        )
    }
}
