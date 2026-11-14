package com.example.data.seed

data class TafsirEntry(
    val id: String,
    val source: String, // "Ibn Kathir", "Tafsir al-Jalalayn", "Tafsir al-Sa'di"
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabicAyah: String,
    val titleEn: String,
    val summaryEn: String,
    val summaryBn: String,
)

data class KidsLesson(
    val id: String,
    val category: String, // "Noorani Qaida", "Short Surahs", "Basic Duas", "Islamic Manners"
    val titleEn: String,
    val titleBn: String,
    val arabicSample: String,
    val transliteration: String,
    val lessonTextEn: String,
    val lessonTextBn: String,
)

data class DigitalLibraryItem(
    val id: String,
    val category: String, // "Books", "Study Guides", "Audio Lectures", "Islamic History"
    val titleEn: String,
    val author: String,
    val language: String,
    val pagesOrDuration: String,
    val summaryEn: String,
    val keyTakeaways: List<String>,
)

data class SunnahHabitTemplate(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val arabicTag: String,
)

object HidayahExtendedData {
    val extraHadiths: List<HadithCollectionEntry> = listOf(
        HadithCollectionEntry(
            id = "muwatta_1",
            collection = "Muwatta Imam Malik",
            number = "Book 47, Hadith 8",
            narrator = "Yahya related from Malik",
            arabicText = "إِنَّمَا بُعِثْتُ لِأُتَمِّمَ حُسْنَ الْأَخْلَاقِ",
            englishText = "I was sent only to perfect noble character.",
            bengaliText = "আমি তো কেবল উত্তম চরিত্রের পূর্ণতা সাধনের জন্যই প্রেরিত হয়েছি।",
            urduText = "مجھے صرف حسنِ اخلاق کی تکمیل کے لیے بھیجا گیا ہے۔",
            grade = "Sahih",
        ),
        HadithCollectionEntry(
            id = "riyad_1",
            collection = "Riyad as-Salihin",
            number = "Book 1, Hadith 21 (Muslim 2564)",
            narrator = "Abu Hurairah (RA)",
            arabicText = "إِنَّ اللَّهَ لَا يَنْظُرُ إِلَى صُوَرِكُمْ وَأَمْوَالِكُمْ، وَلَكِنْ يَنْظُرُ إِلَى قُلُوبِكُمْ وَأَعْمَالِكُمْ",
            englishText = "Verily, Allah does not look at your appearances or wealth, rather He looks at your hearts and your deeds.",
            bengaliText = "নিশ্চয়ই আল্লাহ তোমাদের চেহারা ও সম্পদের দিকে তাকান না; বরং তিনি তোমাদের অন্তর ও আমলের দিকে তাকান।",
            urduText = "بے شک اللہ تمہاری صورتوں اور مالوں کو نہیں دیکھتا، بلکہ تمہارے دلوں اور اعمال کو دیکھتا ہے۔",
            grade = "Sahih (Muslim)",
        ),
    )

    val allHadithCollections: List<String> = listOf(
        "40 Hadith Nawawi",
        "Sahih Bukhari",
        "Sahih Muslim",
        "Sunan Abu Dawood",
        "Jami' at-Tirmidhi",
        "Sunan an-Nasa'i",
        "Sunan Ibn Majah",
        "Muwatta Imam Malik",
        "Riyad as-Salihin",
    )

    val allHadiths: List<HadithCollectionEntry> = IslamicLibraryData.hadiths + extraHadiths

    val tafsirSources: List<String> = listOf(
        "Tafsir Ibn Kathir",
        "Tafsir al-Jalalayn",
        "Tafsir al-Sa'di",
    )

