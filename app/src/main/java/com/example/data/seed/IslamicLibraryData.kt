package com.example.data.seed

import androidx.annotation.DrawableRes
import com.example.R

data class HadithCollectionEntry(
    val id: String,
    val collection: String,
    val number: String,
    val narrator: String,
    val arabicText: String,
    val englishText: String,
    val bengaliText: String,
    val urduText: String,
    val grade: String,
)

data class IslamicStoryEntry(
    val id: String,
    val category: String,
    val titleEn: String,
    val titleBn: String,
    val titleAr: String,
    val titleUr: String,
    val summaryEn: String,
    val summaryBn: String,
    val summaryAr: String,
    val summaryUr: String,
    val sourceRef: String,
)

data class HajjStep(
    val stepNumber: Int,
    val titleEn: String,
    val titleBn: String,
    val arabicDua: String,
    val descriptionEn: String,
    val descriptionBn: String,
)

data class IslamicGlossaryTerm(
    val term: String,
    val arabic: String,
    val definitionEn: String,
    val definitionBn: String,
)

data class IslamicQuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val reference: String,
)

data class IslamicWallpaperItem(
    val id: String,
    val title: String,
    val category: String,
    val isDark: Boolean,
    @DrawableRes val drawableRes: Int,
    val resolutionLabel: String = "4K Ultra HD",
)

object IslamicLibraryData {
    val hadithCollections: List<String> = listOf(
        "40 Hadith Nawawi",
        "Sahih Bukhari",
        "Sahih Muslim",
        "Sunan Abu Dawood",
        "Jami' at-Tirmidhi",
        "Sunan an-Nasa'i",
        "Sunan Ibn Majah",
    )

