package com.example.utils

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

data class CityOption(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double,
)

data class DailyPrayerSchedule(
    val cityName: String,
    val gregorianDate: String,
    val hijriDate: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val fajrMinutes: Int,
    val sunriseMinutes: Int,
    val dhuhrMinutes: Int,
    val asrMinutes: Int,
    val maghribMinutes: Int,
    val ishaMinutes: Int,
)

enum class PrayerMethod(
    val index: Int,
    val aladhanId: Int,
    val displayName: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaIntervalMin: Int = 0,
) {
    KARACHI(1, 1, "University of Islamic Sciences, Karachi", 18.0, 18.0),
    ISNA(2, 2, "Islamic Society of North America (ISNA)", 15.0, 15.0),
    MWL(3, 3, "Muslim World League (MWL)", 18.0, 17.0),
    MAKKAH(4, 4, "Umm Al-Qura University, Makkah", 18.5, 0.0, 90),
    EGYPT(5, 5, "Egyptian General Authority of Survey", 19.5, 17.5);

    companion object {
        fun fromIndex(idx: Int): PrayerMethod = entries.firstOrNull { it.index == idx } ?: KARACHI
    }
}

object PrayerAndQiblaUtils {
    private const val KAABA_LAT = 21.422487
    private const val KAABA_LON = 39.826206

    val presetCities: List<CityOption> = listOf(
        CityOption("Dhaka", "Bangladesh", 23.8103, 90.4125, 6.0),
        CityOption("Chattogram", "Bangladesh", 22.3569, 91.7832, 6.0),
        CityOption("Makkah", "Saudi Arabia", 21.4225, 39.8262, 3.0),
        CityOption("Madinah", "Saudi Arabia", 24.4672, 39.6111, 3.0),
        CityOption("Riyadh", "Saudi Arabia", 24.7136, 46.6753, 3.0),
        CityOption("London", "UK", 51.5072, -0.1276, 0.0),
        CityOption("New York", "USA", 40.7128, -74.0060, -5.0),
        CityOption("Istanbul", "Turkey", 41.0082, 28.9784, 3.0),
        CityOption("Cairo", "Egypt", 30.0444, 31.2357, 2.0),
        CityOption("Jakarta", "Indonesia", -6.2088, 106.8456, 7.0),
        CityOption("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 8.0),
        CityOption("Karachi", "Pakistan", 24.8607, 67.0011, 5.0),
        CityOption("New Delhi", "India", 28.6139, 77.2090, 5.5),
        CityOption("Paris", "France", 48.8566, 2.3522, 1.0),
    )

    fun qiblaBearingDegrees(latitude: Double, longitude: Double): Double {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(KAABA_LAT)
        val deltaLambda = Math.toRadians(KAABA_LON - longitude)
        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val deg = Math.toDegrees(atan2(y, x))
        return (deg + 360.0) % 360.0
    }

    fun distanceToKaabaKm(latitude: Double, longitude: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(KAABA_LAT - latitude)
        val dLon = Math.toRadians(KAABA_LON - longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(latitude)) * cos(Math.toRadians(KAABA_LAT)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun calculateOfflinePrayerSchedule(
        cityName: String,
        latitude: Double,
        longitude: Double,
        method: PrayerMethod,
        asrHanafi: Boolean,
        calendar: Calendar = Calendar.getInstance(),
    ): DailyPrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val presetOffset = presetCities.firstOrNull { it.name.equals(cityName, ignoreCase = true) }?.timezoneOffsetHours
        val tzHours = presetOffset ?: (TimeZone.getDefault().getOffset(calendar.timeInMillis) / 3600000.0)

        val jd = julianDate(year, month, day) - longitude / (15.0 * 24.0)
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))) / 15.0
        val decl = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        val eqTime = q / 15.0 - fixHour(ra)

        val dhuhrHours = fixHour(12.0 + tzHours - longitude / 15.0 - eqTime)
        val sunriseDiff = hourAngle(latitude, decl, -0.833)
        val fajrDiff = hourAngle(latitude, decl, -method.fajrAngle)
        val sunsetDiff = hourAngle(latitude, decl, -0.833)

