package com.example.data.remote

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class AlQuranMultiEditionResponse(
    val code: Int,
    val status: String,
    val data: List<AlQuranSurahEditionDto>,
)

@JsonClass(generateAdapter = true)
data class AlQuranSurahEditionDto(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int,
    val ayahs: List<AlQuranAyahDto>,
)

@JsonClass(generateAdapter = true)
data class AlQuranAyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val page: Int,
    val ruku: Int = 1,
    val hizbQuarter: Int = 1,
    val surah: AlQuranMatchSurahDto? = null,
)

@JsonClass(generateAdapter = true)
data class AlQuranPageResponse(
    val code: Int,
    val status: String,
    val data: AlQuranPageDataDto?,
)

@JsonClass(generateAdapter = true)
data class AlQuranPageDataDto(
    val number: Int,
    val ayahs: List<AlQuranAyahDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class AlQuranSearchResponse(
    val code: Int,
    val status: String,
    val data: AlQuranSearchDataDto?,
)

@JsonClass(generateAdapter = true)
data class AlQuranSearchDataDto(
    val count: Int,
    val matches: List<AlQuranSearchMatchDto>,
)

@JsonClass(generateAdapter = true)
data class AlQuranSearchMatchDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val surah: AlQuranMatchSurahDto,
)

@JsonClass(generateAdapter = true)
data class AlQuranMatchSurahDto(
    val number: Int,
    val name: String,
    val englishName: String,
)

interface QuranApiService {
    @GET("v1/surah/{surahNumber}/editions/{editions}")
    suspend fun getSurahEditions(
        @Path("surahNumber") surahNumber: Int,
        @Path("editions") editions: String,
    ): AlQuranMultiEditionResponse

    @GET("v1/page/{pageNumber}/{edition}")
    suspend fun getPageEdition(
        @Path("pageNumber") pageNumber: Int,
        @Path("edition") edition: String,
    ): AlQuranPageResponse

    @GET("v1/search/{keyword}/all/{edition}")
    suspend fun searchQuran(
        @Path("keyword") keyword: String,
        @Path("edition") edition: String,
    ): AlQuranSearchResponse
}

@JsonClass(generateAdapter = true)
data class AladhanTimingsResponse(
    val code: Int,
    val status: String,
    val data: AladhanDataDto?,
)

@JsonClass(generateAdapter = true)
data class AladhanCalendarResponse(
    val code: Int,
    val status: String,
    val data: List<AladhanDataDto>?,
)

@JsonClass(generateAdapter = true)
data class AladhanDataDto(
    val timings: AladhanTimingsDto,
    val date: AladhanDateDto,
)

@JsonClass(generateAdapter = true)
data class AladhanTimingsDto(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Isha: String,
)

@JsonClass(generateAdapter = true)
data class AladhanDateDto(
    val readable: String,
    val hijri: AladhanHijriDto,
)

@JsonClass(generateAdapter = true)
data class AladhanHijriDto(
    val date: String,
    val day: String,
    val month: AladhanHijriMonthDto,
    val year: String,
)

@JsonClass(generateAdapter = true)
data class AladhanHijriMonthDto(
    val number: Int,
    val en: String,
    val ar: String,
)

interface PrayerApiService {
    @GET("v1/timings")
    suspend fun getTimings(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
        @Query("school") school: Int,
    ): AladhanTimingsResponse

    @GET("v1/calendar")
    suspend fun getMonthlyCalendar(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
        @Query("school") school: Int,
        @Query("month") month: Int,
        @Query("year") year: Int,
    ): AladhanCalendarResponse
}

object NetworkModule {
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .build()
    }

    val quranApi: QuranApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(QuranApiService::class.java)
    }

    val prayerApi: PrayerApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PrayerApiService::class.java)
    }

    val downloadClient: OkHttpClient
        get() = okHttpClient
}
