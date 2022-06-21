package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import java.security.AccessControlContext

private const val TAG = "CheatActivity"
const val EXTRA_ANSWER_IS_ALREADY_SHOWN = "com.bigneardranch.android.geoquiz.answer_is_already_shown"
const val EXTRA_ANSWER_SHOWN = "com.bigneardranch.android.geoquiz.answer_shown"
private const val MAX_CHEATS_LEFT = 3
private const val EXTRA_ANSWER_IS_TRUE =
    "com.bigneardranch.android.geoquiz.answer_is_true"
private const val EXTRA_NUM_OF_CHEATS_LEFT =
    "com.bigneardranch.android.geoquiz.num_of_chets_left"

class CheatActivity : AppCompatActivity() {
    private var answerIsTrue = false
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var apiTextView: TextView
    private lateinit var cheatsTextView: TextView
    private var cheatsNumLeft = MAX_CHEATS_LEFT
    private var shownCheat = false

//    private val quizViewModel: QuizViewModel by lazy {
//        ViewModelProvider(this@MainActivity)
//            .get(QuizViewModel::class.java)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        shownCheat = intent.getBooleanExtra(EXTRA_ANSWER_IS_ALREADY_SHOWN, false)
        cheatsNumLeft = intent.getIntExtra(EXTRA_NUM_OF_CHEATS_LEFT, MAX_CHEATS_LEFT)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        apiTextView = findViewById(R.id.api_text_view)
        cheatsTextView = findViewById(R.id.cheat_text_view)

        val versionString = "${resources.getString(R.string.api_level)} ${Build.VERSION.SDK_INT}"
        apiTextView.setText(versionString)

//        val cheatsString = "${quizViewModel.checkCheatsNum()} ${resources.getString(R.string.cheats_left)}"
//        cheatsTextView.setText(cheatsString)

        updateCheatsNum()

//        cheatsNumLeft = when {
//            shownCheat -> cheatsNumLeft
//            else -> cheatsNumLeft - 1
//        }

        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }

            cheatsNumLeft = when {
                shownCheat -> cheatsNumLeft
                else -> cheatsNumLeft - 1
            }

            shownCheat = true

            updateCheatsNum()

            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }

        setResult(Activity.RESULT_OK, data)
    }

    private fun updateCheatsNum() {
        val cheatsString = "${cheatsNumLeft} ${resources.getString(R.string.cheats_left)}${if (shownCheat) "\nAlready cheated" else ""}"
        Log.d(TAG, cheatsNumLeft.toString())
        cheatsTextView.setText(cheatsString)

        when {
            cheatsNumLeft > 0 -> showAnswerButton.isEnabled = true
            shownCheat -> showAnswerButton.isEnabled = true
            else -> showAnswerButton.isEnabled = false
        }
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean, cheatsNumLeft: Int, shownCheat: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
                putExtra(EXTRA_NUM_OF_CHEATS_LEFT, cheatsNumLeft)
                putExtra(EXTRA_ANSWER_IS_ALREADY_SHOWN, shownCheat)
            }
        }
    }


}