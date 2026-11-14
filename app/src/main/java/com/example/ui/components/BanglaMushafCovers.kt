package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.LateefFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class BanglaMushafBookType {
    TAFSEER,
    HAFIZI_EMDADIA,
    COLOR_MADINAH,
    COLOR_TAJWEED,
    INDO_PAK_GREEN_GOLD,
    UTHMANI_CLASSIC,
}

@Composable
fun BanglaMushafCoverThumbnail(
    type: BanglaMushafBookType,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f),
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        shadowElevation = if (isSelected) 10.dp else 5.dp,
        border = if (isSelected) BorderStroke(2.2.dp, Color(0xFF2B4C3F)) else BorderStroke(0.7.dp, Color(0xFFD5DBD7)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp)
        ) {
            when (type) {
                BanglaMushafBookType.TAFSEER -> TafseerBlueCover()
                BanglaMushafBookType.HAFIZI_EMDADIA -> HafiziEmdadiaCover()
                BanglaMushafBookType.COLOR_MADINAH -> ColorMadinahCover()
                BanglaMushafBookType.COLOR_TAJWEED -> ColorTajweedCover()
                BanglaMushafBookType.INDO_PAK_GREEN_GOLD -> IndoPakGreenGoldCover()
                BanglaMushafBookType.UTHMANI_CLASSIC -> UthmaniClassicCover()
            }
        }
    }
}

@Composable
private fun TafseerBlueCover() {
    val royalBlue = Color(0xFF1A66C2)
    val skyBlue = Color(0xFF4C95E8)
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF9FCFF))) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOrnateThumbBorder(royalBlue, skyBlue, Color(0xFFF9FCFF), bandFraction = 0.085f)

            val cx = size.width / 2f
            val cy = size.height / 2f
            val rx = size.width * 0.25f
            val ry = size.height * 0.22f

            // Ornate Blue Central Medallion with top & bottom finials
            val outerMedallion = Path().apply {
                moveTo(cx, cy - ry * 1.45f)
                quadraticTo(cx + rx * 0.45f, cy - ry * 0.7f, cx + rx, cy)
                quadraticTo(cx + rx * 0.45f, cy + ry * 0.7f, cx, cy + ry * 1.45f)
                quadraticTo(cx - rx * 0.45f, cy + ry * 0.7f, cx - rx, cy)
                quadraticTo(cx - rx * 0.45f, cy - ry * 0.7f, cx, cy - ry * 1.45f)
                close()
            }
            drawPath(outerMedallion, color = Color(0xFFEAF3FE))
            drawPath(outerMedallion, color = royalBlue, style = Stroke(width = 1.6.dp.toPx()))

            val innerMedallion = Path().apply {
                moveTo(cx, cy - ry * 1.15f)
                quadraticTo(cx + rx * 0.35f, cy - ry * 0.55f, cx + rx * 0.78f, cy)
                quadraticTo(cx + rx * 0.35f, cy + ry * 0.55f, cx, cy + ry * 1.15f)
                quadraticTo(cx - rx * 0.35f, cy + ry * 0.55f, cx - rx * 0.78f, cy)
                quadraticTo(cx - rx * 0.35f, cy - ry * 0.55f, cx, cy - ry * 1.15f)
                close()
            }
            drawPath(innerMedallion, color = skyBlue.copy(alpha = 0.85f), style = Stroke(width = 0.9.dp.toPx()))
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "القرآن\nالكريم",
                fontFamily = AmiriFontFamily,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14509E),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HafiziEmdadiaCover() {
    val ink = Color(0xFF121212)
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOrnateThumbBorder(ink, Color(0xFF424242), Color.White, bandFraction = 0.10f)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "آياتها ٧",
                    fontFamily = LateefFontFamily,
                    fontSize = 8.sp,
                    color = ink,
                )
                Text(
                    text = "سُورَةُ الْفَاتِحَةِ مَكِّيَّةٌ",
                    fontFamily = LateefFontFamily,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ink,
                )
                Text(
                    text = "ركوعها ١",
                    fontFamily = LateefFontFamily,
                    fontSize = 8.sp,
                    color = ink,
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ",
                    fontFamily = LateefFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ink,
                    textAlign = TextAlign.Center
                )
                val miniLines = listOf(
                    "الْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِيْنَ ۝",
                    "الرَّحْمٰنِ الرَّحِيْمِ ۝ مَالِكِ يَوْمِ",
                    "الدِّيْنِ ۝ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ",
                    "نَسْتَعِيْنُ ۝ اهْدِنَا الصِّرَاطَ",
                    "الْمُسْتَقِيْمَ ۝ صِرَاطَ الَّذِيْنَ",
                    "أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوْبِ",
                    "عَلَيْهِمْ وَلَا الضَّآلِّيْنَ ۝"
                )
                miniLines.forEach { line ->
                    HorizontalDivider(thickness = 0.6.dp, color = ink.copy(alpha = 0.65f))
                    Text(
                        text = line,
                        fontFamily = LateefFontFamily,
                        fontSize = 8.5.sp,
                        lineHeight = 11.sp,
                        color = ink,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider(thickness = 0.6.dp, color = ink.copy(alpha = 0.65f))
            }
        }
    }
}

