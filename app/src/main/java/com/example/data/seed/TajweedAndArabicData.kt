package com.example.data.seed

data class TajweedLesson(
    val id: String,
    val level: String, // "Beginner", "Intermediate", "Advanced"
    val titleEn: String,
    val titleBn: String,
    val arabicTitle: String,
    val colorHex: Long,
    val summaryEn: String,
    val summaryBn: String,
    val ruleDetailsEn: List<String>,
    val ruleDetailsBn: List<String>,
    val exampleArabic: String,
    val exampleReference: String,
    val exampleAudioGlobalAyah: Int,
)

data class TajweedQuizQuestion(
    val id: String,
    val questionEn: String,
    val questionBn: String,
    val arabicSample: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
)

data class ArabicAlphabetLetter(
    val order: Int,
    val isolated: String,
    val initial: String,
    val medial: String,
    val finalForm: String,
    val nameEn: String,
    val nameBn: String,
    val pronunciation: String,
    val makhraj: String,
    val exampleWord: String,
    val exampleMeaning: String,
)

data class ArabicLesson(
    val id: String,
    val level: String, // "Alphabet & Harakat", "Reading & Joining", "Vocabulary & Conversation", "Grammar (Nahw & Sarf)", "Quranic Arabic"
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val items: List<ArabicLessonItem>,
)

data class ArabicLessonItem(
    val arabic: String,
    val transliteration: String,
    val meaningEn: String,
    val meaningBn: String,
    val noteEn: String = "",
)

