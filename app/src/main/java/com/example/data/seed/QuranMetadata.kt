package com.example.data.seed

import com.example.data.local.CachedAyahEntity
import com.example.data.local.CachedTranslationEntity

data class SurahMeta(
    val number: Int,
    val arabicName: String,
    val englishName: String,
    val englishMeaning: String,
    val bengaliName: String,
    val ayahCount: Int,
    val revelationType: String, // "Meccan" or "Medinan"
    val startJuz: Int,
    val startingGlobalAyah: Int,
)

data class JuzMeta(
    val number: Int,
    val arabicName: String,
    val englishName: String,
    val startSurah: Int,
    val startAyah: Int,
    val endSurah: Int,
    val endAyah: Int,
)

object QuranMetadata {
    private val rawSurahs: List<SurahMeta> = buildList {
        val raw = listOf(
            listOf("1", "الفاتحة", "Al-Fatihah", "The Opening", "সূরা আল-ফাতিহা", "7", "Meccan", "1"),
            listOf("2", "البقرة", "Al-Baqarah", "The Cow", "সূরা আল-বাকারা", "286", "Medinan", "1"),
            listOf("3", "آل عمران", "Ali 'Imran", "Family of Imran", "সূরা আলে ইমরান", "200", "Medinan", "3"),
            listOf("4", "النساء", "An-Nisa", "The Women", "সূরা আন-নিসা", "176", "Medinan", "4"),
            listOf("5", "المائدة", "Al-Ma'idah", "The Table Spread", "সূরা আল-মায়িদা", "120", "Medinan", "6"),
            listOf("6", "الأنعام", "Al-An'am", "The Cattle", "সূরা আল-আনআম", "165", "Meccan", "7"),
            listOf("7", "الأعراف", "Al-A'raf", "The Heights", "সূরা আল-আরাফ", "206", "Meccan", "8"),
            listOf("8", "الأنفال", "Al-Anfal", "The Spoils of War", "সূরা আল-আনফাল", "75", "Medinan", "9"),
            listOf("9", "التوبة", "At-Tawbah", "The Repentance", "সূরা আত-তাওবা", "129", "Medinan", "10"),
            listOf("10", "يونس", "Yunus", "Jonah", "সূরা ইউনুস", "109", "Meccan", "11"),
            listOf("11", "هود", "Hud", "Hud", "সূরা হুদ", "123", "Meccan", "11"),
            listOf("12", "يوسف", "Yusuf", "Joseph", "সূরা ইউসুফ", "111", "Meccan", "12"),
            listOf("13", "الرعد", "Ar-Ra'd", "The Thunder", "সূরা আর-রাদ", "43", "Medinan", "13"),
            listOf("14", "إبراهيم", "Ibrahim", "Abraham", "সূরা ইবরাহিম", "52", "Meccan", "13"),
            listOf("15", "الحجر", "Al-Hijr", "The Rocky Tract", "সূরা আল-হিজর", "99", "Meccan", "14"),
            listOf("16", "النحل", "An-Nahl", "The Bee", "সূরা আন-নাহল", "128", "Meccan", "14"),
            listOf("17", "الإسراء", "Al-Isra", "The Night Journey", "সূরা বনী ইসরাঈল", "111", "Meccan", "15"),
            listOf("18", "الكهف", "Al-Kahf", "The Cave", "সূরা আল-কাহফ", "110", "Meccan", "15"),
            listOf("19", "مريم", "Maryam", "Mary", "সূরা মারইয়াম", "98", "Meccan", "16"),
            listOf("20", "طه", "Taha", "Ta-Ha", "সূরা ত্ব-হা", "135", "Meccan", "16"),
            listOf("21", "الأنبياء", "Al-Anbiya", "The Prophets", "সূরা আল-আম্বিয়া", "112", "Meccan", "17"),
            listOf("22", "الحج", "Al-Hajj", "The Pilgrimage", "সূরা আল-হাজ্জ", "78", "Medinan", "17"),
            listOf("23", "المؤمنون", "Al-Mu'minun", "The Believers", "সূরা আল-মুমিনুন", "118", "Meccan", "18"),
            listOf("24", "النور", "An-Nur", "The Light", "সূরা আন-নূর", "64", "Medinan", "18"),
            listOf("25", "الفرقان", "Al-Furqan", "The Criterion", "সূরা আল-ফুরকান", "77", "Meccan", "18"),
            listOf("26", "الشعراء", "Ash-Shu'ara", "The Poets", "সূরা আশ-শুআরা", "227", "Meccan", "19"),
            listOf("27", "النمل", "An-Naml", "The Ant", "সূরা আন-নামল", "93", "Meccan", "19"),
            listOf("28", "القصص", "Al-Qasas", "The Stories", "সূরা আল-কাসাস", "88", "Meccan", "20"),
            listOf("29", "العنكبوت", "Al-'Ankabut", "The Spider", "সূরা আল-আনকাবুত", "69", "Meccan", "20"),
            listOf("30", "الروم", "Ar-Rum", "The Romans", "সূরা আর-রূম", "60", "Meccan", "21"),
            listOf("31", "لقمان", "Luqman", "Luqman", "সূরা লুকমান", "34", "Meccan", "21"),
            listOf("32", "السجدة", "As-Sajdah", "The Prostration", "সূরা আস-সাজদা", "30", "Meccan", "21"),
            listOf("33", "الأحزاب", "Al-Ahzab", "The Combined Forces", "সূরা আল-আহযাব", "73", "Medinan", "21"),
            listOf("34", "سبإ", "Saba", "Sheba", "সূরা সাবা", "54", "Meccan", "22"),
            listOf("35", "فاطر", "Fatir", "Originator", "সূরা ফাতির", "45", "Meccan", "22"),
            listOf("36", "يس", "Ya-Sin", "Ya Sin", "সূরা ইয়াসিন", "83", "Meccan", "22"),
            listOf("37", "الصافات", "As-Saffat", "Those Who Set the Ranks", "সূরা আস-সাফফাত", "182", "Meccan", "23"),
            listOf("38", "ص", "Sad", "The Letter Sad", "সূরা ছোয়াদ", "88", "Meccan", "23"),
            listOf("39", "الزمر", "Az-Zumar", "The Troops", "সূরা আয-যুমার", "75", "Meccan", "23"),
            listOf("40", "غافر", "Ghafir", "The Forgiver", "সূরা আল-মু'মিন", "85", "Meccan", "24"),
            listOf("41", "فصلت", "Fussilat", "Explained in Detail", "সূরা হা-মীম সাজদা", "54", "Meccan", "24"),
            listOf("42", "الشورى", "Ash-Shuraa", "The Consultation", "সূরা আশ-শূরা", "53", "Meccan", "25"),
            listOf("43", "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", "সূরা আয-যুখরুফ", "89", "Meccan", "25"),
            listOf("44", "الدخان", "Ad-Dukhan", "The Smoke", "সূরা আদ-দুখান", "59", "Meccan", "25"),
            listOf("45", "الجاثية", "Al-Jathiyah", "The Crouching", "সূরা আল-জাসিয়া", "37", "Meccan", "25"),
            listOf("46", "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", "সূরা আল-আহকাফ", "35", "Meccan", "26"),
            listOf("47", "محمد", "Muhammad", "Muhammad", "সূরা মুহাম্মদ", "38", "Medinan", "26"),
            listOf("48", "الفتح", "Al-Fath", "The Victory", "সূরা আল-ফাতহ", "29", "Medinan", "26"),
            listOf("49", "الحجرات", "Al-Hujurat", "The Rooms", "সূরা আল-হুজুরাত", "18", "Medinan", "26"),
            listOf("50", "ق", "Qaf", "The Letter Qaf", "সূরা ক্বাফ", "45", "Meccan", "26"),
            listOf("51", "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", "সূরা আয-যারিয়াত", "60", "Meccan", "26"),
            listOf("52", "الطور", "At-Tur", "The Mount", "সূরা আত-তূর", "49", "Meccan", "27"),
            listOf("53", "النجم", "An-Najm", "The Star", "সূরা আন-নাজম", "62", "Meccan", "27"),
            listOf("54", "القمر", "Al-Qamar", "The Moon", "সূরা আল-ক্বামার", "55", "Meccan", "27"),
            listOf("55", "الرحمن", "Ar-Rahman", "The Beneficent", "সূরা আর-রহমান", "78", "Medinan", "27"),
            listOf("56", "الواقعة", "Al-Waqi'ah", "The Inevitable", "সূরা আল-ওয়াকিয়া", "96", "Meccan", "27"),
            listOf("57", "الحديد", "Al-Hadid", "The Iron", "সূরা আল-হাদীদ", "29", "Medinan", "27"),
            listOf("58", "المجادلة", "Al-Mujadila", "The Pleading Woman", "সূরা আল-মুজাদালা", "22", "Medinan", "28"),
            listOf("59", "الحشر", "Al-Hashr", "The Exile", "সূরা আল-হাশর", "24", "Medinan", "28"),
            listOf("60", "الممتحنة", "Al-Mumtahanah", "She That Is to Be Examined", "সূরা আল-মুমতাহিনা", "13", "Medinan", "28"),
            listOf("61", "الصف", "As-Saf", "The Ranks", "সূরা আস-সাফ", "14", "Medinan", "28"),
            listOf("62", "الجمعة", "Al-Jumu'ah", "The Congregation", "সূরা আল-জুমুআ", "11", "Medinan", "28"),
            listOf("63", "المنافقون", "Al-Munafiqun", "The Hypocrites", "সূরা আল-মুনাফিকুন", "11", "Medinan", "28"),
            listOf("64", "التغابن", "At-Taghabun", "The Mutual Disillusion", "সূরা আত-তাগাবুন", "18", "Medinan", "28"),
            listOf("65", "الطلاق", "At-Talaq", "The Divorce", "সূরা আত-তালাক", "12", "Medinan", "28"),
            listOf("66", "التحريم", "At-Tahrim", "The Prohibition", "সূরা আত-তাহরীম", "12", "Medinan", "28"),
            listOf("67", "الملك", "Al-Mulk", "The Sovereignty", "সূরা আল-মুলক", "30", "Meccan", "29"),
            listOf("68", "القلم", "Al-Qalam", "The Pen", "সূরা আল-ক্বালাম", "52", "Meccan", "29"),
            listOf("69", "الحاقة", "Al-Haqqah", "The Reality", "সূরা আল-হাক্কা", "52", "Meccan", "29"),
            listOf("70", "المعارج", "Al-Ma'arij", "The Ascending Stairways", "সূরা আল-মাআরিজ", "44", "Meccan", "29"),
            listOf("71", "نوح", "Nuh", "Noah", "সূরা নূহ", "28", "Meccan", "29"),
            listOf("72", "الجن", "Al-Jinn", "The Jinn", "সূরা আল-জ্বিন", "28", "Meccan", "29"),
            listOf("73", "المزمل", "Al-Muzzammil", "The Enshrouded One", "সূরা আল-মুযযাম্মিল", "20", "Meccan", "29"),
            listOf("74", "المدثر", "Al-Muddaththir", "The Cloaked One", "সূরা আল-মুদ্দাসসির", "56", "Meccan", "29"),
            listOf("75", "القيامة", "Al-Qiyamah", "The Resurrection", "সূরা আল-ক্বিয়ামাহ", "40", "Meccan", "29"),
            listOf("76", "الإنسان", "Al-Insan", "The Man", "সূরা আদ-দাহর", "31", "Medinan", "29"),
            listOf("77", "المرسلات", "Al-Mursalat", "The Emissaries", "সূরা আল-মুরসালাত", "50", "Meccan", "29"),
            listOf("78", "النبإ", "An-Naba", "The Tidings", "সূরা আন-নাবা", "40", "Meccan", "30"),
            listOf("79", "النازعات", "An-Nazi'at", "Those Who Drag Forth", "সূরা আন-নাযিয়াত", "46", "Meccan", "30"),
            listOf("80", "عبس", "'Abasa", "He Frowned", "সূরা আবাসা", "42", "Meccan", "30"),
            listOf("81", "التكوير", "At-Takwir", "The Overthrowing", "সূরা আত-তাকভীর", "29", "Meccan", "30"),
            listOf("82", "الانفطار", "Al-Infitar", "The Cleaving", "সূরা আল-ইনফিতার", "19", "Meccan", "30"),
            listOf("83", "المطففين", "Al-Mutaffifin", "The Defrauding", "সূরা আল-মুতাফফিফীন", "36", "Meccan", "30"),
            listOf("84", "الانشقاق", "Al-Inshiqaq", "The Sundering", "সূরা আল-ইনশিকাক", "25", "Meccan", "30"),
            listOf("85", "البروج", "Al-Buruj", "The Mansions of the Stars", "সূরা আল-বুরুজ", "22", "Meccan", "30"),
            listOf("86", "الطارق", "At-Tariq", "The Nightcommer", "সূরা আত-তারিক", "17", "Meccan", "30"),
            listOf("87", "الأعلى", "Al-A'la", "The Most High", "সূরা আল-আ'লা", "19", "Meccan", "30"),
            listOf("88", "الغاشية", "Al-Ghashiyah", "The Overwhelming", "সূরা আল-গাশিয়াহ", "26", "Meccan", "30"),
            listOf("89", "الفجر", "Al-Fajr", "The Dawn", "সূরা আল-ফজর", "30", "Meccan", "30"),
            listOf("90", "البلد", "Al-Balad", "The City", "সূরা আল-বালাদ", "20", "Meccan", "30"),
            listOf("91", "الشمس", "Ash-Shams", "The Sun", "সূরা আশ-শামস", "15", "Meccan", "30"),
            listOf("92", "الليل", "Al-Layl", "The Night", "সূরা আল-লাইল", "21", "Meccan", "30"),
            listOf("93", "الضحى", "Ad-Duhaa", "The Morning Hours", "সূরা আদ-দুহা", "11", "Meccan", "30"),
            listOf("94", "الشرح", "Ash-Sharh", "The Relief", "সূরা আল-ইনশিরাহ", "8", "Meccan", "30"),
            listOf("95", "التين", "At-Tin", "The Fig", "সূরা আত-তীন", "8", "Meccan", "30"),
            listOf("96", "العلق", "Al-'Alaq", "The Clot", "সূরা আল-আলাক", "19", "Meccan", "30"),
            listOf("97", "القدر", "Al-Qadr", "The Power", "সূরা আল-ক্বদর", "5", "Meccan", "30"),
            listOf("98", "البينة", "Al-Bayyinah", "The Clear Proof", "সূরা আল-বাইয়্যিনাহ", "8", "Medinan", "30"),
            listOf("99", "الزلزلة", "Az-Zalzalah", "The Earthquake", "সূরা আয-যিলযাল", "8", "Medinan", "30"),
            listOf("100", "العاديات", "Al-'Adiyat", "The Courser", "সূরা আল-আদিয়াত", "11", "Meccan", "30"),
            listOf("101", "القارعة", "Al-Qari'ah", "The Calamity", "সূরা আল-ক্বারিআহ", "11", "Meccan", "30"),
            listOf("102", "التكاثر", "At-Takathur", "The Rivalry in World Increase", "সূরা আত-তাকাসুর", "8", "Meccan", "30"),
            listOf("103", "العصر", "Al-'Asr", "The Declining Day", "সূরা আল-আসর", "3", "Meccan", "30"),
            listOf("104", "الهمزة", "Al-Humazah", "The Traducer", "সূরা আল-হুমাযাহ", "9", "Meccan", "30"),
            listOf("105", "الفيل", "Al-Fil", "The Elephant", "সূরা আল-ফীল", "5", "Meccan", "30"),
            listOf("106", "قريش", "Quraysh", "Quraysh", "সূরা কুরাইশ", "4", "Meccan", "30"),
            listOf("107", "الماعون", "Al-Ma'un", "The Small Kindnesses", "সূরা আল-মাঊন", "7", "Meccan", "30"),
            listOf("108", "الكوثر", "Al-Kawthar", "The Abundance", "সূরা আল-কাওসার", "3", "Meccan", "30"),
            listOf("109", "الكافرون", "Al-Kafirun", "The Disbelievers", "সূরা আল-কাফিরুন", "6", "Meccan", "30"),
            listOf("110", "النصر", "An-Nasr", "The Divine Support", "সূরা আন-নাসর", "3", "Medinan", "30"),
            listOf("111", "المسد", "Al-Masad", "The Palm Fiber", "সূরা আল-লাহাব", "5", "Meccan", "30"),
            listOf("112", "الإخلاص", "Al-Ikhlas", "The Sincerity", "সূরা আল-ইখলাস", "4", "Meccan", "30"),
            listOf("113", "الفلق", "Al-Falaq", "The Daybreak", "সূরা আল-ফালাক", "5", "Meccan", "30"),
            listOf("114", "الناس", "An-Nas", "Mankind", "সূরা আন-নাস", "6", "Meccan", "30"),
        )
        var globalCursor = 1
        for (row in raw) {
            val count = row[5].toInt()
            add(
                SurahMeta(
                    number = row[0].toInt(),
                    arabicName = row[1],
                    englishName = row[2],
                    englishMeaning = row[3],
                    bengaliName = row[4],
                    ayahCount = count,
                    revelationType = row[6],
                    startJuz = row[7].toInt(),
                    startingGlobalAyah = globalCursor,
                )
            )
            globalCursor += count
        }
    }