    val tafsirs: List<TafsirEntry> = listOf(
        TafsirEntry(
            id = "ik_1_1",
            source = "Tafsir Ibn Kathir",
            surahNumber = 1,
            ayahNumber = 1,
            arabicAyah = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
            titleEn = "Al-Fatihah 1:1 — Starting with Allah's Name",
            summaryEn = "Ibn Kathir explains that 'Ar-Rahman' denotes Allah's vast mercy encompassing all creation, while 'Ar-Rahim' denotes His special mercy reserved for believers. Beginning every righteous act with Bismillah invokes blessing and sincerity.",
            summaryBn = "ইবনে কাসীর (রহ.) ব্যাখ্যা করেন: 'রহমান' অর্থ সকল সৃষ্টির প্রতি ব্যাপক দয়া, আর 'রহীম' অর্থ মুমিনদের প্রতি বিশেষ দয়া। প্রতিটি নেক কাজ বিসমিল্লাহ দিয়ে শুরু করা বরকতের উৎস।",
        ),
        TafsirEntry(
            id = "ik_1_5",
            source = "Tafsir Ibn Kathir",
            surahNumber = 1,
            ayahNumber = 5,
            arabicAyah = "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
            titleEn = "Al-Fatihah 1:5 — Worship & Seeking Help Alone",
            summaryEn = "Scholars note this verse is the core secret of the Quran: 'Iyyaka na'budu' frees the heart from shirk and hypocrisy, while 'Iyyaka nasta'in' frees the heart from arrogance and reliance on other than Allah.",
            summaryBn = "এই আয়াত কুরআনের মূল সারমর্ম: একমাত্র আল্লাহর ইবাদত শিরক থেকে রক্ষা করে এবং একমাত্র তাঁরই সাহায্য প্রার্থনা অহংকার ও পরনির্ভরতা থেকে অন্তরকে মুক্ত করে।",
        ),
        TafsirEntry(
            id = "jalalayn_112",
            source = "Tafsir al-Jalalayn",
            surahNumber = 112,
            ayahNumber = 1,
            arabicAyah = "قُلْ هُوَ ٱللَّهُ أَحَدٌ • ٱللَّهُ ٱلصَّمَدُ",
            titleEn = "Al-Ikhlas 112:1-4 — Pure Tawhid",
            summaryEn = "Al-Jalalayn clarifies that 'Ahad' affirms Allah's indivisible oneness without peer, and 'As-Samad' means the Eternal Master to whom all creation turns in every need while He needs none.",
            summaryBn = "তাফসীরে জালালাইন অনুযায়ী 'আহাদ' অর্থ এক ও অদ্বিতীয়, এবং 'আস-সামাদ' অর্থ এমন অমুখাপেক্ষী সত্তা যাঁর উপর সকল সৃষ্টি নির্ভরশীল।",
        ),
        TafsirEntry(
            id = "sadi_103",
            source = "Tafsir al-Sa'di",
            surahNumber = 103,
            ayahNumber = 1,
            arabicAyah = "وَٱلْعَصْرِ • إِنَّ ٱلْإِنسَٰنَ لَفِى خُسْرٍ",
            titleEn = "Al-'Asr 103:1-3 — The Four Pillars of Salvation",
            summaryEn = "Imam As-Sa'di highlights the four essential qualities saving humanity from loss: authentic Faith (Iman), righteous Deeds ('Amal Salih), mutually encouraging Truth (Haqq), and steadfast Patience (Sabr).",
            summaryBn = "আল্লামা সা'দী (রহ.) বলেন: ক্ষতি থেকে মুক্তির চারটি স্তম্ভ হলো—ঈমান, নেক আমল, সত্যের উপদেশ এবং ধৈর্যের পরস্পর উপদেশ।",
        ),
    )

    val kidsLessons: List<KidsLesson> = listOf(
        KidsLesson(
            id = "kids_qaida_1",
            category = "Noorani Qaida",
            titleEn = "Lesson 1: Recognizing Alif, Ba, Ta, Tha",
            titleBn = "নূরানী কায়দা পাঠ ১: আলিফ, বা, তা, ছা",
            arabicSample = "ا  ب  ت  ث",
            transliteration = "Alif • Ba • Ta • Tha",
            lessonTextEn = "Tip for kids: Alif stands tall like a pillar. Ba has 1 dot below, Ta has 2 dots above like smiling eyes, and Tha has 3 dots like a little crown!",
            lessonTextBn = "বাচ্চাদের জন্য সহজ টিপস: আলিফ সোজা দাঁড়ানো লাঠির মতো। বা-এর নিচে ১ ফোঁটা, তা-এর উপরে ২ ফোঁটা এবং ছা-এর উপরে ৩ ফোঁটা থাকে!",
        ),
        KidsLesson(
            id = "kids_qaida_2",
            category = "Noorani Qaida",
            titleEn = "Lesson 2: Joyful Harakat (Fathah, Kasrah, Dammah)",
            titleBn = "নূরানী কায়দা পাঠ ২: যবর, যের ও পেশ",
            arabicSample = "بَ  بِ  بُ",
            transliteration = "Ba • Bi • Bu",
            lessonTextEn = "Open your mouth for Fathah (a), smile gently for Kasrah (i), and round your lips like an 'O' for Dammah (u)!",
            lessonTextBn = "যবরে মুখ খুলে 'আ', যেরে নিচের দিকে 'ই' এবং পেশে দুই ঠোঁট গোল করে 'উ' উচ্চারণ করো!",
        ),
        KidsLesson(
            id = "kids_dua_1",
            category = "Basic Duas",
            titleEn = "Before Drinking Milk or Water",
            titleBn = "দুধ ও পানি পান করার দোয়া",
            arabicSample = "بِسْمِ اللَّهِ",
            transliteration = "Bismillah (Start) • Alhamdulillah (Finish)",
            lessonTextEn = "Always sit down, hold with your right hand, say Bismillah before drinking, and say Alhamdulillah when finished.",
            lessonTextBn = "বসে ডান হাতে বিসমিল্লাহ বলে পান করো এবং শেষে আলহামদুলিল্লাহ বলো।",
        ),
        KidsLesson(
            id = "kids_manners_1",
            category = "Islamic Manners",
            titleEn = "Speaking Truth & Sharing with Others",
            titleBn = "সত্য কথা বলা ও সবার সাথে ভাগ করে নেওয়া",
            arabicSample = "الصِّدْقُ نَجَاةٌ",
            transliteration = "As-Sidqu Najah (Truthfulness is salvation)",
            lessonTextEn = "A Muslim child always tells the truth, greets elders with As-Salamu Alaikum, and shares toys kindly.",
            lessonTextBn = "একজন ভালো মুসলিম শিশু সর্বদা সত্য বলে, বড়দের সালাম দেয় এবং নিজের জিনিস অন্যের সাথে ভাগ করে নেয়।",
        ),
    )