    val hadiths: List<HadithCollectionEntry> = listOf(
        HadithCollectionEntry(
            id = "nawawi_1",
            collection = "40 Hadith Nawawi",
            number = "Hadith 1 (Bukhari 1, Muslim 1907)",
            narrator = "Umar ibn al-Khattab (RA)",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            englishText = "Actions are judged by intentions, and every person will have what they intended.",
            bengaliText = "সকল আমল নিয়তের উপর নির্ভরশীল, আর প্রত্যেক ব্যক্তি তাই পাবে যা সে নিয়ত করেছে।",
            urduText = "اعمال کا دارومدار نیتوں پر ہے اور ہر شخص کو وہی ملے گا جس کی اس نے نیت کی۔",
            grade = "Sahih (Muttafaqun 'Alayh)",
        ),
        HadithCollectionEntry(
            id = "bukhari_5027",
            collection = "Sahih Bukhari",
            number = "Hadith 5027",
            narrator = "Uthman ibn Affan (RA)",
            arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            englishText = "The best among you are those who learn the Quran and teach it.",
            bengaliText = "তোমাদের মধ্যে সর্বোত্তম সেই ব্যক্তি, যে নিজে কুরআন শেখে এবং অন্যকে শেখায়।",
            urduText = "تم میں سے بہترین وہ ہے جو قرآن سیکھے اور سکھائے۔",
            grade = "Sahih",
        ),
        HadithCollectionEntry(
            id = "muslim_2699",
            collection = "Sahih Muslim",
            number = "Hadith 2699",
            narrator = "Abu Hurairah (RA)",
            arabicText = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا، سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
            englishText = "Whoever follows a path in pursuit of knowledge, Allah will make easy for him a path to Paradise.",
            bengaliText = "যে ব্যক্তি জ্ঞান অর্জনের জন্য কোনো পথ অবলম্বন করে, আল্লাহ তার জন্য জান্নাতের পথ সহজ করে দেন।",
            urduText = "جو شخص علم کی تلاش میں کسی راستے پر چلتا ہے، اللہ اس کے لیے جنت کا راستہ آسان فرما دیتا ہے۔",
            grade = "Sahih",
        ),
        HadithCollectionEntry(
            id = "abudawood_4941",
            collection = "Sunan Abu Dawood",
            number = "Hadith 4941",
            narrator = "Abdullah ibn Amr (RA)",
            arabicText = "الرَّاحِمُونَ يَرْحَمُهُمُ الرَّحْمَنُ، ارْحَمُوا مَنْ فِي الأَرْضِ يَرْحَمْكُمْ مَنْ فِي السَّمَاءِ",
            englishText = "The merciful will be shown mercy by the Most Merciful. Show mercy to those on earth, and He who is in the heavens will show mercy to you.",
            bengaliText = "দয়াশীলদের প্রতি দয়াময় আল্লাহ দয়া করেন। তোমরা পৃথিবীবাসীর প্রতি দয়া করো, আসমানবাসী তোমাদের প্রতি দয়া করবেন।",
            urduText = "رحم کرنے والوں پر رحمٰن رحم فرماتا ہے۔ تم زمین والوں پر رحم کرو، آسمان والا تم پر رحم فرمائے گا۔",
            grade = "Sahih",
        ),
        HadithCollectionEntry(
            id = "tirmidhi_1987",
            collection = "Jami' at-Tirmidhi",
            number = "Hadith 1987",
            narrator = "Abu Dharr (RA)",
            arabicText = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ",
            englishText = "Fear Allah wherever you are, follow a bad deed with a good deed to erase it, and treat people with good character.",
            bengaliText = "তুমি যেখানেই থাকো আল্লাহকে ভয় করো, মন্দ কাজের পর ভালো কাজ করো যা তা মুছে দেবে এবং মানুষের সাথে উত্তম আচরণ করো।",
            urduText = "تم جہاں کہیں بھی ہو اللہ سے ڈرو، برائی کے بعد نیکی کرو جو اسے مٹا دے گی، اور لوگوں سے اچھے اخلاق سے پیش آؤ۔",
            grade = "Hasan Sahih",
        ),
        HadithCollectionEntry(
            id = "nasai_4995",
            collection = "Sunan an-Nasa'i",
            number = "Hadith 4995",
            narrator = "Anas ibn Malik (RA)",
            arabicText = "لَا يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لِأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            englishText = "None of you truly believes until he loves for his brother what he loves for himself.",
            bengaliText = "তোমাদের কেউ প্রকৃত মুমিন হতে পারবে না যতক্ষণ না সে নিজের জন্য যা পছন্দ করে তা তার ভাইয়ের জন্যও পছন্দ করে।",
            urduText = "تم میں سے کوئی شخص اس وقت تک کامل مومن نہیں ہو سکتا جب تک اپنے بھائی کے لیے وہی پسند نہ کرے جو اپنے لیے پسند کرتا ہے۔",
            grade = "Sahih",
        ),
        HadithCollectionEntry(
            id = "ibnmajah_224",
            collection = "Sunan Ibn Majah",
            number = "Hadith 224",
            narrator = "Anas ibn Malik (RA)",
            arabicText = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            englishText = "Seeking knowledge is an obligation upon every Muslim.",
            bengaliText = "জ্ঞান অর্জন করা প্রত্যেক মুসলিমের উপর ফরয।",
            urduText = "علم حاصل کرنا ہر مسلمان پر فرض ہے۔",
            grade = "Sahih (Al-Albani)",
        ),
        HadithCollectionEntry(
            id = "nawawi_13",
            collection = "40 Hadith Nawawi",
            number = "Hadith 15 (Bukhari 6018)",
            narrator = "Abu Hurairah (RA)",
            arabicText = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            englishText = "Whoever believes in Allah and the Last Day, let him speak good or remain silent.",
            bengaliText = "যে ব্যক্তি আল্লাহ ও শেষ দিনের প্রতি ঈমান রাখে, সে যেন উত্তম কথা বলে অথবা চুপ থাকে।",
            urduText = "جو شخص اللہ اور آخرت کے دن پر ایمان رکھتا ہے اسے چاہیے کہ اچھی بات کہے یا خاموش رہے۔",
            grade = "Sahih",
        ),
    )

    val storyCategories: List<String> = listOf(
        "Stories of the Prophets",
        "Stories of the Sahabah",
        "Stories of the Sahabiyat",
        "Lives of the Four Imams",
        "Stories from the Quran",
        "Islamic Manners",
        "Stories for Children",
        "Islamic History",
    )

