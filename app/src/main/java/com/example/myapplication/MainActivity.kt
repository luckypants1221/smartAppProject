package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.home.*
import com.example.myapplication.ui.quiz.CourseIds
import com.example.myapplication.ui.quiz.ProgressStore
import com.example.myapplication.ui.quiz.QuizActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private val QUIZ_TOTAL = 5               // QuizActivity의 total과 동일해야 함
    private val COURSE_ID  = CourseIds.COMP_BASIC  // 코스 식별자(코스별로 다르게 주면 됨)

    private var rvCourses: RecyclerView? = null
    private var rvQuests: RecyclerView? = null
    private var chartContainer: android.widget.FrameLayout? = null

    private var coursesAdapter: CourseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 드로어/툴바
        val drawer = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.navigationView)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size)
        toolbar.setNavigationOnClickListener { drawer.openDrawer(GravityCompat.START) }
        navView.setNavigationItemSelectedListener {
            // TODO: 메뉴 처리(로그인/로그아웃 등)
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        rvCourses = findViewById(R.id.rvCourses)
        rvQuests  = findViewById(R.id.rvQuests)
        chartContainer = findViewById(R.id.chartContainer)

        if (rvCourses != null && rvQuests != null && chartContainer != null) {
            rvCourses!!.layoutManager = LinearLayoutManager(this)
            rvQuests!!.layoutManager  = LinearLayoutManager(this)

            // 최초 한 번 세팅 (저장된 진행 반영)
            coursesAdapter = buildCoursesAdapter(loadPercent())
            rvCourses!!.adapter = coursesAdapter

            rvQuests!!.adapter = QuestAdapter(
                mutableListOf(
                    QuestItem("일일 학습 30분", 30, false),
                    QuestItem("복습하기", 20, false)
                )
            )

            val chartView = WeeklyBarChartView(this).apply {
                values = listOf(12, 16, 9, 20, 13, 17, 26)
                setPadding(24, 12, 24, 24)
            }
            chartContainer!!.addView(chartView)
        }
    }

    override fun onResume() {
        super.onResume()
        // 🔁 퀴즈에서 나갔다 돌아오면 진행도 갱신
        val percent = loadPercent()
        val newItems = listOf(CourseItem("컴활 학습", percent, 30))
        coursesAdapter?.updateItems(newItems)
    }

    // ────────────────────────────────────────────────
    // 헬퍼들
    private fun loadPercent(): Int {
        val (_, solvedCount) = ProgressStore.load(this, COURSE_ID)
        val percent = if (QUIZ_TOTAL == 0) 0 else (solvedCount.toFloat() / QUIZ_TOTAL * 100).toInt()
        return percent.coerceIn(0, 100)
    }

    private fun buildCoursesAdapter(percent: Int): CourseAdapter {
        val courses = listOf(CourseItem("컴활 학습", percent, 30))
        return CourseAdapter(
            items = courses,
            onStartClick = {
                val (idx, solved) = ProgressStore.load(this, COURSE_ID)
                if (solved >= QUIZ_TOTAL || idx >= QUIZ_TOTAL) {
                    // ✅ 다음 입장은 무조건 1번부터
                    ProgressStore.saveSync(this, COURSE_ID, currentIndex = 1, solvedCount = 0)
                }
                startActivity(Intent(this, QuizActivity::class.java)
                    .putExtra(CourseIds.EXTRA_COURSE_ID, COURSE_ID))
            }
            ,
            onCardClick = { /* Optional: 상세 이동 */ },
            onReviewClick = { /* Optional */ }
        )
    }
}