@Composable
private fun ColorMadinahCover() {
    val emerald = Color(0xFF0E744C)
    val gold = Color(0xFFD19E26)
    val crimson = Color(0xFFBA2B2B)
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFCFBF4))) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOrnateThumbBorder(emerald, crimson, Color(0xFFFCFBF4), bandFraction = 0.095f, tertiary = gold)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xFFE5F3EA),
                border = BorderStroke(1.dp, emerald),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "سُورَةُ الْبَقَرَةِ",
                    fontFamily = AmiriFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = emerald,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ",
                fontFamily = AmiriFontFamily,
                fontSize = 9.sp,
                color = Color(0xFF1D4D9A),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            // Soft tinted alternating page bands typical of Rainbow/Color Madinah Mushaf
            val bandColors = listOf(
                Color(0xFFEBF6EE),
                Color(0xFFFEF6E4),
                Color(0xFFEAF2FC),
                Color(0xFFFDF0F0),
                Color(0xFFEBF6EE),
            )
            val lines = listOf(
                "الٓمٓ ۝ ذٰلِكَ الْكِتَابُ لَا رَيْبَ فِيهِ",
                "هُدًى لِّلْمُتَّقِيْنَ ۝ الَّذِيْنَ يُؤْمِنُوْنَ",
                "بِالْغَيْبِ وَيُقِيْمُوْنَ الصَّلٰوةَ",
                "وَمِمَّا رَزَقْنَاهُمْ يُنْفِقُوْنَ ۝",
                "وَالَّذِيْنَ يُؤْمِنُوْنَ بِمَا أُنْزِلَ",
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                lines.forEachIndexed { idx, l ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bandColors[idx % bandColors.size], RoundedCornerShape(2.dp))
                            .padding(vertical = 1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = l,
                            fontFamily = LateefFontFamily,
                            fontSize = 8.5.sp,
                            color = Color(0xFF16201A),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorTajweedCover() {
    val gold = Color(0xFFC49222)
    val maroon = Color(0xFF7D241C)
    val forest = Color(0xFF1D4F39)
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFAF4E4))) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOrnateThumbBorder(forest, gold, Color(0xFFFAF4E4), bandFraction = 0.085f, tertiary = maroon)

            // Upper geometric star-dome medallion typical of Indo-Pak Color Tajweed Mushafs
            val pad = size.width * 0.11f
            val domeW = size.width - pad * 2
            val domeH = size.height * 0.34f
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(gold.copy(alpha = 0.35f), maroon.copy(alpha = 0.2f))),
                topLeft = Offset(pad, pad),
                size = Size(domeW, domeH),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
            )
            drawRoundRect(
                color = gold,
                topLeft = Offset(pad, pad),
                size = Size(domeW, domeH),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                style = Stroke(width = 1.1.dp.toPx())
            )
            val cx = size.width / 2f
            val cy = pad + domeH / 2f
            drawStarPoly(Offset(cx, cy), minOf(domeW, domeH) * 0.34f, 12, maroon, gold)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Surface(
                color = maroon.copy(alpha = 0.1f),
                border = BorderStroke(0.8.dp, gold),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "سُورَةُ الْبَقَرَةِ",
                    fontFamily = AmiriFontFamily,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = maroon,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ",
                fontFamily = LateefFontFamily,
                fontSize = 9.5.sp,
                color = Color(0xFF1D4F39),
                textAlign = TextAlign.Center
            )
            val tajweedLines = listOf(
                Triple("الٓمٓ ۝ ", "ذٰلِكَ الْكِتٰبُ ", Color(0xFFB71C1C)),
                Triple("لَا رَيْبَ فِيهِ ", "هُدًى لِّلْمُتَّقِيْنَ", Color(0xFF1565C0)),
                Triple("الَّذِيْنَ يُؤْمِنُوْنَ ", "بِالْغَيْبِ وَيُقِيْمُوْنَ", Color(0xFF2E7D32)),
                Triple("الصَّلٰوةَ وَمِمَّا ", "رَزَقْنَٰهُمْ يُنْفِقُوْنَ", Color(0xFFD84315)),
            )
            tajweedLines.forEach { (part1, part2, accent) ->
                HorizontalDivider(thickness = 0.5.dp, color = gold.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = part2,
                        fontFamily = LateefFontFamily,
                        fontSize = 8.sp,
                        color = accent,
                        maxLines = 1
                    )
                    Text(
                        text = part1,
                        fontFamily = LateefFontFamily,
                        fontSize = 8.sp,
                        color = Color(0xFF181818),
                        maxLines = 1
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
private fun IndoPakGreenGoldCover() {
    val deepGreen = Color(0xFF1A4B37)
    val gold = Color(0xFFC89928)
    val terracotta = Color(0xFFA8422B)
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFAF8F0))) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOrnateThumbBorder(deepGreen, terracotta, Color(0xFFFAF8F0), bandFraction = 0.095f, tertiary = gold)

            // Ornate Mughal/Indo-Pak emerald floral arch medallion in top half
            val pad = size.width * 0.12f
            val archW = size.width - pad * 2
            val archH = size.height * 0.38f
            val archPath = Path().apply {
                moveTo(pad, pad + archH)
                lineTo(pad, pad + archH * 0.45f)
                quadraticTo(size.width / 2f, pad - archH * 0.1f, pad + archW, pad + archH * 0.45f)
                lineTo(pad + archW, pad + archH)
                close()
            }
            drawPath(archPath, color = deepGreen.copy(alpha = 0.14f))
            drawPath(archPath, color = gold, style = Stroke(width = 1.2.dp.toPx()))
            drawStarPoly(Offset(size.width / 2f, pad + archH * 0.52f), minOf(archW, archH) * 0.27f, 10, deepGreen, gold)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Surface(
                color = deepGreen,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ",
                    fontFamily = LateefFontFamily,
                    fontSize = 9.5.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            val indoPakLines = listOf(
                "الْحَمْدُ لِلّٰهِ رَبِّ الْعٰلَمِيْنَ ۝",
                "الرَّحْمٰنِ الرَّحِيْمِ ۝ مٰلِكِ يَوْمِ الدِّيْنِ",
                "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِيْنُ ۝",
                "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيْمَ ۝"
            )
            indoPakLines.forEach { line ->
                HorizontalDivider(thickness = 0.5.dp, color = deepGreen.copy(alpha = 0.35f))
                Text(
                    text = line,
                    fontFamily = LateefFontFamily,
                    fontSize = 8.5.sp,
                    lineHeight = 11.sp,
                    color = Color(0xFF15201A),
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(thickness = 0.5.dp, color = deepGreen.copy(alpha = 0.35f))
        }
    }
}