    val stories: List<IslamicStoryEntry> = listOf(
        IslamicStoryEntry(
            id = "story_ibrahim",
            category = "Stories of the Prophets",
            titleEn = "Prophet Ibrahim (AS): Steadfastness Upon Tawhid",
            titleBn = "হযরত ইবরাহীম (আ.): তাওহীদের অবিচল আদর্শ",
            titleAr = "قصة نبي الله إبراهيم عليه السلام",
            titleUr = "حضرت ابراہیم علیہ السلام کا عزم و استقامت",
            summaryEn = "When Prophet Ibrahim (AS) stood firm for pure monotheism and was cast into the blazing fire, he placed complete trust in Allah ('HasbunAllahu wa Ni'mal Wakil'), and Allah commanded the fire: 'O fire, be coolness and safety upon Ibrahim.' (Surah Al-Anbiya 21:69).",
            summaryBn = "হযরত ইবরাহীম (আ.) যখন তাওহীদের দাওয়াতে অটল থেকে আগুনে নিক্ষিপ্ত হলেন, তখন তিনি আল্লাহর উপর পূর্ণ ভরসা করেছিলেন। আল্লাহ আগুনকে নির্দেশ দিলেন: 'হে আগুন! তুমি ইবরাহীমের জন্য শীতল ও নিরাপদ হয়ে যাও।' (সূরা আম্বিয়া ২১:৬৯)।",
            summaryAr = "لما أُلقي نبي الله إبراهيم عليه السلام في النار ثباتاً على التوحيد، توكل على ربه فقال الله: يا نار كوني برداً وسلاماً على إبراهيم (الأنبياء: ٦٩).",
            summaryUr = "جب حضرت ابراہیم علیہ السلام کو توحید پر استقامت کی وجہ سے آگ میں ڈالا گیا تو اللہ نے حکم دیا: اے آگ! ابراہیم پر ٹھنڈی اور سلامتی والی ہو جا (الأنبیاء: 69)۔",
            sourceRef = "Surah Al-Anbiya 21:51-70; Sahih al-Bukhari 4563",
        ),
        IslamicStoryEntry(
            id = "story_abubakr",
            category = "Stories of the Sahabah",
            titleEn = "Abu Bakr As-Siddiq (RA): Companion of the Cave",
            titleBn = "হযরত আবু বকর সিদ্দীক (রা.): গুহার সাথী",
            titleAr = "أبو بكر الصديق رضي الله عنه",
            titleUr = "حضرت ابوبکر صدیق رضی اللہ عنہ",
            summaryEn = "During the Hijrah to Madinah, when trackers reached the mouth of Cave Thawr, Prophet Muhammad (SAW) reassured Abu Bakr (RA): 'Do not grieve; indeed Allah is with us.' (Surah At-Tawbah 9:40).",
            summaryBn = "মদীনায় হিজরতের সময় সাওর গুহায় রাসূলুল্লাহ (সা.) হযরত আবু বকর (রা.)-কে সান্ত্বনা দিয়ে বলেছিলেন: 'ভয় পেয়ো না, নিশ্চয়ই আল্লাহ আমাদের সাথে আছেন।' (সূরা তাওবা ৯:৪০)।",
            summaryAr = "في هجرة النبي صلى الله عليه وسلم وغار ثور، قال لصاحبه أبي بكر: لا تحزن إن الله معنا (التوبة: ٤٠).",
            summaryUr = "ہجرت مدینہ کے وقت غارِ ثور میں نبی کریم ﷺ نے حضرت ابوبکرؓ سے فرمایا: غم نہ کرو، بے شک اللہ ہمارے ساتھ ہے (التوبہ: 40)۔",
            sourceRef = "Surah At-Tawbah 9:40; Sahih al-Bukhari 3653",
        ),
        IslamicStoryEntry(
            id = "story_khadijah",
            category = "Stories of the Sahabiyat",
            titleEn = "Khadijah bint Khuwaylid (RA): Pillar of Comfort",
            titleBn = "হযরত খাদিজা (রা.): রাসূল (সা.)-এর প্রথম সাথী",
            titleAr = "خديجة بنت خويلد رضي الله عنها",
            titleUr = "ام المؤمنین حضرت خدیجہ رضی اللہ عنہا",
            summaryEn = "When the first revelation arrived in Cave Hira, Khadijah (RA) comforted the Prophet (SAW) with unforgettable wisdom: 'Never! By Allah, Allah will never disgrace you. You keep good relations with your kin, help the poor, and honor your guests.'",
            summaryBn = "হেরা গুহায় প্রথম ওহী নাযিল হওয়ার পর মা খাদিজা (রা.) বলেছিলেন: 'আল্লাহ আপনাকে কখনোই লাঞ্ছিত করবেন না; আপনি আত্মীয়তার সম্পর্ক বজায় রাখেন, অসহায়দের সাহায্য করেন ও মেহমানের সম্মান করেন।'",
            summaryAr = "عندما نزل الوحي أول مرة طمأنت خديجة النبي صلى الله عليه وسلم بقولها: كلا والله ما يخزيك الله أبداً إنك لتصل الرحم وتقري الضيف.",
            summaryUr = "پہلی وحی کے نزول کے وقت حضرت خدیجہؓ نے نبی ﷺ کو تسلی دی: ہرگز نہیں، اللہ آپ کو کبھی رسوا نہیں کرے گا، آپ صلہ رحمی کرتے ہیں اور مہمان نوازی کرتے ہیں۔",
            sourceRef = "Sahih al-Bukhari 3",
        ),
        IslamicStoryEntry(
            id = "story_imams",
            category = "Lives of the Four Imams",
            titleEn = "The Four Great Imams of Fiqh",
            titleBn = "চার মহান ইমামের জীবনাদর্শ",
            titleAr = "الأئمة الأربعة رحمهم الله",
            titleUr = "ائمہ اربعہ رحمہم اللہ",
            summaryEn = "Imam Abu Hanifah (Kufa), Imam Malik (Madinah), Imam Ash-Shafi'i (Gaza/Egypt), and Imam Ahmad ibn Hanbal (Baghdad) dedicated their lives to preserving Quranic jurisprudence and Sunnah with deep humility: 'If a Hadith is authentic, that is my madhhab.'",
            summaryBn = "ইমাম আবু হানীফা (রহ.), ইমাম মালিক (রহ.), ইমাম শাফিঈ (রহ.) এবং ইমাম আহমদ ইবনে হাম্বল (রহ.) কুরআন ও সুন্নাহর আলোকে ফিকহ শাস্ত্রকে সুবিন্যস্ত করেছেন এবং পরস্পরকে গভীর শ্রদ্ধা করতেন।",
            summaryAr = "أئمة الفقه الأربعة: أبو حنيفة ومالك والشافعي وأحمد رحمهم الله خدموا الكتاب والسنة بتواضع عظيم واحترام متبادل.",
            summaryUr = "امام ابوحنیفہ، امام مالک، امام شافعی اور امام احمد بن حنبل رحمہم اللہ نے قرآن و سنت کی روشنی میں فقہ کی عظیم خدمت انجام دی۔",
            sourceRef = "Siyar A'lam an-Nubala (Adh-Dhahabi)",
        ),
        IslamicStoryEntry(
            id = "story_ashab_kahf",
            category = "Stories from the Quran",
            titleEn = "The Youth of the Cave (Ashab al-Kahf)",
            titleBn = "আসহাবে কাহাফের যুবকদের কাহিনী",
            titleAr = "أصحاب الكهف",
            titleUr = "اصحابِ کہف کا واقعہ",
            summaryEn = "Young believers who refused idolatry and sought refuge in a cave praying: 'Our Lord, grant us from Yourself mercy and prepare for us from our affair right guidance.' (Surah Al-Kahf 18:10).",
            summaryBn = "ঈমানদার যুবকদল শিরক ত্যাগ করে গুহায় আশ্রয় নিয়ে দোয়া করেছিলেন: 'হে আমাদের রব! আপনার পক্ষ থেকে আমাদেরকে রহমত দান করুন এবং আমাদের জন্য সঠিক পথ সুগম করে দিন।' (সূরা কাহফ ১৮:১০)।",
            summaryAr = "فتية آمنوا بربهم فزادهم هدى وأووا إلى الكهف داعين: ربنا آتنا من لدنك رحمة وهيئ لنا من أمرنا رشداً (الكهف: ١٠).",
            summaryUr = "ایمان والے نوجوانوں نے غار میں پناہ لی اور دعا کی: اے ہمارے رب! ہمیں اپنی طرف سے رحمت عطا فرما اور ہمارے کام میں ہدایت فراہم فرما (الکہف: 10)۔",
            sourceRef = "Surah Al-Kahf 18:9-26",
        ),
        IslamicStoryEntry(
            id = "story_manners",
            category = "Islamic Manners",
            titleEn = "Kindness to Parents & Neighbors",
            titleBn = "পিতা-মাতা ও প্রতিবেশীর প্রতি সদাচরণ",
            titleAr = "بر الوالدين والإحسان إلى الجار",
            titleUr = "والدین اور پڑوسیوں کے ساتھ حسن سلوک",
            summaryEn = "The Quran commands lowering the wing of humility to parents (17:23-24) and the Prophet (SAW) taught that Jibril emphasized caring for one's neighbor so often that he thought neighbors might inherit.",
            summaryBn = "কুরআনে পিতা-মাতার সাথে নম্র ব্যবহারের নির্দেশ দেওয়া হয়েছে (১৭:২৩-২৪) এবং প্রতিবেশীর হক আদায়ে বিশেষ গুরুত্ব দেওয়া হয়েছে।",
            summaryAr = "أوصى القرآن بالإحسان إلى الوالدين (الإسراء: ٢٣) وأوصى النبي صلى الله عليه وسلم بالجار خيراً.",
            summaryUr = "قرآن میں والدین کے ساتھ احسان کا حکم ہے (بنی اسرائیل: 23) اور حدیث میں پڑوسی کے حقوق پر خاص زور دیا گیا ہے۔",
            sourceRef = "Surah Al-Isra 17:23-24; Sahih al-Bukhari 6014",
        ),
        IslamicStoryEntry(
            id = "story_children",
            category = "Stories for Children",
            titleEn = "Prophet Sulayman (AS) & The Tiny Ant",
            titleBn = "হযরত সুলাইমান (আ.) ও ছোট্ট পিঁপড়ার গল্প",
            titleAr = "سليمان عليه السلام والنملة",
            titleUr = "حضرت سلیمان علیہ السلام اور چیونٹی",
            summaryEn = "When Prophet Sulayman (AS) marched with his army and overheard a small ant warning its colony to step aside, he smiled and thanked Allah for enabling him to appreciate every creature's life (Surah An-Naml 27:18-19).",
            summaryBn = "হযরত সুলাইমান (আ.) যখন ক্ষুদ্র পিঁপড়ার কথা শুনতে পেলেন, তখন তিনি মুচকি হাসলেন এবং ছোট প্রাণীর প্রতিও দয়া দেখিয়ে আল্লাহর শুকরিয়া আদায় করলেন (সূরা নামল ২৭:১৮-১৯)।",
            summaryAr = "تبسم نبي الله سليمان ضاحكاً من قول النملة وشكر الله على نعمه وحمى المخلوقات الضعيفة (النمل: ١٨-١٩).",
            summaryUr = "حضرت سلیمان علیہ السلام نے ایک چھوٹی چیونٹی کی بات سن کر مسکرا کر اللہ کا شکر ادا کیا اور لشکر کو بچا کر نکالا (النمل: 18-19)۔",
            sourceRef = "Surah An-Naml 27:18-19",
        ),
        IslamicStoryEntry(
            id = "story_history",
            category = "Islamic History",
            titleEn = "Bayt al-Hikmah & The Golden Age of Scholarship",
            titleBn = "বাইতুল হিকমাহ ও মুসলিম মনীষীদের অবদান",
            titleAr = "بيت الحكمة وإسهامات العلماء المسلمين",
            titleUr = "بیت الحکمہ اور مسلم سائنسدان",
            summaryEn = "Inspired by the Quranic call to reflect upon the heavens and earth, scholars in Baghdad, Cordoba, and Cairo—such as Al-Khwarizmi, Ibn Sina, and Al-Haytham—pioneered algebra, optics, astronomy, and clinical medicine.",
            summaryBn = "কুরআনের জ্ঞান-বিজ্ঞান চর্চার আহ্বানে অনুপ্রাণিত হয়ে আল-খাওয়ারিযমী, ইবনে সিনা ও ইবনুল হাইসাম গণিত, চিকিৎসাবিজ্ঞান ও জ্যোতির্বিজ্ঞানে যুগান্তকারী অবদান রাখেন।",
            summaryAr = "أسهم علماء المسلمين كالخوارزمي وابن الهيثم وابن سينا في الجبر والبصريات والطب انطلاقاً من الحث القرآني على التفكر.",
            summaryUr = "قرآنی دعوتِ فکر سے متاثر ہو کر الخوارزمی، ابن الہیثم اور ابن سینا نے ریاضی، بصریات اور طب میں عظیم خدمات انجام دیں۔",
            sourceRef = "Classical Islamic Civilization History",
        ),
    )