    val surahs: List<SurahMeta> = rawSurahs

    fun getSurah(number: Int): SurahMeta =
        surahs.firstOrNull { it.number == number } ?: surahs.first()

    fun globalAyahNumber(surahNumber: Int, ayahNumber: Int): Int {
        val meta = getSurah(surahNumber)
        return (meta.startingGlobalAyah + ayahNumber - 1).coerceIn(1, 6236)
    }

    val juzList: List<JuzMeta> = listOf(
        JuzMeta(1, "آلم", "Alif Lam Mim", 1, 1, 2, 141),
        JuzMeta(2, "سيقول", "Sayaqul", 2, 142, 2, 252),
        JuzMeta(3, "تلك الرسل", "Tilkal Rusul", 2, 253, 3, 92),
        JuzMeta(4, "لن تنالوا", "Lan Tana Lu", 3, 93, 4, 23),
        JuzMeta(5, "والمحصنات", "Wal Muhsanat", 4, 24, 4, 147),
        JuzMeta(6, "لا يحب الله", "La Yuhibbullah", 4, 148, 5, 81),
        JuzMeta(7, "وإذا سمعوا", "Wa Idha Sami'u", 5, 82, 6, 110),
        JuzMeta(8, "ولو أننا", "Wa Law Annana", 6, 111, 7, 87),
        JuzMeta(9, "قال الملأ", "Qalal Mala'u", 7, 88, 8, 40),
        JuzMeta(10, "واعلموا", "Wa'lamu", 8, 41, 9, 92),
        JuzMeta(11, "يعتذرون", "Ya'tadhirun", 9, 93, 11, 5),
        JuzMeta(12, "وما من دابة", "Wa Ma Min Dabbah", 11, 6, 12, 52),
        JuzMeta(13, "وما أبرئ", "Wa Ma Ubarri'u", 12, 53, 14, 52),
        JuzMeta(14, "ربما", "Rubama", 15, 1, 16, 128),
        JuzMeta(15, "سبحان الذي", "Subhanalladhi", 17, 1, 18, 74),
        JuzMeta(16, "قال ألم", "Qal Alam", 18, 75, 20, 135),
        JuzMeta(17, "اقترب للناس", "Iqtaraba", 21, 1, 22, 78),
        JuzMeta(18, "قد أفلح", "Qad Aflaha", 23, 1, 25, 20),
        JuzMeta(19, "وقال الذين", "Wa Qalalladhina", 25, 21, 27, 55),
        JuzMeta(20, "أمن خلق", "A'man Khalaqa", 27, 56, 29, 45),
        JuzMeta(21, "اتل ما أوحي", "Utlu Ma Uhiya", 29, 46, 33, 30),
        JuzMeta(22, "ومن يقنت", "Wa Man Yaqnut", 33, 31, 36, 27),
        JuzMeta(23, "وما لي", "Wa Mali", 36, 28, 39, 31),
        JuzMeta(24, "فمن أظلم", "Faman Azlamu", 39, 32, 41, 46),
        JuzMeta(25, "إليه يرد", "Ilayhi Yuraddu", 41, 47, 45, 37),
        JuzMeta(26, "حم", "Ha Mim", 46, 1, 51, 30),
        JuzMeta(27, "قال فما خطبكم", "Qala Fama Khatbukum", 51, 31, 57, 29),
        JuzMeta(28, "قد سمع الله", "Qad Sami'Allah", 58, 1, 66, 12),
        JuzMeta(29, "تبارك الذي", "Tabarakalladhi", 67, 1, 77, 50),
        JuzMeta(30, "عم يتساءلون", "Amma Yatasa'alun", 78, 1, 114, 6),
    )