object TajweedAndArabicData {
    val tajweedLessons: List<TajweedLesson> = listOf(
        TajweedLesson(
            id = "makharij",
            level = "Beginner",
            titleEn = "Makharijul Huruf (Articulation Points)",
            titleBn = "মাখারিজুল হুরুফ (উচ্চারণ স্থান)",
            arabicTitle = "مخارج الحروف",
            colorHex = 0xFF0B3B2E,
            summaryEn = "Every Arabic letter originates from one of 5 major areas: Al-Jawf (oral cavity), Al-Halq (throat), Al-Lisan (tongue), Ash-Shafatan (lips), and Al-Khayshum (nasal passage).",
            summaryBn = "আরবি ২৯টি হরফ মূলত ৫টি প্রধান স্থান থেকে উচ্চারিত হয়: আল-জাওফ (মুখের খালি জায়গা), আল-হালক (কণ্ঠনালী), আল-লিসান (জিহ্বা), আশ-শাফাতান (দুই ঠোঁট) ও আল-খাইশূম (নাসিকা)।",
            ruleDetailsEn = listOf(
                "Al-Halq (Throat): ء هـ (bottom), ع ح (middle), غ خ (top).",
                "Ash-Shafatan (Lips): ف (upper teeth on lower lip), ب م و (lips).",
                "Al-Lisan (Tongue): 18 letters across root, middle, sides, and tip.",
                "Al-Khayshum (Nasal): Produces Ghunnah on Noon and Meem."
            ),
            ruleDetailsBn = listOf(
                "আল-হালক (কণ্ঠনালী): ء هـ (শুরু), ع ح (মাঝখান), غ خ (উপর)।",
                "আশ-শাফাতান (ঠোঁট): ف (উপরের দাঁত ও নিচের ঠোঁট), ب م و (দুই ঠোঁট)।",
                "জিহ্বা থেকে ১৮টি হরফ স্পষ্টভাবে উচ্চারিত হয়।"
            ),
            exampleArabic = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ",
            exampleReference = "Al-Fatihah 1:2",
            exampleAudioGlobalAyah = 2,
        ),
        TajweedLesson(
            id = "sifat",
            level = "Intermediate",
            titleEn = "Sifatul Huruf (Characteristics of Letters)",
            titleBn = "সিফাতুল হুরুফ (হরফের গুণাবলী)",
            arabicTitle = "صفات الحروف",
            colorHex = 0xFF1F7A8C,
            summaryEn = "Distinctive acoustic traits such as Hams (breathiness), Jahr (voicing), Tafkhim (heavy elevation: خ ص ض غ ط ق ظ), and Tarqiq (lightness).",
            summaryBn = "হরফ উচ্চারণের সময় ধ্বনির বৈশিষ্ট্য যেমন: হাম্মস, জহর, তাফখীম (মোটা উচ্চারণ: خ ص ض غ ط ق ظ) ও তারকীক (পাতলা উচ্চারণ)।",
            ruleDetailsEn = listOf(
                "Isti'la (Heavy Letters): خ ص ض غ ط ق ظ — raise the back of the tongue.",
                "Safir (Whistling): ص ز س — clear sibilant resonance.",
                "Tafashshi (Spreading breath): ش — air spreads across the palate."
            ),
            ruleDetailsBn = listOf(
                "ইসতি’লা বা মোটা ৭টি হরফ: خ ص ض غ ط ق ظ সর্বদা মোটা করে পড়তে হয়।",
                "সাফীর হরফ: ص ز س উচ্চারণে শিসধ্বনি থাকে।"
            ),
            exampleArabic = "صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ",
            exampleReference = "Al-Fatihah 1:7",
            exampleAudioGlobalAyah = 7,
        ),
        TajweedLesson(
            id = "noon_sakinah",
            level = "Beginner",
            titleEn = "Noon Sakinah & Tanween (Izhar, Idgham, Iqlab, Ikhfa)",
            titleBn = "নূন সাকিন ও তানবীন (ইযহার, ইদগাম, ইকলাব, ইখফা)",
            arabicTitle = "أحكام النون الساكنة والتنوين",
            colorHex = 0xFFD97706,
            summaryEn = "Four essential rules when a vowelless Noon (نْ) or Tanween (ـً ـٍ ـٌ) meets the following letter.",
            summaryBn = "নূন সাকিন (نْ) বা তানবীন (ـً ـٍ ـٌ)-এর পর আগত হরফ অনুযায়ী চারটি প্রধান বিধান প্রযোজ্য হয়।",
            ruleDetailsEn = listOf(
                "Izhar (Clear): Before throat letters ء هـ ع ح غ خ — pronounce clearly without extra nasal hold.",
                "Idgham (Merge): Before ي ر م ل و ن (Yarmaloon) — merge with Ghunnah on ي ن م و, without Ghunnah on ل ر.",
                "Iqlab (Convert): Before ب — convert Noon sound into a hidden Meem with Ghunnah.",
                "Ikhfa (Hide): Before the remaining 15 letters — conceal the Noon nasally for 2 harakat."
            ),
            ruleDetailsBn = listOf(
                "ইযহার (স্পষ্ট): হরফে হালকী ৬টি (ء هـ ع ح غ خ) আসলে স্পষ্ট করে পড়তে হয়।",
                "ইদগাম (মিলানো): ي ر م ل و ن আসলে মিলিয়ে পড়তে হয়।",
                "ইকলাব (পরিবর্তন): ب আসলে নূনকে মীম-এ রূপান্তর করে গুন্নাহসহ পড়তে হয়।",
                "ইখফা (গোপন): বাকি ১৫টি হরফ আসলে নাসিকায় লুকিয়ে ২ আলিফ গুন্নাহ করতে হয়।"
            ),
            exampleArabic = "مِن شَرِّ مَا خَلَقَ",
            exampleReference = "Al-Falaq 113:2",
            exampleAudioGlobalAyah = 6227,
        ),
        TajweedLesson(
            id = "meem_sakinah",
            level = "Intermediate",
            titleEn = "Meem Sakinah Rules",
            titleBn = "মীম সাকিনের বিধান",
            arabicTitle = "أحكام الميم الساكنة",
            colorHex = 0xFF2563EB,
            summaryEn = "Three rules for vowelless Meem (مْ): Ikhfa Shafawi before ب, Idgham Shafawi before م, and Izhar Shafawi before all other 26 letters.",
            summaryBn = "মীম সাকিনের (مْ) তিনটি বিধান: ইখফায়ে শাফাবী (ب), ইদগামে শাফাবী (م) এবং ইযহারে শাফাবী (বাকি ২৬ হরফ)।",
            ruleDetailsEn = listOf(
                "Ikhfa Shafawi: مْ followed by ب — light lip contact with 2-beat Ghunnah.",
                "Idgham Shafawi: مْ followed by م — merge into one Meem Mushaddadah with Ghunnah.",
                "Izhar Shafawi: مْ followed by any other letter — pronounce clearly, especially before و and ف."
            ),
            ruleDetailsBn = listOf(
                "ইখফায়ে শাফাবী: مْ-এর পর ب আসলে গুন্নাহসহ গোপন করে পড়া।",
                "ইদগামে শাফাবী: مْ-এর পর م আসলে মিলিয়ে গুন্নাহ করা।"
            ),
            exampleArabic = "تَرْمِيهِم بِحِجَارَةٍ مِّن سِجِّيلٍ",
            exampleReference = "Al-Fil 105:4",
            exampleAudioGlobalAyah = 6192,
        ),
        TajweedLesson(
            id = "qalqalah_ghunnah",
            level = "Beginner",
            titleEn = "Qalqalah & Ghunnah",
            titleBn = "কলকলাহ ও গুন্নাহ",
            arabicTitle = "القلقلة والغنة",
            colorHex = 0xFFDC2626,
            summaryEn = "Qalqalah is the echoing bounce on ق ط ب ج د (Qutb Jad) when carrying Sukun; Ghunnah is the 2-count nasal hum on نّ and مّ.",
            summaryBn = "কলকলাহর ৫টি হরফ (ق ط ب ج د)-এ সাকিন হলে প্রতিধ্বনি হয়। আর নূন ও মীমে তাশদীদ (نّ / مّ) থাকলে ওয়াজিব গুন্নাহ হয়।",
            ruleDetailsEn = listOf(
                "Qalqalah Sughra (Minor): Inside a word (e.g., يَدْخُلُونَ).",
                "Qalqalah Kubra (Major): At the end of a verse when stopping (e.g., أَحَدٌ).",
                "Ghunnah: Hold nasal resonance for 2 counts on نّ and مّ."
            ),
            ruleDetailsBn = listOf(
                "কলকলাহ সুগরা: শব্দের মাঝে সাকিন হলে।",
                "কলকলাহ কুবরা: আয়াতের শেষে ওয়াকফ করলে।",
                "গুন্নাহ: نّ এবং مّ-এ নাকের বাঁশি থেকে ২ হরকত পরিমাণ ধ্বনি।"
            ),
            exampleArabic = "قُلْ هُوَ ٱللَّهُ أَحَدٌ",
            exampleReference = "Al-Ikhlas 112:1",
            exampleAudioGlobalAyah = 6222,
        ),
        TajweedLesson(
            id = "madd_rules",
            level = "Advanced",
            titleEn = "Ahkam al-Madd (Elongation Rules)",
            titleBn = "মাদ্দ বা টেনে পড়ার নিয়মাবলী",
            arabicTitle = "أحكام المد",
            colorHex = 0xFF7C3AED,
            summaryEn = "Elongation occurs with Alif after Fathah, Waw Sakinah after Dammah, and Ya Sakinah after Kasrah — ranging from 2 to 6 harakat.",
            summaryBn = "মাদ্দের হরফ ৩টি: ا ও ى ও و। স্বাভাবিক মাদ্দ ২ আলিফ, আর হামযাহ বা সাকিনের কারণে ৪ থেকে ৬ আলিফ পর্যন্ত টেনে পড়া হয়।",
            ruleDetailsEn = listOf(
                "Madd Tabi'i (Natural): 2 counts (e.g., قَالَ).",
                "Madd Muttasil (Connected): Hamzah in the same word — 4 to 5 counts.",
                "Madd Munfasil (Separated): Hamzah in the next word — 4 to 5 counts.",
                "Madd Lazim (Compulsory): Permanent Sukun or Shaddah after Madd letter — 6 counts (e.g., ٱلضَّآلِّينَ)."
            ),
            ruleDetailsBn = listOf(
                "মাদ্দে তবঈ: ২ হরকত টান (যেমন: قَالَ)।",
                "মাদ্দে মুত্তাসিল ও মুনফাসিল: ৪–৫ আলিফ টান।",
                "মাদ্দে লাযিম: ৬ আলিফ পরিমাণ টেনে পড়া আবশ্যক (যেমন: ٱلضَّآلِّينَ)।"
            ),
            exampleArabic = "وَلَا ٱلضَّآلِّينَ",
            exampleReference = "Al-Fatihah 1:7",
            exampleAudioGlobalAyah = 7,
        ),
    )

