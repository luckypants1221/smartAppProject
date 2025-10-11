package com.example.myapplication.ui.quiz

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
class QuizActivity : AppCompatActivity() {// 뷰
private lateinit var progress: LinearProgressIndicator
    private lateinit var tvPercent: TextView
    private lateinit var tvQuestion: TextView

    private lateinit var choice1: MaterialCardView
    private lateinit var tvChoice1: TextView
    private lateinit var choice2: MaterialCardView
    private lateinit var tvChoice2: TextView
    private lateinit var choice3: MaterialCardView
    private lateinit var tvChoice3: TextView

    private lateinit var explanationContainer: View
    private lateinit var tvExplanation: TextView
    private lateinit var feedbackBar: View
    private lateinit var tvFeedback: TextView
    private lateinit var btnContinue: MaterialButton

    private lateinit var ivJudge: android.widget.ImageView

    // 진행 상태
    private var current = 1
    private val total = 5  // 임시 총 문제 수(서버 연동 시 교체)
    private var answered = false
    private var isCorrect = false

    // 더미 데이터(서버 연동 전까지 사용)
    private data class QuizItem(
        val question: String,
        val choices: List<String>,
        val answerIndex: Int,
        val explanation: String
    )
    private val items = listOf(
        QuizItem("사과의 영어는?", listOf("Apple", "Banana", "Grape"), 0, "사과는 Apple."),
        QuizItem("포도의 영어는?", listOf("Orange", "Grape", "Melon"), 1, "포도는 Grape."),
        QuizItem("바나나의 영어는?", listOf("Banana", "Apple", "Peach"), 0, "바나나는 Banana."),
        QuizItem("오렌지의 영어는?", listOf("Berry", "Orange", "Lemon"), 1, "오렌지는 Orange."),
        QuizItem("복숭아의 영어는?", listOf("Peach", "Pear", "Plum"), 0, "복숭아는 Peach.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        bindViews()
        setupProgress()
        bindChoiceClicks()
        renderQuestion()
    }

    private fun bindViews() {
        progress = findViewById(R.id.progressQuiz)
        tvPercent = findViewById(R.id.tvProgressPercent)
        tvQuestion = findViewById(R.id.tvQuestion)


        ivJudge = findViewById(R.id.ivJudge)
        choice1 = findViewById(R.id.choice1)
        tvChoice1 = findViewById(R.id.tvChoice1)
        choice2 = findViewById(R.id.choice2)
        tvChoice2 = findViewById(R.id.tvChoice2)
        choice3 = findViewById(R.id.choice3)
        tvChoice3 = findViewById(R.id.tvChoice3)

        explanationContainer = findViewById(R.id.explanationContainer)
        tvExplanation = findViewById(R.id.tvExplanation)
        feedbackBar = findViewById(R.id.feedbackBar)
        tvFeedback = findViewById(R.id.tvFeedback)
        btnContinue = findViewById(R.id.btnContinue)

        btnContinue.setOnClickListener {
            android.util.Log.d("quiz","continue clicked, answered=$answered current=$current/$total")
            if (!answered) return@setOnClickListener

            if (current < total) {
                current += 1

                // 다음 문제 진입하기 전, 혹시 남아있을지도 모를 UI 숨김
                explanationContainer.visibility = View.GONE
                tvExplanation.text = ""
                feedbackBar.visibility = View.GONE
                tvFeedback.text = ""

                renderQuestion()
                updateProgress()
                btnContinue.text = if (current == total) "완료" else "다음 문제"
            } else {
                showCompletion()
            }
        }
    }

    private fun setupProgress() {
        progress.max = total
        updateProgress()
        btnContinue.text = if (current == total) "완료" else "다음 문제"
    }

    private fun updateProgress() {
        progress.setProgressCompat(current.coerceAtMost(total), true)
        val pct = (current.toFloat() / total * 100).toInt()
        tvPercent.text = "$pct%"
    }

    private fun bindChoiceClicks() {
        choice1.setOnClickListener { onChoiceSelected(0) }
        choice2.setOnClickListener { onChoiceSelected(1) }
        choice3.setOnClickListener { onChoiceSelected(2) }
    }

    private fun onChoiceSelected(selectedIndex: Int) {
        if (answered) return
        handleAnswer(selectedIndex)
    }

    private fun handleAnswer(selectedIndex: Int) {
        if (answered) return
        answered = true

        val item = items[current - 1]
        val correctIndex = item.answerIndex
        isCorrect = (selectedIndex == correctIndex)

        feedbackBar.visibility = View.VISIBLE

        if (isCorrect) {
            tvFeedback.text = "정답이에요! 잘했어"
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.brand_primary))
            ivJudge.setImageResource(R.drawable.quit3)
            ivJudge.visibility = View.VISIBLE
            explanationContainer.visibility = View.GONE  // 정답이면 해설 숨김
            tvExplanation.text = ""                      // 혹시 남은 텍스트 제거
        } else {
            tvFeedback.text = "아쉽다! 오답이에요"
            tvFeedback.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            ivJudge.setImageResource(R.drawable.quit4)
            ivJudge.visibility = View.VISIBLE
            tvExplanation.text = item.explanation       // 해설 넣고
            explanationContainer.visibility = View.VISIBLE
        }

