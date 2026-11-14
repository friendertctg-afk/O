package com.example.data.seed

data class QariInfo(
    val id: String,
    val name: String,
    val arabicName: String,
    val style: String,
    val country: String,
    val bitrateKbps: Int = 128,
) {
    fun ayahAudioUrl(globalAyahNumber: Int): String =
        "https://cdn.islamic.network/quran/audio/$bitrateKbps/$id/$globalAyahNumber.mp3"
}

object QariCatalog {
    val qaris: List<QariInfo> = listOf(
        QariInfo("ar.alafasy", "Mishary Rashid Alafasy", "مشاري راشد العفاسي", "Murattal • Hafs", "Kuwait"),
        QariInfo("ar.abdulbasitmurattal", "Abdul Basit 'Abdus Samad", "عبد الباسط عبد الصمد", "Murattal • Hafs", "Egypt"),
        QariInfo("ar.abdulsamad", "Abdul Basit (Mujawwad)", "عبد الباسط عبد الصمد (مجود)", "Mujawwad • Classic", "Egypt"),
        QariInfo("ar.husary", "Mahmoud Khalil Al-Husary", "محمود خليل الحصري", "Murattal • Tajweed", "Egypt"),
        QariInfo("ar.husarymujawwad", "Al-Husary (Mujawwad)", "الحصري (مجود)", "Mujawwad • Instructional", "Egypt"),
        QariInfo("ar.minshawi", "Muhammad Siddiq Al-Minshawi", "محمد صديق المنشاوي", "Murattal • Hafs", "Egypt"),
        QariInfo("ar.minshawimujawwad", "Al-Minshawi (Mujawwad)", "المنشاوي (مجود)", "Mujawwad • Melodic", "Egypt"),
        QariInfo("ar.mahermuaiqly", "Maher Al-Muaiqly", "ماهر المعيقلي", "Murattal • Haramain", "Saudi Arabia"),
        QariInfo("ar.saoodshuraym", "Saud Al-Shuraim", "سعود الشريم", "Murattal • Haramain", "Saudi Arabia"),
        QariInfo("ar.abdurrahmaansudais", "Abdurrahman As-Sudais", "عبد الرحمن السديس", "Murattal • Haramain", "Saudi Arabia"),
        QariInfo("ar.ghamdi", "Sa'ad Al-Ghamdi", "سعد الغامدي", "Murattal • Hafs", "Saudi Arabia"),
        QariInfo("ar.shaatree", "Abu Bakr Al-Shatri", "أبو بكر الشاطري", "Murattal • Hafs", "Saudi Arabia"),
        QariInfo("ar.hanirifai", "Hani Ar-Rifai", "هاني الرفاعي", "Murattal • Emotional", "Saudi Arabia"),
        QariInfo("ar.nasseralqatami", "Nasser Al-Qatami", "ناصر القطامي", "Murattal • Hafs", "Saudi Arabia"),
        QariInfo("ar.yasseraldosari", "Yasser Al-Dosari", "ياسر الدوسري", "Murattal • Haramain", "Saudi Arabia"),
        QariInfo("ar.alijaber", "Ali Jaber", "علي جابر", "Murattal • Classic Haram", "Saudi Arabia"),
        QariInfo("ar.faresabbad", "Fares Abbad", "فارس عباد", "Murattal • Clear", "Yemen"),
        QariInfo("ar.ahmedajamy", "Ahmed ibn Ali Al-Ajmi", "أحمد بن علي العجمي", "Murattal • Hafs", "Saudi Arabia"),
        QariInfo("ar.abdullahbasfar", "Abdullah Basfar", "عبد الله بصفر", "Murattal • Instructional", "Saudi Arabia"),
        QariInfo("ar.muhammadayyoub", "Muhammad Ayyub", "محمد أيوب", "Murattal • Madinah", "Saudi Arabia"),
    )

    fun findById(id: String): QariInfo =
        qaris.firstOrNull { it.id == id } ?: qaris.first()
}