    val tajweedQuiz: List<TajweedQuizQuestion> = listOf(
        TajweedQuizQuestion(
            id = "tq1",
            questionEn = "Which five letters form the Qalqalah (echoing bounce) group?",
            questionBn = "কলকলাহর ৫টি হরফ কোনগুলো?",
            arabicSample = "قُطْبُ جَدٍّ",
            options = listOf("ق ط ب ج د", "ي ر م ل و ن", "خ ص ض غ ط", "ء هـ ع ح غ خ"),
            correctIndex = 0,
            explanationEn = "Qalqalah applies to ق ط ب ج د (Qutbu Jad) whenever they carry a Sukun or are stopped upon."
        ),
        TajweedQuizQuestion(
            id = "tq2",
            questionEn = "When Noon Sakinah (نْ) is followed by ب, which rule applies?",
            questionBn = "নূন সাকিনের পর ب আসলে কোন নিয়ম প্রযোজ্য হয়?",
            arabicSample = "مِنۢ بَعْدِ",
            options = listOf("Izhar", "Iqlab", "Qalqalah", "Madd Lazim"),
            correctIndex = 1,
            explanationEn = "Iqlab converts Noon Sakinah or Tanween before ب into a concealed Meem sound with Ghunnah."
        ),
        TajweedQuizQuestion(
            id = "tq3",
            questionEn = "How many counts is Madd Lazim (e.g. ٱلضَّآلِّينَ) elongated?",
            questionBn = "মাদ্দে লাযিম কত হরকত টেনে পড়তে হয়?",
            arabicSample = "وَلَا ٱلضَّآلِّينَ",
            options = listOf("1 count", "2 counts", "4 counts", "6 counts"),
            correctIndex = 3,
            explanationEn = "Madd Lazim is a compulsory elongation held for 6 harakat."
        ),
        TajweedQuizQuestion(
            id = "tq4",
            questionEn = "What are the 6 throat letters (Huruf al-Halq) for Izhar?",
            questionBn = "ইযহারের ৬টি হরফে হালকী কোনগুলো?",
            arabicSample = "مَنْ آمَنَ",
            options = listOf("ء هـ ع ح غ خ", "ب ت ث ج ح", "ي ن م و", "ص ض ط ظ"),
            correctIndex = 0,
            explanationEn = "The 6 throat letters (ء هـ ع ح غ خ) trigger clear pronunciation (Izhar)."
        ),
    )