    // Verified authentic starter ayahs for immediate offline reading (Surah 1, 103, 108, 112, 113, 114)
    val bundledAyahs: List<CachedAyahEntity> = listOf(
        // Surah 1: Al-Fatihah
        CachedAyahEntity(1, 1, 1, 1, 1, "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "بسم الله الرحمن الرحيم", "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "Bismillaahir Rahmaanir Raheem"),
        CachedAyahEntity(1, 2, 2, 1, 1, "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ", "الحمد لله رب العالمين", "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ", "Alhamdu lillaahi Rabbil 'aalameen"),
        CachedAyahEntity(1, 3, 3, 1, 1, "ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "الرحمن الرحيم", "ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "Ar-Rahmaanir-Raheem"),
        CachedAyahEntity(1, 4, 4, 1, 1, "مَٰلِكِ يَوْمِ ٱلدِّينِ", "مالك يوم الدين", "مَٰلِكِ يَوْمِ ٱلدِّينِ", "Maaliki Yawmid-Deen"),
        CachedAyahEntity(1, 5, 5, 1, 1, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "إياك نعبد وإياك نستعين", "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyaaka na'budu wa lyyaaka nasta'een"),
        CachedAyahEntity(1, 6, 6, 1, 1, "ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ", "اهدنا الصراط المستقيم", "ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ", "Ihdinas-Siraatal-Mustaqeem"),
        CachedAyahEntity(1, 7, 7, 1, 1, "صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ", "صراط الذين أنعمت عليهم غير المغضوب عليهم ولا الضالين", "صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ", "Siraatal-lazeena an'amta 'alaihim ghayril-maghdoobi 'alaihim wa lad-daaalleen"),

        // Surah 103: Al-Asr
        CachedAyahEntity(103, 1, 6177, 30, 601, "وَٱلْعَصْرِ", "والعصر", "وَٱلْعَصْرِ", "Wal-'asr"),
        CachedAyahEntity(103, 2, 6178, 30, 601, "إِنَّ ٱلْإِنسَٰنَ لَفِى خُسْرٍ", "إن الإنسان لفي خسر", "إِنَّ ٱلْإِنسَٰنَ لَفِى خُسْرٍ", "Innal-insaana lafee khusr"),
        CachedAyahEntity(103, 3, 6179, 30, 601, "إِلَّا ٱلَّذِينَ ءَامَنُوا۟ وَعَمِلُوا۟ ٱلصَّٰلِحَٰتِ وَتَوَاصَوْا۟ بِٱلْحَقِّ وَتَوَاصَوْا۟ بِٱلصَّبْرِ", "إلا الذين آمنوا وعملوا الصالحات وتواصوا بالحق وتواصوا بالصبر", "إِلَّا ٱلَّذِينَ ءَامَنُوا۟ وَعَمِلُوا۟ ٱلصَّٰلِحَٰتِ وَتَوَاصَوْا۟ بِٱلْحَقِّ وَتَوَاصَوْا۟ بِٱلصَّبْرِ", "Illal-lazeena aamanoo wa 'amilus-saalihaati wa tawaasaw bil-haqqi wa tawaasaw bis-sabr"),

        // Surah 108: Al-Kawthar
        CachedAyahEntity(108, 1, 6205, 30, 602, "إِنَّآ أَعْطَيْنَٰكَ ٱلْكَوْثَرَ", "إنا أعطيناك الكوثر", "إِنَّآ أَعْطَيْنَٰكَ ٱلْكَوْثَرَ", "Innaa a'tainaakal-kawthar"),
        CachedAyahEntity(108, 2, 6206, 30, 602, "فَصَلِّ لِرَبِّكَ وَٱنْحَرْ", "فصل لربك وانحر", "فَصَلِّ لِرَبِّكَ وَٱنْحَرْ", "Fa salli li-Rabbika wanhar"),
        CachedAyahEntity(108, 3, 6207, 30, 602, "إِنَّ شَانِئَكَ هُوَ ٱلْأَبْتَرُ", "إن شانئك هو الأبتر", "إِنَّ شَانِئَكَ هُوَ ٱلْأَبْتَرُ", "Inna shaani-aka huwal-abtar"),

        // Surah 112: Al-Ikhlas
        CachedAyahEntity(112, 1, 6222, 30, 604, "قُلْ هُوَ ٱللَّهُ أَحَدٌ", "قل هو الله أحد", "قُلْ هُوَ ٱللَّهُ أَحَدٌ", "Qul huwal-laahu ahad"),
        CachedAyahEntity(112, 2, 6223, 30, 604, "ٱللَّهُ ٱلصَّمَدُ", "الله الصمد", "ٱللَّهُ ٱلصَّمَدُ", "Allaahus-Samad"),
        CachedAyahEntity(112, 3, 6224, 30, 604, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "لم يلد ولم يولد", "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yoolad"),
        CachedAyahEntity(112, 4, 6225, 30, 604, "وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌۢ", "ولم يكن له كفوا أحد", "وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌۢ", "Wa lam yakul-lahoo kufuwan ahad"),

        // Surah 113: Al-Falaq
        CachedAyahEntity(113, 1, 6226, 30, 604, "قُلْ أَعُوذُ بِرَبِّ ٱلْفَلَقِ", "قل أعوذ برب الفلق", "قُلْ أَعُوذُ بِرَبِّ ٱلْفَلَقِ", "Qul a'oozu bi-Rabbil-falaq"),
        CachedAyahEntity(113, 2, 6227, 30, 604, "مِن شَرِّ مَا خَلَقَ", "من شر ما خلق", "مِن شَرِّ مَا خَلَقَ", "Min sharri maa khalaq"),
        CachedAyahEntity(113, 3, 6228, 30, 604, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "ومن شر غاسق إذا وقب", "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min sharri ghaasiqin izaa waqab"),
        CachedAyahEntity(113, 4, 6229, 30, 604, "وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِى ٱلْعُقَدِ", "ومن شر النفاثات في العقد", "وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِى ٱلْعُقَدِ", "Wa min sharrin-naffaathaati fil-'uqad"),
        CachedAyahEntity(113, 5, 6230, 30, 604, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "ومن شر حاسد إذا حسد", "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min sharri haasidin izaa hasad"),

        // Surah 114: An-Nas
        CachedAyahEntity(114, 1, 6231, 30, 604, "قُلْ أَعُوذُ بِرَبِّ ٱلنَّاسِ", "قل أعوذ برب الناس", "قُلْ أَعُوذُ بِرَبِّ ٱلنَّاسِ", "Qul a'oozu bi-Rabbin-naas"),
        CachedAyahEntity(114, 2, 6232, 30, 604, "مَلِكِ ٱلنَّاسِ", "ملك الناس", "مَلِكِ ٱلنَّاسِ", "Malikin-naas"),
        CachedAyahEntity(114, 3, 6233, 30, 604, "إِلَٰهِ ٱلنَّاسِ", "إله الناس", "إِلَٰهِ ٱلنَّاسِ", "Ilaahin-naas"),
        CachedAyahEntity(114, 4, 6234, 30, 604, "مِن شَرِّ ٱلْوَسْوَاسِ ٱلْخَنَّاسِ", "من شر الوسواس الخناس", "مِن شَرِّ ٱلْوَسْوَاسِ ٱلْخَنَّاسِ", "Min sharril-waswaasil-khannaas"),
        CachedAyahEntity(114, 5, 6235, 30, 604, "ٱلَّذِى يُوَسْوِسُ فِى صُدُورِ ٱلنَّاسِ", "الذي يوسوس في صدور الناس", "ٱلَّذِى يُوَسْوِسُ فِى صُدُورِ ٱلنَّاسِ", "Allazee yuwaswisu fee sudoorin-naas"),
        CachedAyahEntity(114, 6, 6236, 30, 604, "مِنَ ٱلْجِنَّةِ وَٱلنَّاسِ", "من الجنة والناس", "مِنَ ٱلْجِنَّةِ وَٱلنَّاسِ", "Minal-jinnati wan-naas"),
    )

