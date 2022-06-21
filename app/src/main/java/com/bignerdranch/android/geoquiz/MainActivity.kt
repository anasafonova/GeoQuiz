package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val TAG = "QuizActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0
//private const val KEY_CHEAT = "cheat"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)
            .get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        //val isCheater = savedInstanceState?.getBoolean(KEY_CHEAT, false) ?: false
        //quizViewModel.isCheater = isCheater

//        val provider: ViewModelProvider = ViewModelProvider(this)
//        val quizViewModel = provider.get(QuizViewModel::class.java)
//        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            //Log.d(TAG, "Updating question text", Exception())
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }

        cheatButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val numberOfCheats = quizViewModel.checkCheatsNum()
            val cheatShown = quizViewModel.isCheatedBank[quizViewModel.currentIndex]

            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue, numberOfCheats, cheatShown)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions
                    .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        questionTextView.setOnClickListener {
            updateQuestion()
        }

        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
//            val cheater =
//                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if (data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) == true)
                quizViewModel.isCheated(quizViewModel.currentIndex) //cheater(cheater)
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        //savedInstanceState.putBoolean(KEY_CHEAT, quizViewModel.isCheater)
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)

        falseButton.isEnabled = !quizViewModel.isAnsweredBank[quizViewModel.currentIndex]
        trueButton.isEnabled = !quizViewModel.isAnsweredBank[quizViewModel.currentIndex]

        //quizViewModel.isCheater = false
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

//        val messageResId = if (userAnswer == correctAnswer) {
//            R.string.correct_toast
//        } else {
//            R.string.incorrect_toast
//        }

        quizViewModel.isAnswered(quizViewModel.currentIndex)

        quizViewModel.numOfCorrectAnswsers += if (userAnswer == correctAnswer) 1 else 0

        val messageResId = when {
            quizViewModel.isCheatedBank[quizViewModel.currentIndex] -> R.string.judgement_toast //isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show()

        falseButton.isEnabled = false
        trueButton.isEnabled = false

        val result : Boolean = quizViewModel.isAnsweredBank.all { it }

        if (result)
            Toast.makeText(this,
                "${resources.getString(R.string.num_toast)}: ${quizViewModel.numOfCorrectAnswsers*100/quizViewModel.questionBank.size}%.",
                Toast.LENGTH_SHORT)
                .show()

    }

}