    val arabicAlphabet: List<ArabicAlphabetLetter> = listOf(
        ArabicAlphabetLetter(1, "ا", "ا", "ـا", "ـا", "Alif", "আলিফ", "ā / a", "Al-Jawf (Oral cavity)", "أَحَد", "One"),
        ArabicAlphabetLetter(2, "ب", "بـ", "ـبـ", "ـب", "Ba", "বা", "b", "Both lips", "بَيْت", "House"),
        ArabicAlphabetLetter(3, "ت", "تـ", "ـتـ", "ـت", "Ta", "তা", "t", "Tongue tip & upper incisors", "تَوْبَة", "Repentance"),
        ArabicAlphabetLetter(4, "ث", "ثـ", "ـثـ", "ـث", "Tha", "ছা", "th (soft)", "Tongue tip & tooth edge", "ثَوَاب", "Reward"),
        ArabicAlphabetLetter(5, "ج", "جـ", "ـجـ", "ـج", "Jim", "জীম", "j", "Middle of tongue & palate", "جَنَّة", "Paradise"),
        ArabicAlphabetLetter(6, "ح", "حـ", "ـحـ", "ـح", "Ha", "হা", "ḥ (crisp throat)", "Middle throat", "حَمْد", "Praise"),
        ArabicAlphabetLetter(7, "خ", "خـ", "ـخـ", "ـخ", "Kha", "খা", "kh (heavy)", "Top throat", "خَيْر", "Goodness"),
        ArabicAlphabetLetter(8, "د", "د", "ـد", "ـد", "Dal", "দাল", "d", "Tongue tip & upper gum", "دِين", "Religion"),
        ArabicAlphabetLetter(9, "ذ", "ذ", "ـذ", "ـذ", "Dhal", "যাল", "dh (soft)", "Tongue tip & tooth edge", "ذِكْر", "Remembrance"),
        ArabicAlphabetLetter(10, "ر", "ر", "ـر", "ـر", "Ra", "রা", "r", "Tongue tip & palate ridge", "رَحْمَة", "Mercy"),
        ArabicAlphabetLetter(11, "ز", "ز", "ـز", "ـز", "Zay", "যা", "z", "Tongue tip & lower incisors", "زَكَاة", "Zakat"),
        ArabicAlphabetLetter(12, "س", "سـ", "ـسـ", "ـس", "Sin", "সীন", "s", "Tongue tip & lower incisors", "سَلَام", "Peace"),
        ArabicAlphabetLetter(13, "ش", "شـ", "ـشـ", "ـش", "Shin", "শীন", "sh", "Middle of tongue", "شُكْر", "Gratitude"),
        ArabicAlphabetLetter(14, "ص", "صـ", "ـصـ", "ـص", "Sad", "ছোয়াদ", "ṣ (heavy s)", "Tongue tip (elevated)", "صَلَاة", "Prayer"),
        ArabicAlphabetLetter(15, "ض", "ضـ", "ـضـ", "ـض", "Dad", "দোয়াদ", "ḍ (heavy d)", "Side of tongue & molars", "ضِيَاء", "Radiance"),
        ArabicAlphabetLetter(16, "ط", "طـ", "ـطـ", "ـط", "Ta (heavy)", "ত্বা", "ṭ (heavy t)", "Tongue tip (elevated)", "طَهُور", "Pure"),
        ArabicAlphabetLetter(17, "ظ", "ظـ", "ـظـ", "ـظ", "Za (heavy)", "যোয়া", "ẓ (heavy dh)", "Tongue tip (elevated)", "ظِلّ", "Shade"),
        ArabicAlphabetLetter(18, "ع", "عـ", "ـعـ", "ـع", "'Ayn", "আইন", "'a (deep throat)", "Middle throat", "عِلْم", "Knowledge"),
        ArabicAlphabetLetter(19, "غ", "غـ", "ـغـ", "ـغ", "Ghayn", "গাইন", "gh", "Top throat", "غَفُور", "Forgiving"),
        ArabicAlphabetLetter(20, "ف", "فـ", "ـفـ", "ـف", "Fa", "ফা", "f", "Upper teeth & lower lip", "فَجْر", "Dawn"),
        ArabicAlphabetLetter(21, "ق", "قـ", "ـقـ", "ـق", "Qaf", "ক্বাফ", "q (deep uvular)", "Deepest tongue & soft palate", "قُرْآن", "Quran"),
        ArabicAlphabetLetter(22, "ك", "كـ", "ـكـ", "ـك", "Kaf", "কাফ", "k", "Back tongue & palate", "كِتَاب", "Book"),
        ArabicAlphabetLetter(23, "ل", "لـ", "ـلـ", "ـل", "Lam", "লাম", "l", "Front tongue edge", "لَيْل", "Night"),
        ArabicAlphabetLetter(24, "م", "مـ", "ـمـ", "ـم", "Meem", "মীম", "m", "Both lips", "مَسْجِد", "Mosque"),
        ArabicAlphabetLetter(25, "ن", "نـ", "ـنـ", "ـن", "Noon", "নূন", "n", "Tongue tip & gum", "نُور", "Light"),
        ArabicAlphabetLetter(26, "هـ", "هـ", "ـهـ", "ـه", "Ha (soft)", "হা", "h", "Bottom throat", "هِدَايَة", "Guidance"),
        ArabicAlphabetLetter(27, "و", "و", "ـو", "ـو", "Waw", "ওয়াও", "w / ū", "Rounded lips", "وَحْي", "Revelation"),
        ArabicAlphabetLetter(28, "ي", "يـ", "ـيـ", "ـي", "Ya", "ইয়া", "y / ī", "Middle of tongue", "يَوْم", "Day"),
    )

