package com.example.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.ui.theme.TajweedGhunnah
import com.example.ui.theme.TajweedIdgham
import com.example.ui.theme.TajweedIkhfa
import com.example.ui.theme.TajweedIqlab
import com.example.ui.theme.TajweedMadd
import com.example.ui.theme.TajweedQalqalah

object TajweedParser {
    private val qalqalahLetters = setOf('ق', 'ط', 'ب', 'ج', 'د')
    private val sukunChar = '\u0652'
    private val shaddaChar = '\u0651'
    private val maddMark = '\u0653'

    fun parseTajweedAnnotated(
        tajweedOrArabic: String,
        baseColor: Color,
    ): AnnotatedString {
        // Check if string has AlQuran Cloud Tajweed bracket tokens like [g[نّ]] or [q:12[د]]
        if (tajweedOrArabic.contains("[") && tajweedOrArabic.contains("]")) {
            return parseBracketedTajweed(tajweedOrArabic, baseColor)
        }
        return highlightHeuristicTajweed(tajweedOrArabic, baseColor)
    }

    private fun parseBracketedTajweed(input: String, baseColor: Color): AnnotatedString = buildAnnotatedString {
        var i = 0
        while (i < input.length) {
            if (input[i] == '[') {
                val innerOpen = input.indexOf('[', i + 1)
                val close = if (innerOpen != -1) input.indexOf(']', innerOpen + 1) else -1
                if (innerOpen != -1 && close != -1) {
                    val tag = input.substring(i + 1, innerOpen).substringBefore(':')
                    val text = input.substring(innerOpen + 1, close)
                    val color = colorForTag(tag, baseColor)
                    withStyle(SpanStyle(color = color)) {
                        append(text)
                    }
                    i = close + 1
                    continue
                }
            }
            withStyle(SpanStyle(color = baseColor)) {
                append(input[i])
            }
            i++
        }
    }

    private fun colorForTag(tag: String, fallback: Color): Color = when (tag) {
        "g" -> TajweedGhunnah
        "a", "u", "n", "id" -> TajweedIdgham
        "f" -> TajweedIkhfa
        "i" -> TajweedIqlab
        "q" -> TajweedQalqalah
        "m", "p", "o" -> TajweedMadd
        "h", "l", "s" -> fallback.copy(alpha = 0.55f)
        else -> fallback
    }

    private fun highlightHeuristicTajweed(text: String, baseColor: Color): AnnotatedString = buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            val ch = text[i]
            val next = text.getOrNull(i + 1)
            val color = when {
                (ch == 'ن' || ch == 'م') && next == shaddaChar -> TajweedGhunnah
                ch in qalqalahLetters && next == sukunChar -> TajweedQalqalah
                next == maddMark -> TajweedMadd
                else -> baseColor
            }
            withStyle(SpanStyle(color = color)) {
                append(ch)
            }
            i++
        }
    }
}
