package com.example.data

data class SentenceItem(
    val english: String,
    val bengali: String
)

data class SvoPuzzleItem(
    val bengali: String,
    val englishWords: List<String>,
    val scrambledWords: List<String>,
    val tip: String
)

data class VocabularyItem(
    val word: String,
    val meaning: String,
    val example: String,
    val exampleMeaning: String
)

data class TenseSliderItem(
    val past: String,
    val present: String,
    val future: String,
    val pastMeaning: String,
    val presentMeaning: String,
    val futureMeaning: String
)

data class QuizItem(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

data class LessonContent(
    val day: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val listenRepeatSentences: List<SentenceItem>,
    val svoPuzzle: SvoPuzzleItem,
    val vocabulary: List<VocabularyItem>,
    val tenseSlider: TenseSliderItem,
    val quiz: QuizItem
)

object Curriculum {
    private val lessons = listOf(
        // Day 1
        LessonContent(
            day = 1,
            title = "পরিচয় ও SVO Shift",
            subtitle = "SVO (Subject-Verb-Object) Word Order",
            description = "বাংলায় Verb শেষে বসলেও ইংরেজিতে Verb সাবজেক্টের পরেই বসে। এটি শিখুন।",
            listenRepeatSentences = listOf(
                SentenceItem("I like tea", "আমি চা পছন্দ করি"),
                SentenceItem("She reads books", "সে বই পড়ে"),
                SentenceItem("We play cricket", "আমরা ক্রিকেট খেলি")
            ),
            svoPuzzle = SvoPuzzleItem(
                bengali = "আমি চা পছন্দ করি",
                englishWords = listOf("I", "like", "tea"),
                scrambledWords = listOf("like", "I", "tea"),
                tip = "Subject-Verb-Object (SVO) Shift: বাংলায় Verb (ক্রিয়া) সবার শেষে বসে (যেমন: পছন্দ করি)। কিন্তু ইংরেজিতে Verb সবসময় Subject-এর ঠিক পরেই বসে (যেমন: I like tea)।"
            ),
            vocabulary = listOf(
                VocabularyItem("Like", "পছন্দ করা", "I like apples.", "আমি আপেল পছন্দ করি।"),
                VocabularyItem("Read", "পড়া", "He reads a newspaper.", "সে সংবাদপত্র পড়ে।")
            ),
            tenseSlider = TenseSliderItem(
                past = "I liked tea",
                present = "I like tea",
                future = "I will like tea",
                pastMeaning = "আমি চা পছন্দ করতাম",
                presentMeaning = "আমি চা পছন্দ করি",
                futureMeaning = "আমি চা পছন্দ করব"
            ),
            quiz = QuizItem(
                question = "How do you say 'সে বই পড়ে'?",
                options = listOf("She books reads", "She reads books", "Reads she books"),
                correctIndex = 1
            )
        ),
        // Day 2
        LessonContent(
            day = 2,
            title = "আর্টিকেল ব্যবহার (Articles)",
            subtitle = "Missing Articles: a, an, the",
            description = "ইংরেজিতে Singular Countable Nouns-এর আগে a, an, the এর সঠিক ব্যবহার জানুন।",
            listenRepeatSentences = listOf(
                SentenceItem("This is a book", "এটি একটি বই"),
                SentenceItem("I eat an apple", "আমি একটি আপেল খাই"),
                SentenceItem("The sky is blue", "আকাশটি নীল")
            ),
            svoPuzzle = SvoPuzzleItem(
                bengali = "এটি একটি চমৎকার দিন",
                englishWords = listOf("It", "is", "a", "beautiful", "day"),
                scrambledWords = listOf("beautiful", "is", "a", "It", "day"),
                tip = "Missing Articles Rule: বাংলায় সাধারণত 'একটি' বা 'টি' সবসময় বলা হয় না। কিন্তু ইংরেজিতে Singular Countable Nouns-এর আগে 'a', 'an' বা নির্দিষ্ট করতে 'the' বসানো বাধ্যতামূলক।"
            ),
            vocabulary = listOf(
                VocabularyItem("Book", "বই", "This is a library book.", "এটি লাইব্রেরির একটি বই।"),
                VocabularyItem("Apple", "আপেল", "An apple a day keeps the doctor away.", "দিনে একটি আপেল ডাক্তার থেকে দূরে রাখে।")
            ),
            tenseSlider = TenseSliderItem(
                past = "It was a beautiful day",
                present = "It is a beautiful day",
                future = "It will be a beautiful day",
                pastMeaning = "এটি একটি সুন্দর দিন ছিল",
                presentMeaning = "এটি একটি সুন্দর দিন",
                futureMeaning = "এটি একটি সুন্দর দিন হবে"
            ),
            quiz = QuizItem(
                question = "How do you say 'আমি একটি আপেল খাই'?",
                options = listOf("I eat apple", "I eat an apple", "An apple I eat"),
                correctIndex = 1
            )
        ),
        // Day 3
        LessonContent(
            day = 3,
            title = "V/W উচ্চারণ প্র্যাকটিস",
            subtitle = "V vs W Phonetics Practice",
            description = "Bengali স্পিকারদের কমন ভুল V এবং W-এর সঠিক উচ্চারণ এবং পার্থক্য প্র্যাকটিস করুন।",
            listenRepeatSentences = listOf(
                SentenceItem("Very wet weather", "খুব ভেজা আবহাওয়া"),
                SentenceItem("The wine in the vine", "লতার দ্রাক্ষারস"),
                SentenceItem("We visited the valley", "আমরা উপত্যকাটি পরিদর্শন করেছি")
            ),
            svoPuzzle = SvoPuzzleItem(
                bengali = "আমরা পানি পান করি",
                englishWords = listOf("We", "drink", "water"),
                scrambledWords = listOf("water", "We", "drink"),
                tip = "V/W Phonetic Practice: 'V' উচ্চারণ করতে উপরের দাঁত নিচের ঠোঁটে স্পর্শ করবে (যেমন: Very)। কিন্তু 'W' উচ্চারণ করতে ঠোঁট গোল বা বৃত্তাকার করতে হবে (যেমন: Wet)।"
            ),
            vocabulary = listOf(
                VocabularyItem("Very", "খুব", "She is very smart.", "সে খুব বুদ্ধিমান।"),
                VocabularyItem("Wet", "ভেজা", "The grass is wet.", "ঘাসগুলো ভেজা।")
            ),
            tenseSlider = TenseSliderItem(
                past = "It was very wet",
                present = "It is very wet",
                future = "It will be very wet",
                pastMeaning = "এটি খুব ভেজা ছিল",
                presentMeaning = "এটি খুব ভেজা",
                futureMeaning = "এটি খুব ভেজা হবে"
            ),
            quiz = QuizItem(
                question = "Which letter sound requires touching your upper teeth to your lower lip?",
                options = listOf("The letter 'W'", "The letter 'V'", "Both letters"),
                correctIndex = 1
            )
        ),
        // Day 4
        LessonContent(
            day = 4,
            title = "To-Be Verb ও সর্বনাম",
            subtitle = "Personal Pronouns & Am/Is/Are",
            description = "বাংলায় 'হয়/হই' ক্রিয়া না থাকলেও ইংরেজিতে to-be verb ব্যবহারের নিয়ম শিখুন।",
            listenRepeatSentences = listOf(
                SentenceItem("I am a student", "আমি একজন ছাত্র"),
                SentenceItem("They are happy", "তারা সুখী"),
                SentenceItem("He is a doctor", "সে একজন ডাক্তার")
            ),
            svoPuzzle = SvoPuzzleItem(
                bengali = "সে একজন শিক্ষক",
                englishWords = listOf("She", "is", "a", "teacher"),
                scrambledWords = listOf("is", "a", "teacher", "She"),
                tip = "To-Be Verb omission: বাংলায় 'আমি ছাত্র' বা 'সে শিক্ষক' বলার সময় কোনো Verb বা ক্রিয়া থাকে না। কিন্তু ইংরেজিতে 'am', 'is', 'are' বসানো আবশ্যক!"
            ),
            vocabulary = listOf(
                VocabularyItem("Teacher", "শিক্ষক", "Our teacher is kind.", "আমাদের শিক্ষক দয়ালু।"),
                VocabularyItem("Student", "ছাত্র", "He is a college student.", "সে কলেজের একজন ছাত্র।")
            ),
            tenseSlider = TenseSliderItem(
                past = "I was a student",
                present = "I am a student",
                future = "I will be a student",
                pastMeaning = "আমি একজন ছাত্র ছিলাম",
                presentMeaning = "আমি একজন ছাত্র",
                futureMeaning = "আমি একজন ছাত্র হব"
            ),
            quiz = QuizItem(
                question = "How do you translate 'আমি সুখী' to English?",
                options = listOf("I happy", "I am happy", "Am I happy"),
                correctIndex = 1
            )
        ),
        // Day 5
        LessonContent(
            day = 5,
            title = "না-বোধক বাক্য ও Auxiliary",
            subtitle = "Negation: do not / does not",
            description = "ইংরেজিতে না-বোধক বাক্য তৈরি করার জন্য do not এবং does not এর ব্যবহার আয়ত্ত করুন।",
            listenRepeatSentences = listOf(
                SentenceItem("I do not like tea", "আমি চা পছন্দ করি না"),
                SentenceItem("She does not speak English", "সে ইংরেজি বলে না"),
                SentenceItem("We do not play football", "আমরা ফুটবল খেলি না")
            ),
            svoPuzzle = SvoPuzzleItem(
                bengali = "আমি মিথ্যা বলি না",
                englishWords = listOf("I", "do", "not", "lie"),
                scrambledWords = listOf("not", "do", "I", "lie"),
                tip = "English Negation: বাংলায় ক্রিয়ার সাথে শুধু 'না' যোগ করলেই নেতিবাচক বাক্য হয় (যেমন: খেলি না)। কিন্তু ইংরেজিতে নেতিবাচক করতে Auxiliary verb (do/does) এবং 'not' ব্যবহার করতে হয়।"
            ),
            vocabulary = listOf(
                VocabularyItem("Speak", "কথা বলা", "Speak slowly.", "ধীরে কথা বলুন।"),
                VocabularyItem("Lie", "মিথ্যা বলা", "Never tell a lie.", "কখনও মিথ্যা বলবেন না।")
            ),
            tenseSlider = TenseSliderItem(
                past = "I did not like tea",
                present = "I do not like tea",
                future = "I will not like tea",
                pastMeaning = "আমি চা পছন্দ করতাম না",
                presentMeaning = "আমি চা পছন্দ করি না",
                futureMeaning = "আমি চা পছন্দ করব না"
            ),
            quiz = QuizItem(
                question = "How do you translate 'সে চা পছন্দ করে না'?",
                options = listOf("She not likes tea", "She does not like tea", "She do not like tea"),
                correctIndex = 1
            )
        )
    )

    fun getLessonContent(day: Int): LessonContent {
        if (day <= 0) return lessons[0]
        val index = (day - 1) % lessons.size
        return lessons[index].copy(day = day)
    }
}