    val bundledTranslations: List<CachedTranslationEntity> = listOf(
        // English Sahih
        CachedTranslationEntity(1, 1, "en.sahih", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
        CachedTranslationEntity(1, 2, "en.sahih", "[All] praise is [due] to Allah, Lord of the worlds -"),
        CachedTranslationEntity(1, 3, "en.sahih", "The Entirely Merciful, the Especially Merciful,"),
        CachedTranslationEntity(1, 4, "en.sahih", "Sovereign of the Day of Recompense."),
        CachedTranslationEntity(1, 5, "en.sahih", "It is You we worship and You we ask for help."),
        CachedTranslationEntity(1, 6, "en.sahih", "Guide us to the straight path -"),
        CachedTranslationEntity(1, 7, "en.sahih", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray."),

        CachedTranslationEntity(103, 1, "en.sahih", "By time,"),
        CachedTranslationEntity(103, 2, "en.sahih", "Indeed, mankind is in loss,"),
        CachedTranslationEntity(103, 3, "en.sahih", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience."),

        CachedTranslationEntity(108, 1, "en.sahih", "Indeed, We have granted you, [O Muhammad], al-Kawthar."),
        CachedTranslationEntity(108, 2, "en.sahih", "So pray to your Lord and sacrifice [to Him alone]."),
        CachedTranslationEntity(108, 3, "en.sahih", "Indeed, your enemy is the one cut off."),

        CachedTranslationEntity(112, 1, "en.sahih", "Say, \"He is Allah, [who is] One,"),
        CachedTranslationEntity(112, 2, "en.sahih", "Allah, the Eternal Refuge."),
        CachedTranslationEntity(112, 3, "en.sahih", "He neither begets nor is born,"),
        CachedTranslationEntity(112, 4, "en.sahih", "Nor is there to Him any equivalent.\""),

        CachedTranslationEntity(113, 1, "en.sahih", "Say, \"I seek refuge in the Lord of daybreak"),
        CachedTranslationEntity(113, 2, "en.sahih", "From the evil of that which He created"),
        CachedTranslationEntity(113, 3, "en.sahih", "And from the evil of darkness when it settles"),
        CachedTranslationEntity(113, 4, "en.sahih", "And from the evil of the blowers in knots"),
        CachedTranslationEntity(113, 5, "en.sahih", "And from the evil of an envier when he envies.\""),

        CachedTranslationEntity(114, 1, "en.sahih", "Say, \"I seek refuge in the Lord of mankind,"),
        CachedTranslationEntity(114, 2, "en.sahih", "The Sovereign of mankind,"),
        CachedTranslationEntity(114, 3, "en.sahih", "The God of mankind,"),
        CachedTranslationEntity(114, 4, "en.sahih", "From the evil of the retreating whisperer -"),
        CachedTranslationEntity(114, 5, "en.sahih", "Who whispers [evil] into the breasts of mankind -"),
        CachedTranslationEntity(114, 6, "en.sahih", "From among the jinn and mankind.\""),

        // Bengali
        CachedTranslationEntity(1, 1, "bn.bengali", "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অতি দয়ালু।"),
        CachedTranslationEntity(1, 2, "bn.bengali", "যাবতীয় প্রশংসা আল্লাহ তা’আলার যিনি সকল সৃষ্টি জগতের পালনকর্তা।"),
        CachedTranslationEntity(1, 3, "bn.bengali", "যিনি নিতান্ত মেহেরবান ও দয়ালু।"),
        CachedTranslationEntity(1, 4, "bn.bengali", "যিনি বিচার দিনের মালিক।"),
        CachedTranslationEntity(1, 5, "bn.bengali", "আমরা একমাত্র তোমারই ইবাদত করি এবং শুধুমাত্র তোমারই সাহায্য প্রার্থনা করি।"),
        CachedTranslationEntity(1, 6, "bn.bengali", "আমাদেরকে সরল পথ দেখাও,"),
        CachedTranslationEntity(1, 7, "bn.bengali", "সে সমস্ত লোকের পথ, যাদেরকে তুমি নেয়ামত দান করেছ। তাদের পথ নয়, যাদের প্রতি তোমার গজব নাযিল হয়েছে এবং যারা পথভ্রষ্ট হয়েছে।"),

        CachedTranslationEntity(112, 1, "bn.bengali", "বলুন, তিনি আল্লাহ, এক,"),
        CachedTranslationEntity(112, 2, "bn.bengali", "আল্লাহ অমুখাপেক্ষী,"),
        CachedTranslationEntity(112, 3, "bn.bengali", "তিনি কাউকে জন্ম দেননি এবং কেউ তাকে জন্ম দেয়নি"),
        CachedTranslationEntity(112, 4, "bn.bengali", "এবং তাঁর সমতুল্য কেউ নেই।"),
    )

    val translationEditions: List<Pair<String, String>> = listOf(
        "en.sahih" to "English (Sahih International)",
        "bn.bengali" to "Bengali (Muhiuddin Khan)",
        "ur.jalandhry" to "Urdu (Fateh Muhammad Jalandhry)",
        "hi.hindi" to "Hindi (Suhel Farooq Khan)",
        "ar.muyassar" to "Arabic (Tafsir Al-Muyassar)",
        "id.indonesian" to "Indonesian (Ministry of Religious Affairs)",
        "tr.diyanet" to "Turkish (Diyanet Isleri)",
        "fr.hamidullah" to "French (Muhammad Hamidullah)",
        "ms.basmeih" to "Malay (Abdullah Muhammad Basmeih)",
    )
}