    val hajjSteps: List<HajjStep> = listOf(
        HajjStep(1, "Ihram & Niyyah", "ইহরাম ও নিয়ত", "لَبَّيْكَ اللَّهُمَّ عُمْرَةً / حَجًّا", "Enter the state of Ihram at the Miqat with sincerity, wearing two white unstitched cloths (men) or modest clothing (women), and recite Talbiyah.", "মিকাত থেকে পবিত্র নিয়তসহ ইহরাম বাঁধুন এবং তালবিয়াহ পাঠ করুন।"),
        HajjStep(2, "Tawaf around the Kaaba", "কাবা শরীফ তাওয়াফ", "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً...", "Circle the Holy Kaaba seven times counter-clockwise starting from the Black Stone (Hajar al-Aswad), followed by two Rak'ahs behind Maqam Ibrahim.", "হাজরে আসওয়াদ থেকে শুরু করে সাত চক্কর তাওয়াফ করুন এবং মাকামে ইবরাহীমের পেছনে দুই রাকাত নামাজ আদায় করুন।"),
        HajjStep(3, "Sa'i between Safa & Marwah", "সাফা ও মারওয়া সাঈ", "إِنَّ الصَّفَا وَالْمَرْوَةَ مِن شَعَائِرِ اللَّهِ", "Walk briskly between Safa and Marwah seven times, remembering Hajar (AS)'s trust in Allah and drinking Zamzam.", "সাফা ও মারওয়া পাহাড়ের মাঝে সাতবার সাঈ করুন এবং যমযমের পানি পান করুন।"),
        HajjStep(4, "Day of Arafah (9th Dhul-Hijjah)", "আরাফাতের ময়দানে অবস্থান", "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ...", "Stand in heartfelt Dua, Dhikr, and Istighfar at Arafah from Dhuhr until sunset—the pinnacle pillar of Hajj.", "৯ই জিলহজ্ব আরাফাতের ময়দানে যোহর থেকে সূর্যাস্ত পর্যন্ত দোয়া ও ইস্তিগফারে মশগুল থাকুন।"),
        HajjStep(5, "Muzdalifah, Rami, Qurbani & Halq", "মুযদালিফা, কঙ্কর নিক্ষেপ ও কুরবানী", "اللَّهُ أَكْبَرُ", "Spend the night at Muzdalifah, throw pebbles at Jamarat in Mina on the 10th, offer sacrifice, shave/trim hair, and perform Tawaf al-Ifadah.", "মুযদালিফায় রাত্রিযাপন করে মিনায় জামারাতে কঙ্কর নিক্ষেপ, কুরবানী ও মাথা মুণ্ডন/চুল ছাঁটা সম্পন্ন করুন।"),
    )

