package com.example.myapplication.ui.quiz

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator

class QuizActivity : AppCompatActivity() {

    // 뷰
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
    private var skipAutoSave = false
    private lateinit var ivJudge: android.widget.ImageView

    // 진행 상태 (current는 1-based 문제 인덱스)
    private var current = 1
    private var answered = false
    private var isCorrect = false
    private lateinit var courseId: String

    // 더미 문제 (서버 연동 전)
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

    // ✅ 하드코드 대신 실제 문항 수 사용
    private val total get() = items.size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        courseId = intent.getStringExtra(CourseIds.EXTRA_COURSE_ID) ?: CourseIds.COMP_BASIC

        bindViews()
        setupProgress()
        bindChoiceClicks()

        val (savedIndex, savedSolved) = ProgressStore.load(this, courseId)
        // ✅ 완료 상태거나 마지막 인덱스에 걸려 있으면 무조건 리셋
        if (savedSolved >= total || savedIndex >= total) {
            current = 1
            answered = false
            ProgressStore.save(this, courseId, currentIndex = 1, solvedCount = 0)
        } else {
            current = savedIndex.coerceIn(1, total)
        }

        renderQuestion()
        updateProgress()

        onBackPressedDispatcher.addCallback(this) { showExitConfirmDialog() }
    }


    private fun currentCourseId(): String = intent.getStringExtra("courseId") ?: "default"

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
            if (!answered) return@setOnClickListener

            if (current < total) {
                current += 1
                hideFeedbacks()
                renderQuestion()
                updateProgress()
                btnContinue.text = if (current == total) "완료" else "다음 문제"

                // 🔁 진행 중 저장
                ProgressStore.save(this, courseId, currentIndex = current, solvedCount = solvedSoFar())
            } else {
                // 마지막 문제 → 화면 100% 먼저
                progress.setProgressCompat(total, true)
                tvPercent.text = "100%"
                showCompletion()
            }
        }

    }

    private fun setupProgress() {
        progress.max = total
        // 시작 시 이미 푼 개수로 표시 (current는 1-based)
        updateProgress()
        btnContinue.text = if (current == total) "완료" else "다음 문제"
    }
    private fun solvedSoFar(): Int {
        val base = (current - 1).coerceAtLeast(0)  // 이전까지 완전히 끝낸 개수
        val extra = if (answered) 1 else 0         // 현재 문제를 이미 풀었다면 +1
        return (base + extra).coerceAtMost(total)
    }
    // ✅ 진행도는 “푼 문제 수 = current - 1” 로 표시
    private fun updateProgress() {
        val solved = solvedSoFar()
        progress.setProgressCompat(solved, true)
        val pct = if (total == 0) 0 else (solved.toFloat() / total * 100).toInt()
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
            explanationContainer.visibility = View.GONE
            tvExplanation.text = ""
        } else {
            tvFeedback.text = "아쉽다! 오답이에요"
            tvFeedback.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            ivJudge.setImageResource(R.drawable.quit4)
            ivJudge.visibility = View.VISIBLE
            tvExplanation.text = item.explanation
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
        hideFeedbacks()
        resetChoiceStyles()

        ivJudge.setImageResource(R.drawable.quit2)
        btnContinue.text = if (current == total) "완료" else "다음 문제"
    }

    private fun hideFeedbacks() {
        explanationContainer.visibility = View.GONE
        tvExplanation.text = ""
        feedbackBar.visibility = View.GONE
        tvFeedback.text = ""
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

        when (correctIndex) {
            0 -> styleCorrect(choice1, tvChoice1)
            1 -> styleCorrect(choice2, tvChoice2)
            2 -> styleCorrect(choice3, tvChoice3)
        }
        if (selectedIndex != correctIndex) {
            when (selectedIndex) {
                0 -> styleWrong(choice1, tvChoice1)
                1 -> styleWrong(choice2, tvChoice2)
                2 -> styleWrong(choice3, tvChoice3)
            }
        }
    }

    private fun showExitConfirmDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("퀴즈 나가기")
            .setMessage("나가면 진행 상황이 저장돼요. 나갈까요?")
            .setNegativeButton("취소") { d, _ -> d.dismiss() }
            .setPositiveButton("나가기") { d, _ ->
                d.dismiss()
                // ✅ 지금까지 푼 개수 기준으로 저장 (마지막 문제 풀고 바로 나가도 100%)
                ProgressStore.saveSync(
                    this, courseId,
                    currentIndex = current,           // 위치는 현재 문제
                    solvedCount = solvedSoFar()       // 개수는 푼 만큼(최대 total)
                )
                finish()
            }
            .show()
    }

    private fun showCompletion() {
        MaterialAlertDialogBuilder(this)
            .setTitle("퀴즈 완료")
            .setMessage("모든 문제를 다 풀었어요! 처음부터 다시 풀까요?")
            .setNegativeButton("닫기") { d, _ ->
                d.dismiss()
                skipAutoSave = true
                // ✅ 메인 100% 보이도록 확정 기록
                ProgressStore.saveSync(this, courseId, currentIndex = total, solvedCount = total)
                finish()
            }
            .setPositiveButton("다시 풀기") { d, _ ->
                d.dismiss()
                skipAutoSave = false
                current = 1
                answered = false
                ProgressStore.save(this, courseId, currentIndex = 1, solvedCount = 0)

                hideFeedbacks()
                resetChoiceStyles()
                renderQuestion()
                updateProgress() // 0%
                btnContinue.text = "다음 문제"
            }
            .show()
    }

    override fun onPause() {
        super.onPause()
        if (skipAutoSave) return
        // ✅ 항상 solvedSoFar()로 저장
        ProgressStore.save(this, courseId, currentIndex = current, solvedCount = solvedSoFar())
    }
}