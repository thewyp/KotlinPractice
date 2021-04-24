package com.thewyp.android.geoquiz

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheakButton: Button
    private lateinit var nextButton: Button
    private lateinit var questionTextView: TextView

    private val viewModel by viewModels<QuizViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheakButton = findViewById(R.id.cheat_button)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            viewModel.moveToNext()
            updateQuestion()
        }

        questionTextView.setOnClickListener {
            viewModel.moveToNext()
            updateQuestion()
        }

        val function: (v: View) -> Unit = {
            val newIntent = CheatActivity.newIntent(this, viewModel.currentQuestionAnswer)
            val options =
                ActivityOptions.makeClipRevealAnimation(it, 0, 0, it.width, it.height)
            startActivityForResult(newIntent, REQUEST_CODE_CHEAT, options.toBundle())
        }
        cheakButton.setOnClickListener(function)

        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            viewModel.currentQuestion.isCheat =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }


    private fun checkAnswer(userAnswer: Boolean) {
        val messageResId = when {
            viewModel.currentQuestion.isCheat -> R.string.judgement_toast
            userAnswer == viewModel.currentQuestionAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun updateQuestion() {
        val questionTextResId = viewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        cheakButton.isEnabled = !viewModel.checkCheated()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState: ")
        outState.putInt(KEY_INDEX, viewModel.currentIndex)
    }
}