    val glossaryTerms: List<IslamicGlossaryTerm> = listOf(
        IslamicGlossaryTerm("Tawhid", "التوحيد", "The absolute oneness and uniqueness of Allah in Lordship, Worship, and Names/Attributes.", "আল্লাহর একত্ববাদ—রুবুবিয়াত, উলুহিয়াত ও আসমা-সিফাতে তাঁর এককত্ব।"),
        IslamicGlossaryTerm("Taqwa", "التقوى", "Mindfulness and awe of Allah that protects a believer from sin.", "আল্লাহভীতি ও সচেতনতা যা গুনাহ থেকে রক্ষা করে।"),
        IslamicGlossaryTerm("Sunnah", "السنة", "The authentic teachings, actions, and approvals of Prophet Muhammad (SAW).", "রাসূলুল্লাহ (সা.)-এর কথা, কাজ ও অনুমোদন।"),
        IslamicGlossaryTerm("Tawakkul", "التوكل", "Taking lawful practical means while placing full inner trust in Allah.", "সর্বাত্মক প্রচেষ্টার পর আল্লাহর উপর পূর্ণ ভরসা।"),
        IslamicGlossaryTerm("Ihsan", "الإحسان", "Worshipping Allah as though you see Him, knowing He always sees you.", "আল্লাহকে দেখার অনুভূতি নিয়ে ইবাদত ও সৎকর্ম করা।"),
        IslamicGlossaryTerm("Nisab", "النصاب", "Minimum wealth threshold (87.48g gold or 612.36g silver equivalent) above which Zakat becomes obligatory.", "যাকাত ফরয হওয়ার ন্যূনতম সম্পদের পরিমাণ।"),
    )

