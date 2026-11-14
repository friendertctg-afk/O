package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold),
)

val HindSiliguriFontFamily = FontFamily(
    Font(R.font.hind_siliguri, FontWeight.Normal),
    Font(R.font.hind_siliguri, FontWeight.Medium),
    Font(R.font.hind_siliguri, FontWeight.SemiBold),
    Font(R.font.hind_siliguri, FontWeight.Bold),
)

val NotoSansBengaliFontFamily = FontFamily(
    Font(R.font.noto_sans_bengali, FontWeight.Normal),
    Font(R.font.noto_sans_bengali, FontWeight.Bold),
)

val LateefFontFamily = FontFamily(
    Font(R.font.lateef, FontWeight.Normal),
)

val GulzarFontFamily = FontFamily(
    Font(R.font.gulzar, FontWeight.Normal),
)

val NotoNastaliqUrduFontFamily = FontFamily(
    Font(R.font.noto_nastaliq_urdu, FontWeight.Normal),
)

val AmiriFontFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
)

val ScheherazadeFontFamily = FontFamily(
    Font(R.font.scheherazade_new, FontWeight.Normal),
)

val NotoNaskhArabicFontFamily = FontFamily(
    Font(R.font.noto_naskh_arabic, FontWeight.Normal),
)

fun ArabicFontChoice.toFontFamily(): FontFamily = when (this) {
    ArabicFontChoice.LATEEF -> LateefFontFamily
    ArabicFontChoice.GULZAR -> GulzarFontFamily
    ArabicFontChoice.NOTO_NASTALIQ -> NotoNastaliqUrduFontFamily
    ArabicFontChoice.AMIRI -> AmiriFontFamily
    ArabicFontChoice.SCHEHERAZADE -> ScheherazadeFontFamily
    ArabicFontChoice.NOTO_NASKH -> NotoNaskhArabicFontFamily
}

val AlQuranTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 26.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    ),
)