@Composable
private fun UthmaniClassicCover() {
    val ink = Color(0xFF171A19)
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val outer = size.width * 0.06f
            val inner = size.width * 0.095f
            drawRect(
                color = ink.copy(alpha = 0.25f),
                topLeft = Offset(outer, outer),
                size = Size(size.width - outer * 2, size.height - outer * 2),
                style = Stroke(width = 1.dp.toPx())
            )
            drawRect(
                color = ink.copy(alpha = 0.55f),
                topLeft = Offset(inner, inner),
                size = Size(size.width - inner * 2, size.height - inner * 2),
                style = Stroke(width = 0.7.dp.toPx())
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                fontFamily = AmiriFontFamily,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = ink,
                textAlign = TextAlign.Center
            )
            val uthmaniLines = listOf(
                "الٓمٓ ۝ ذَٰلِكَ ٱلْكِتَٰبُ لَا رَيْبَ فِيهِ",
                "هُدًى لِّلْمُتَّقِينَ ۝ ٱلَّذِينَ يُؤْمِنُونَ",
                "بِٱلْغَيْبِ وَيُقِيمُونَ ٱلصَّلَوٰةَ",
                "وَمِمَّا رَزَقْنَٰهُمْ يُنفِقُونَ ۝",
                "وَٱلَّذِينَ يُؤْمِنُونَ بِمَآ أُنزِلَ",
                "إِلَيْكَ وَمَآ أُنزِلَ مِن قَبْلِكَ",
                "وَبِٱلْءَاخِرَةِ هُمْ يُوقِنُونَ ۝",
                "أُو۟لَٰٓئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ"
            )
            uthmaniLines.forEach { line ->
                Text(
                    text = line,
                    fontFamily = AmiriFontFamily,
                    fontSize = 8.sp,
                    lineHeight = 11.5.sp,
                    color = ink,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun DrawScope.drawOrnateThumbBorder(
    primary: Color,
    secondary: Color,
    innerBg: Color,
    bandFraction: Float,
    tertiary: Color? = null,
) {
    val band = size.width * bandFraction
    drawRect(
        color = primary,
        topLeft = Offset.Zero,
        size = size,
        style = Stroke(width = 1.5.dp.toPx())
    )
    // Repeating scalloped arabesque dots around border track
    val step = 6.dp.toPx()
    var x = band * 0.6f
    while (x < size.width - band * 0.5f) {
        drawCircle(color = primary.copy(alpha = 0.85f), radius = 1.5.dp.toPx(), center = Offset(x, band * 0.5f))
        drawCircle(color = secondary, radius = 0.8.dp.toPx(), center = Offset(x, band * 0.5f))
        drawCircle(color = primary.copy(alpha = 0.85f), radius = 1.5.dp.toPx(), center = Offset(x, size.height - band * 0.5f))
        drawCircle(color = secondary, radius = 0.8.dp.toPx(), center = Offset(x, size.height - band * 0.5f))
        x += step
    }
    var y = band * 0.6f
    while (y < size.height - band * 0.5f) {
        drawCircle(color = primary.copy(alpha = 0.85f), radius = 1.5.dp.toPx(), center = Offset(band * 0.5f, y))
        drawCircle(color = secondary, radius = 0.8.dp.toPx(), center = Offset(band * 0.5f, y))
        drawCircle(color = primary.copy(alpha = 0.85f), radius = 1.5.dp.toPx(), center = Offset(size.width - band * 0.5f, y))
        drawCircle(color = secondary, radius = 0.8.dp.toPx(), center = Offset(size.width - band * 0.5f, y))
        y += step
    }
    drawRect(
        color = innerBg,
        topLeft = Offset(band, band),
        size = Size(size.width - band * 2, size.height - band * 2)
    )
    drawRect(
        color = tertiary ?: primary,
        topLeft = Offset(band, band),
        size = Size(size.width - band * 2, size.height - band * 2),
        style = Stroke(width = 1.1.dp.toPx())
    )
}

private fun DrawScope.drawStarPoly(
    center: Offset,
    radius: Float,
    points: Int,
    strokeColor: Color,
    fillColor: Color,
) {
    val path = Path()
    for (i in 0 until points * 2) {
        val angle = (i * PI / points).toFloat()
        val r = if (i % 2 == 0) radius else radius * 0.72f
        val px = center.x + r * cos(angle)
        val py = center.y + r * sin(angle)
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path, color = fillColor.copy(alpha = 0.35f))
    drawPath(path, color = strokeColor, style = Stroke(width = 1.dp.toPx()))
}

@Composable
fun SurahStarBadge(
    numberText: String,
    ringColor: Color = Color(0xFF5A6560),
    textColor: Color = Color(0xFF2B4C3F),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(44.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val rOuter = minOf(size.width, size.height) * 0.46f
            val rInner = rOuter * 0.82f
            val points = 16
            val starPath = Path()
            for (i in 0 until points * 2) {
                val angle = (i * PI / points).toFloat()
                val r = if (i % 2 == 0) rOuter else rInner
                val x = cx + r * cos(angle)
                val y = cy + r * sin(angle)
                if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
            }
            starPath.close()
            drawPath(starPath, color = ringColor, style = Stroke(width = 1.25.dp.toPx()))
            drawCircle(
                color = ringColor.copy(alpha = 0.6f),
                radius = rInner * 0.84f,
                center = Offset(cx, cy),
                style = Stroke(width = 0.85.dp.toPx())
            )
        }
        Text(
            text = numberText,
            fontFamily = HindSiliguriFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = if (numberText.length >= 3) 11.5.sp else 13.5.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SurahOriginIcon(
    isMeccan: Boolean,
    modifier: Modifier = Modifier,
) {
    val forestGreen = Color(0xFF2B7A53)
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        if (isMeccan) {
            // Miniature Kaaba icon (charcoal + gold kiswah band) matching reference screenshot
            val left = w * 0.2f
            val top = h * 0.22f
            val boxW = w * 0.6f
            val boxH = h * 0.62f
            drawRect(
                color = Color(0xFF232825),
                topLeft = Offset(left, top),
                size = Size(boxW, boxH)
            )
            drawRect(
                color = Color(0xFFD6A832),
                topLeft = Offset(left, top + boxH * 0.26f),
                size = Size(boxW, boxH * 0.14f)
            )
            // Small mosque minarets behind Kaaba
            drawLine(
                color = forestGreen,
                start = Offset(w * 0.14f, h * 0.84f),
                end = Offset(w * 0.14f, h * 0.28f),
                strokeWidth = 1.4.dp.toPx()
            )
            drawLine(
                color = forestGreen,
                start = Offset(w * 0.86f, h * 0.84f),
                end = Offset(w * 0.86f, h * 0.28f),
                strokeWidth = 1.4.dp.toPx()
            )
        } else {
            // Miniature Green Prophet's Mosque dome & minaret for Medinan Surahs
            val baseTop = h * 0.58f
            drawRect(
                color = Color(0xFFDDE5DF),
                topLeft = Offset(w * 0.22f, baseTop),
                size = Size(w * 0.62f, h * 0.28f)
            )
            val dome = Path().apply {
                moveTo(w * 0.28f, baseTop)
                quadraticTo(w * 0.53f, h * 0.18f, w * 0.78f, baseTop)
                close()
            }
            drawPath(dome, color = forestGreen)
            // Crescent finial
            drawLine(
                color = forestGreen,
                start = Offset(w * 0.53f, h * 0.31f),
                end = Offset(w * 0.53f, h * 0.20f),
                strokeWidth = 1.dp.toPx()
            )
            // Tall slender green minaret on left
            drawRect(
                color = forestGreen.copy(alpha = 0.88f),
                topLeft = Offset(w * 0.15f, h * 0.22f),
                size = Size(w * 0.10f, h * 0.64f)
            )
        }
    }
}