        renderChoicesResult(selectedIndex, correctIndex)
    }

    private fun renderQuestion() {
        val item = items[current - 1]
        tvQuestion.text = item.question
        tvChoice1.text = item.choices[0]
        tvChoice2.text = item.choices[1]
        tvChoice3.text = item.choices[2]

        answered = false
        isCorrect = false
        explanationContainer.visibility = View.GONE
        tvExplanation.text = ""  // 이전 해설 내용 비우기
        feedbackBar.visibility = View.GONE
        tvFeedback.text = ""     // 이전 피드백 문구 비우기

// 선택지 스타일 초기화
        resetChoiceStyles()

        ivJudge.setImageResource(R.drawable.quit2)
// 버튼 텍스트
        btnContinue.text = if (current == total) "완료" else "다음 문제"
    }

    private fun resetChoiceStyles() {
        val stroke = (1 * resources.displayMetrics.density).toInt()
        listOf(
            choice1 to tvChoice1,
            choice2 to tvChoice2,
            choice3 to tvChoice3
        ).forEach { (card, tv) ->
            card.setCardBackgroundColor(Color.WHITE)
            card.strokeWidth = stroke
            card.strokeColor = Color.parseColor("#E5E5E5")
            tv.setTextColor(Color.parseColor("#222222"))
            tv.setCompoundDrawables(null, null, null, null)
        }
    }

    private fun renderChoicesResult(selectedIndex: Int, correctIndex: Int) {
        fun styleCorrect(card: MaterialCardView, tv: TextView) {
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.brand_light_bg))
            card.strokeWidth = 0
            tv.setTextColor(ContextCompat.getColor(this, R.color.brand_primary))
        }
        fun styleWrong(card: MaterialCardView, tv: TextView) {
            card.setCardBackgroundColor(Color.parseColor("#FFF1F1"))
            card.strokeWidth = 0
            tv.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }

        // 정답 강조
        when (correctIndex) {
            0 -> styleCorrect(choice1, tvChoice1)
            1 -> styleCorrect(choice2, tvChoice2)
            2 -> styleCorrect(choice3, tvChoice3)
        }
        // 오답 선택이면 그 선택지만 오답 스타일
        if (selectedIndex != correctIndex) {
            when (selectedIndex) {
                0 -> styleWrong(choice1, tvChoice1)
                1 -> styleWrong(choice2, tvChoice2)
                2 -> styleWrong(choice3, tvChoice3)
            }
        }
    }

    private fun showCompletion() {
        val dlg = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("퀴즈 완료")
            .setMessage("모든 문제를 풀었어요. 수고했어!")
            .setPositiveButton("확인") { d, _ -> d.dismiss(); finish() }
            .create()
        dlg.show()
    }
}