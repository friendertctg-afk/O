package com.example.data.repository

import android.util.Log
import com.example.data.remote.NetworkModule
import com.example.utils.DailyPrayerSchedule
import com.example.utils.PrayerAndQiblaUtils
import com.example.utils.PrayerMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

data class MonthlyPrayerDay(
    val dayOfMonth: Int,
    val gregorian: String,
    val hijri: String,
    val fajr: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
)

class PrayerTimesRepository {

    suspend fun getDailySchedule(
        cityName: String,
        latitude: Double,
        longitude: Double,
        method: PrayerMethod,
        asrHanafi: Boolean,
    ): DailyPrayerSchedule = withContext(Dispatchers.IO) {
        val offline = PrayerAndQiblaUtils.calculateOfflinePrayerSchedule(
            cityName = cityName,
            latitude = latitude,
            longitude = longitude,
            method = method,
            asrHanafi = asrHanafi,
        )
        try {
            val school = if (asrHanafi) 1 else 0
            val response = NetworkModule.prayerApi.getTimings(
                latitude = latitude,
                longitude = longitude,
                method = method.aladhanId,
                school = school,
            )
            val data = response.data ?: return@withContext offline
            val t = data.timings
            val fMin = PrayerAndQiblaUtils.parseApiTimeToMinutes(t.Fajr)
            val sMin = PrayerAndQiblaUtils.parseApiTimeToMinutes(t.Sunrise)
            val dMin = PrayerAndQiblaUtils.parseApiTimeToMinutes(t.Dhuhr)
            val aMin = PrayerAndQiblaUtils.parseApiTimeToMinutes(t.Asr)
            val mMin = PrayerAndQiblaUtils.parseApiTimeToMinutes(t.Maghrib)
            val iMin = PrayerAndQiblaUtils.parseApiTimeToMinutes(t.Isha)
            val hijriStr = "${data.date.hijri.day} ${data.date.hijri.month.en} ${data.date.hijri.year} AH"

            DailyPrayerSchedule(
                cityName = cityName,
                gregorianDate = data.date.readable,
                hijriDate = hijriStr,
                fajr = PrayerAndQiblaUtils.formatMinutes(fMin),
                sunrise = PrayerAndQiblaUtils.formatMinutes(sMin),
                dhuhr = PrayerAndQiblaUtils.formatMinutes(dMin),
                asr = PrayerAndQiblaUtils.formatMinutes(aMin),
                maghrib = PrayerAndQiblaUtils.formatMinutes(mMin),
                isha = PrayerAndQiblaUtils.formatMinutes(iMin),
                fajrMinutes = fMin,
                sunriseMinutes = sMin,
                dhuhrMinutes = dMin,
                asrMinutes = aMin,
                maghribMinutes = mMin,
                ishaMinutes = iMin,
            )
        } catch (e: Exception) {
            Log.w("PrayerTimesRepo", "Using offline astronomical calculation: ${e.message}")
            offline
        }
    }

    suspend fun getMonthlyCalendar(
        cityName: String,
        latitude: Double,
        longitude: Double,
        method: PrayerMethod,
        asrHanafi: Boolean,
    ): List<MonthlyPrayerDay> = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        try {
            val school = if (asrHanafi) 1 else 0
            val res = NetworkModule.prayerApi.getMonthlyCalendar(
                latitude = latitude,
                longitude = longitude,
                method = method.aladhanId,
                school = school,
                month = month,
                year = year,
            )
            val days = res.data
            if (!days.isNullOrEmpty()) {
                return@withContext days.mapIndexed { idx, d ->
                    MonthlyPrayerDay(
                        dayOfMonth = idx + 1,
                        gregorian = d.date.readable,
                        hijri = "${d.date.hijri.day} ${d.date.hijri.month.en}",
                        fajr = PrayerAndQiblaUtils.formatMinutes(PrayerAndQiblaUtils.parseApiTimeToMinutes(d.timings.Fajr)),
                        dhuhr = PrayerAndQiblaUtils.formatMinutes(PrayerAndQiblaUtils.parseApiTimeToMinutes(d.timings.Dhuhr)),
                        asr = PrayerAndQiblaUtils.formatMinutes(PrayerAndQiblaUtils.parseApiTimeToMinutes(d.timings.Asr)),
                        maghrib = PrayerAndQiblaUtils.formatMinutes(PrayerAndQiblaUtils.parseApiTimeToMinutes(d.timings.Maghrib)),
                        isha = PrayerAndQiblaUtils.formatMinutes(PrayerAndQiblaUtils.parseApiTimeToMinutes(d.timings.Isha)),
                    )
                }
            }
        } catch (_: Exception) {
            // Fall through to offline 30-day generator
        }

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        (1..daysInMonth).map { day ->
            val dayCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }
            val s = PrayerAndQiblaUtils.calculateOfflinePrayerSchedule(
                cityName, latitude, longitude, method, asrHanafi, dayCal
            )
            MonthlyPrayerDay(
                dayOfMonth = day,
                gregorian = s.gregorianDate,
                hijri = s.hijriDate,
                fajr = s.fajr,
                dhuhr = s.dhuhr,
                asr = s.asr,
                maghrib = s.maghrib,
                isha = s.isha,
            )
        }
    }
}
