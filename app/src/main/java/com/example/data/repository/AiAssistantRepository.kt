package com.example.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AiLearningMode(
    val key: String,
    val label: String,
    val systemContext: String,
) {
    DAILY_GUIDANCE("daily", "Daily Islamic Guidance", "Focus on practical everyday worship, Salah, Wudu, fasting, Zakat, and character."),
    QURAN_LEARNING("quran", "Quran Study", "Focus on explaining Quranic verses, themes, context of revelation, and clear linguistic beauty."),
    TAJWEED("tajweed", "Tajweed Coach", "Focus on Makharij, Sifat, Noon/Meem Sakinah, Madd, Qalqalah, and practical pronunciation tips."),
    HIFZ("hifz", "Hifz Mentor", "Focus on memorization strategies, revision schedules, mutashabihat (similar verses), and retention."),
    ARABIC("arabic", "Arabic Teacher", "Focus on Arabic alphabet, Harakat, Nahw, Sarf, root letters, and Quranic vocabulary with Bengali & English clarity."),
    HADITH("hadith", "Hadith Research", "Focus on authentic Hadith collections (Bukhari, Muslim, Nawawi, etc.), narrators, grading, and practical lessons."),
    TAFSIR("tafsir", "Tafsir Explorer", "Focus on classical Tafsir insights (Ibn Kathir, Jalalayn, Al-Sa'di) and verified linguistic commentary."),
    HISTORY("history", "Islamic History", "Focus on Seerah of Prophet Muhammad (SAW), Prophets, Sahabah, and classical Islamic civilization.");
}

class AiAssistantRepository {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are the HIDAYAH AI Islamic Assistant — a respectful, clear, and evidence-conscious Islamic educational companion inside the HIDAYAH app (Founded and Owned by Mohammed Touhidul Islam, Son of Sirajul Islam).

                    MANDATORY RELIGIOUS ACCURACY & SAFETY RULES:
                    1. NEVER fabricate or invent Quranic Arabic verses or Hadith texts. Only cite exact Surah:Ayah numbers or Hadith books/collections you are genuinely certain of; if unsure of an exact number or grading, state clearly that you cannot verify the exact reference and advise checking an authentic source or scholar.
                    2. NEVER claim to be a Mufti or issue binding personal Fatwas. For complex personal legal/fiqh rulings (divorce, inheritance, complex finance, medical exceptions), advise consulting a qualified local Islamic scholar or Mufti.
                    3. Clearly distinguish between:
                       - Quranic Evidence
                       - Authentic Hadith / Sunnah
                       - Recognized Scholarly Differences (Madhhabs / Ikhtilaf)
                       - General Practical Guidance
                    4. Automatically recognize the user's language (English, Bengali, Arabic, Urdu, or Hindi) and reply in that same language unless they ask for another language. Understand informal phrasing or minor spelling mistakes gracefully.
                    5. Structure answers concisely and warmly:
                       - Direct Answer (simple, clear)
                       - Evidence & References (when relevant)
                       - Scholarly Nuances (only if applicable)
                       - Practical Next Step + helpful follow-up question.
                    """.trimIndent()
                )
            }
        )
    }

    fun streamAnswer(
        userQuestion: String,
        mode: AiLearningMode,
        preferredLanguageName: String,
    ): Flow<String> {
        val enrichedPrompt = """
            [Mode: ${mode.label} — ${mode.systemContext}]
            [User preferred UI language: $preferredLanguageName — reply in the user's prompt language if clearly written in Bengali/Arabic/Urdu/Hindi/English]
            Question: $userQuestion
        """.trimIndent()

        return model.generateContentStream(enrichedPrompt).map { chunk ->
            chunk.text.orEmpty()
        }
    }
}
