package com.thewyp.android.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var currentIndex = 0

    val currentQuestion: Question
        get() = questionBank[currentIndex]

    val currentQuestionText: Int
        get() = currentQuestion.textResId

    val currentQuestionAnswer: Boolean
        get() = currentQuestion.answer

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun checkCheated(): Boolean {
        var count = 0
        for (question in questionBank) {
            if (question.isCheat) {
                count++
            }
            if (count == 3) {
                break
            }
        }
        return count == 3
    }
}