    val islamicQuiz: List<IslamicQuizQuestion> = listOf(
        IslamicQuizQuestion("iq1", "Which Surah is known as 'The Heart of the Quran' in classical narrations and has 83 verses?", listOf("Surah Al-Mulk", "Surah Ya-Sin", "Surah Ar-Rahman", "Surah Al-Kahf"), 1, "Surah 36 (83 Ayahs)"),
        IslamicQuizQuestion("iq2", "What is the standard Zakat rate on eligible zakatable wealth held for one lunar year (Hawl)?", listOf("1.5%", "2.5%", "5.0%", "10%"), 1, "Sahih al-Bukhari 1454"),
        IslamicQuizQuestion("iq3", "Which Surah is recommended to be recited every Friday (Jummah) for spiritual light?", listOf("Surah Al-Kahf", "Surah Al-Fajr", "Surah Al-Qadr", "Surah Al-Asr"), 0, "Al-Mustadrak / Sunan al-Kubra"),
        IslamicQuizQuestion("iq4", "How many Juz (parts) does the Noble Quran contain?", listOf("24 Juz", "28 Juz", "30 Juz", "40 Juz"), 2, "Standard Mushaf Division"),
        IslamicQuizQuestion("iq5", "What is the 9th month of the Islamic Hijri calendar in which the Quran was revealed?", listOf("Rajab", "Sha'ban", "Ramadan", "Dhul-Hijjah"), 2, "Surah Al-Baqarah 2:185"),
    )

    val wallpapers: List<IslamicWallpaperItem> = listOf(
        IslamicWallpaperItem("kaaba_twilight", "Masjid Al-Haram Twilight", "Kaaba & Makkah", isDark = true, R.drawable.img_wallpaper_kaaba),
        IslamicWallpaperItem("madinah_dawn", "Masjid An-Nabawi Green Dome", "Madinah", isDark = false, R.drawable.img_wallpaper_madinah),
        IslamicWallpaperItem("emerald_geometry", "Royal Emerald & Gold Star", "Geometric Patterns", isDark = true, R.drawable.img_wallpaper_geometry),
        IslamicWallpaperItem("courtyard_arch", "Golden Mosque Courtyard", "Masjid & Architecture", isDark = false, R.drawable.img_hero_emerald_arch),
    )
}