    val digitalLibraryItems: List<DigitalLibraryItem> = listOf(
        DigitalLibraryItem(
            id = "lib_1",
            category = "Books",
            titleEn = "Forty Hadith of Imam An-Nawawi (Study Edition)",
            author = "Imam Yahya ibn Sharaf An-Nawawi",
            language = "Arabic • English • Bengali",
            pagesOrDuration = "42 Core Hadiths",
            summaryEn = "Foundational prophetic narrations covering intention, Islam-Iman-Ihsan, lawful vs unlawful, sincerity, and good character.",
            keyTakeaways = listOf(
                "Hadith 1: Actions are judged by intentions.",
                "Hadith 2: Jibril's dialogue on Islam, Iman, and Ihsan.",
                "Hadith 18: Fear Allah and follow bad deeds with good."
            ),
        ),
        DigitalLibraryItem(
            id = "lib_2",
            category = "Study Guides",
            titleEn = "Tajweed Mastery: Practical Rules & Makharij Guide",
            author = "HIDAYAH Curriculum Team",
            language = "English • Bengali",
            pagesOrDuration = "6 Core Modules",
            summaryEn = "Structured visual guide covering articulation points, Noon/Meem Sakinah, Qalqalah, and Madd lengths with audio examples.",
            keyTakeaways = listOf(
                "5 main articulation areas (Jawf, Halq, Lisan, Shafatan, Khayshum).",
                "4 Noon Sakinah rules: Izhar, Idgham, Iqlab, Ikhfa."
            ),
        ),
        DigitalLibraryItem(
            id = "lib_3",
            category = "Islamic History",
            titleEn = "The Seerah of Prophet Muhammad (SAW): Makkah to Madinah",
            author = "Verified Classical Chronology",
            language = "English • Bengali • Urdu",
            pagesOrDuration = "Timeline & Lessons",
            summaryEn = "Key milestones from Cave Hira, Year of Sorrow, Al-Isra wal-Mi'raj, Hijrah, Charter of Madinah, and the Conquest of Makkah.",
            keyTakeaways = listOf(
                "Mercy and forgiveness at the Conquest of Makkah.",
                "Brotherhood of Muhajirun and Ansar in Madinah."
            ),
        ),
        DigitalLibraryItem(
            id = "lib_4",
            category = "Audio Lectures",
            titleEn = "Reflections on Surah Al-Fatihah & Daily Salah",
            author = "Audio Study Series",
            language = "English • Arabic",
            pagesOrDuration = "7 Verse Reflections",
            summaryEn = "Deepen focus (Khushu') in daily prayer by reflecting on each verse of Surah Al-Fatihah and what Allah responds to the believer.",
            keyTakeaways = listOf(
                "Al-Fatihah is a living dialogue between the servant and Allah (Sahih Muslim 395)."
            ),
        ),
    )

    val sunnahHabits: List<SunnahHabitTemplate> = listOf(
        SunnahHabitTemplate("tahajjud", "Tahajjud Night Prayer", "তাহাজ্জুদ নামাজ", "تهجد"),
        SunnahHabitTemplate("duha", "Salat ad-Duha", "চাশত / দুহা নামাজ", "الضحى"),
        SunnahHabitTemplate("morning_evening", "Morning & Evening Azkar", "সকাল-সন্ধ্যার যিকির", "الأذكار"),
        SunnahHabitTemplate("quran_page", "Read 2+ Pages of Quran", "দৈনিক কুরআন তিলাওয়াত", "القرآن"),
        SunnahHabitTemplate("sadaqah", "Daily Sadaqah / Kindness", "দৈনিক দান ও সদাচরণ", "صدقة"),
        SunnahHabitTemplate("fasting", "Sunnah / Voluntary Fast", "নফল রোযা", "صيام"),
    )
}