    val arabicLessons: List<ArabicLesson> = listOf(
        ArabicLesson(
            id = "harakat",
            level = "Alphabet & Harakat",
            titleEn = "Harakat (Short Vowels, Sukun & Shaddah)",
            titleBn = "হরকত (যবর, যের, পেশ, সুকূন ও তাশদীদ)",
            descriptionEn = "Master Arabic vowel signs that give every consonant its sound.",
            descriptionBn = "যবর (ফাতহা), যের (কাসরা), পেশ (দম্মাহ), তানবীন ও তাশদীদ চিনে সহজে আরবি পড়ুন।",
            items = listOf(
                ArabicLessonItem("بَ", "Ba", "Fathah (a sound)", "ফাতহা / যবর (আ-কার)", "Short 'a' above the letter"),
                ArabicLessonItem("بِ", "Bi", "Kasrah (i sound)", "কাসরা / যের (ই-কার)", "Short 'i' below the letter"),
                ArabicLessonItem("بُ", "Bu", "Dammah (u sound)", "দম্মাহ / পেশ (উ-কার)", "Short 'u' above the letter"),
                ArabicLessonItem("بً / بٍ / بٌ", "Ban / Bin / Bun", "Tanween (Double vowels)", "তানবীন (দুই যবর/যের/পেশ)", "Adds an 'n' sound at word end"),
                ArabicLessonItem("أَبْ", "Ab", "Sukun (Resting stop)", "সুকূন / জযম", "Joins previous vowel without a new vowel"),
                ArabicLessonItem("رَبِّ", "Rabbi", "Shaddah (Doubled letter)", "তাশদীদ (দ্বিত্ব উচ্চারণ)", "Doubles and stresses the consonant"),
            ),
        ),
        ArabicLesson(
            id = "joining",
            level = "Reading & Joining",
            titleEn = "Joining Arabic Letters & Non-Connectors",
            titleBn = "আরবি হরফ যুক্ত করার নিয়ম",
            descriptionEn = "Learn how letters transform in Initial, Medial, and Final positions, and the 6 right-only connectors (ا د ذ ر ز و).",
            descriptionBn = "শব্দের শুরুতে, মাঝে ও শেষে হরফের রূপ পরিবর্তন এবং ৬টি অযুক্ত হরফ (ا د ذ ر ز و) চিনুন।",
            items = listOf(
                ArabicLessonItem("ك + ت + ب = كَتَبَ", "Kataba", "He wrote", "সে লিখেছে", "All three letters connect smoothly"),
                ArabicLessonItem("ق + ل + م = قَلَم", "Qalam", "Pen", "কলম", "Initial Qaf, medial Lam, final Meem"),
                ArabicLessonItem("ر + ح + م = رَحِمَ", "Rahima", "He had mercy", "তিনি দয়া করেছেন", "Ra does not connect to the left"),
                ArabicLessonItem("ن + و + ر = نُور", "Noor", "Light", "আলো / নূর", "Waw connects from the right only"),
            ),
        ),
        ArabicLesson(
            id = "daily_conv",
            level = "Vocabulary & Conversation",
            titleEn = "Daily Arabic Conversations & Greetings",
            titleBn = "দৈনন্দিন আরবি কথোপকথন ও অভিবাদন",
            descriptionEn = "Practical phrases used in daily Islamic greetings, travel, and study.",
            descriptionBn = "দৈনন্দিন জীবনে ব্যবহৃত প্রয়োজনীয় আরবি বাক্য ও শব্দাবলী।",
            items = listOf(
                ArabicLessonItem("ٱلسَّلَامُ عَلَيْكُمْ", "As-salamu alaykum", "Peace be upon you", "আপনার উপর শান্তি বর্ষিত হোক"),
                ArabicLessonItem("وَعَلَيْكُمُ ٱلسَّلَامُ", "Wa alaykumus-salam", "And upon you be peace", "আপনার উপরও শান্তি বর্ষিত হোক"),
                ArabicLessonItem("جَزَاكَ ٱللَّهُ خَيْرًا", "JazakAllahu khayran", "May Allah reward you with good", "আল্লাহ আপনাকে উত্তম প্রতিদান দিন"),
                ArabicLessonItem("كَيْفَ حَالُكَ؟", "Kayfa haluka?", "How are you?", "আপনি কেমন আছেন?"),
                ArabicLessonItem("أَنَا بِخَيْرٍ، ٱلْحَمْدُ لِلَّهِ", "Ana bi-khayrin, Alhamdulillah", "I am well, praise be to Allah", "আমি ভালো আছি, আলহামদুলিল্লাহ"),
                ArabicLessonItem("بِسْمِ ٱللَّهِ", "Bismillah", "In the name of Allah", "আল্লাহর নামে শুরু"),
            ),
        ),
        ArabicLesson(
            id = "nahw_sarf",
            level = "Grammar (Nahw & Sarf)",
            titleEn = "Nahw (Syntax) & Sarf (Morphology) Essentials",
            titleBn = "নাহু (বাক্যতত্ত্ব) ও ছরফ (শব্দতত্ত্ব)-এর মূলনীতি",
            descriptionEn = "Understand Ism (Noun), Fi'l (Verb), Harf (Particle), and the 3-letter Arabic root system.",
            descriptionBn = "ইসম (বিশেষ্য), ফে'ল (ক্রিয়া), হরফ (অব্যয়) এবং তিন অক্ষরের আরবি মূল ধাতু (মাসদার) শিখুন।",
            items = listOf(
                ArabicLessonItem("ٱلِاسْمُ (Ism)", "Noun / Adjective", "Names a person, place, or attribute (e.g., كِتَاب)", "বিশেষ্য বা গুণবাচক শব্দ (যেমন: কিতাব)"),
                ArabicLessonItem("ٱلْفِعْلُ (Fi'l)", "Verb", "Past (فَعَلَ), Present (يَفْعَلُ), Command (اِفْعَلْ)", "ক্রিয়া: অতীত, বর্তমান ও আদেশ সূচক"),
                ArabicLessonItem("ٱلْحَرْفُ (Harf)", "Particle", "Prepositions like فِي (in), عَلَى (on), مِنْ (from)", "অব্যয় যেমন: فِي (মধ্যে), مِنْ (হতে)"),
                ArabicLessonItem("جَذْر ك-ت-ب", "Root K-T-B (Write)", "كِتَاب (Book), كَاتِب (Writer), مَكْتَب (Desk)", "একই মূল ধাতু থেকে বহু অর্থবহ শব্দ গঠন"),
                ArabicLessonItem("مَرْفُوع / مَنْصُوب / مَجْرُور", "Case Endings (I'rab)", "Subject (-u), Object (-a), Prepositional (-i)", "বাক্যে শব্দের অবস্থানের কারণে শেষ হরকতের পরিবর্তন"),
            ),
        ),
        ArabicLesson(
            id = "quranic_vocab",
            level = "Quranic Arabic",
            titleEn = "High-Frequency Quranic Vocabulary",
            titleBn = "কুরআনে সর্বাধিক ব্যবহৃত শব্দসমূহ",
            descriptionEn = "Core words that appear hundreds of times throughout the Noble Quran.",
            descriptionBn = "কুরআন মাজীদ বুঝতে সহায়ক সর্বাধিক পুনরাবৃত্ত আরবি শব্দ।",
            items = listOf(
                ArabicLessonItem("ٱللَّهُ", "Allah", "The One True God (2,699x)", "মহান আল্লাহ"),
                ArabicLessonItem("رَبّ", "Rabb", "Lord, Sustainer, Cherisher (975x)", "প্রতিপালক ও রব"),
                ArabicLessonItem("عِلْم", "'Ilm", "Knowledge (854x root)", "জ্ঞান"),
                ArabicLessonItem("هُدًى", "Hudan", "Guidance (316x root)", "হেদায়াত বা সঠিক পথ"),
                ArabicLessonItem("رَحْمَة", "Rahmah", "Mercy & Compassion (339x root)", "দয়া ও রহমত"),
                ArabicLessonItem("قَلْب / قُلُوب", "Qalb / Qulub", "Heart / Hearts (132x)", "অন্তর"),
                ArabicLessonItem("صَبْر", "Sabr", "Steadfast Patience (103x)", "ধৈর্য"),
                ArabicLessonItem("شُكْر", "Shukr", "Thankfulness & Gratitude (75x)", "কৃতজ্ঞতা"),
            ),
        ),
    )
}
