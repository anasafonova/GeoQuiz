package com.bignerdranch.android.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlin.coroutines.EmptyCoroutineContext.get

private const val TAG = "QuizViewModel"
private const val MAX_CHEATS_LEFT = 3

class QuizViewModel : ViewModel() {
    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var maximumCheatNum = MAX_CHEATS_LEFT

    var isAnsweredBank = mutableListOf(
        false,
        false,
        false,
        false,
        false,
        false
    )

    var isCheatedBank = mutableListOf(
        false,
        false,
        false,
        false,
        false,
        false
    )

    //var isCheater = false

    var currentIndex = 0

    var numOfCorrectAnswsers = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun isAnswered(index: Int) {
        isAnsweredBank[index] = true
    }

    fun checkCheatsNum() : Int {
        Log.d(TAG, isCheatedBank.toString())
        Log.d(TAG, "${maximumCheatNum - isCheatedBank.count { it }}")
        return (maximumCheatNum - isCheatedBank.count { it })
    }

    fun isCheated(index: Int) {
        isCheatedBank[index] = true
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = if (currentIndex == 0) {
            questionBank.size - 1
        } else {
            (currentIndex - 1) % questionBank.size
        }
    }

//    fun cheater(cheater: Boolean) {
//        isCheater = cheater
//    }

    init {
        Log.d(TAG, "ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }
}