        val asrShadowFactor = if (asrHanafi) 2.0 else 1.0
        val asrAngle = Math.toDegrees(atan2(1.0, asrShadowFactor + tan(Math.toRadians(kotlin.math.abs(latitude - decl)))))
        val asrDiff = hourAngle(latitude, decl, asrAngle)

        val fajrH = dhuhrHours - fajrDiff
        val sunriseH = dhuhrHours - sunriseDiff
        val asrH = dhuhrHours + asrDiff
        val maghribH = dhuhrHours + sunsetDiff
        val ishaH = if (method.ishaIntervalMin > 0) {
            maghribH + method.ishaIntervalMin / 60.0
        } else {
            dhuhrHours + hourAngle(latitude, decl, -method.ishaAngle)
        }

        val fajrMin = hoursToMinutes(fajrH)
        val sunriseMin = hoursToMinutes(sunriseH)
        val dhuhrMin = hoursToMinutes(dhuhrHours)
        val asrMin = hoursToMinutes(asrH)
        val maghribMin = hoursToMinutes(maghribH)
        val ishaMin = hoursToMinutes(ishaH)

        val gregorian = String.format("%04d-%02d-%02d", year, month, day)
        val hijri = approximateHijriDate(year, month, day)

        return DailyPrayerSchedule(
            cityName = cityName,
            gregorianDate = gregorian,
            hijriDate = hijri,
            fajr = formatMinutes(fajrMin),
            sunrise = formatMinutes(sunriseMin),
            dhuhr = formatMinutes(dhuhrMin),
            asr = formatMinutes(asrMin),
            maghrib = formatMinutes(maghribMin),
            isha = formatMinutes(ishaMin),
            fajrMinutes = fajrMin,
            sunriseMinutes = sunriseMin,
            dhuhrMinutes = dhuhrMin,
            asrMinutes = asrMin,
            maghribMinutes = maghribMin,
            ishaMinutes = ishaMin,
        )
    }

    fun approximateHijriDate(year: Int, month: Int, day: Int): String {
        val jd = floor(julianDate(year, month, day)).toInt()
        val l = jd - 1948440 + 10632
        val n = floor((l - 1) / 10631.0).toInt()
        val l2 = l - 10631 * n + 354
        val j = (floor((10985 - l2) / 5316.0) * floor((50 * l2) / 17719.0) +
            floor(l2 / 5670.0) * floor((43 * l2) / 15238.0)).toInt()
        val l3 = l2 - (floor((30 - j) / 15.0) * floor((17719 * j) / 50.0) +
            floor(j / 16.0) * floor((15238 * j) / 43.0)).toInt() + 29
        val hMonth = floor((24 * l3) / 709.0).toInt()
        val hDay = l3 - floor((709 * hMonth) / 24.0).toInt()
        val hYear = 30 * n + j - 30
        val months = listOf(
            "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
            "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )
        val mName = months.getOrElse(hMonth - 1) { "Hijri" }
        return "$hDay $mName $hYear AH"
    }

    fun parseApiTimeToMinutes(raw: String): Int {
        val clean = raw.substringBefore(" ").trim()
        val parts = clean.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h * 60 + m
    }

    fun formatMinutes(totalMin: Int): String {
        val wrapped = ((totalMin % 1440) + 1440) % 1440
        val h24 = wrapped / 60
        val m = wrapped % 60
        val amPm = if (h24 >= 12) "PM" else "AM"
        val h12 = when {
            h24 == 0 -> 12
            h24 > 12 -> h24 - 12
            else -> h24
        }
        return String.format("%d:%02d %s", h12, m, amPm)
    }

    private fun hoursToMinutes(hours: Double): Int {
        val wrapped = ((hours % 24.0) + 24.0) % 24.0
        return (wrapped * 60.0 + 0.5).toInt()
    }

    private fun hourAngle(lat: Double, decl: Double, angle: Double): Double {
        val num = sin(Math.toRadians(angle)) - sin(Math.toRadians(lat)) * sin(Math.toRadians(decl))
        val den = cos(Math.toRadians(lat)) * cos(Math.toRadians(decl))
        val cosVal = (num / den).coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(cosVal)) / 15.0
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(a: Double): Double = ((a % 360.0) + 360.0) % 360.0
    private fun fixHour(a: Double): Double = ((a % 24.0) + 24.0) % 24.0
}
