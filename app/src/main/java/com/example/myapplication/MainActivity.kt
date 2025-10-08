package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.home.*
import com.example.myapplication.ui.quiz.QuizActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 드로어/툴바
        val drawer = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size)
        toolbar.setNavigationOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            // TODO: 메뉴 처리(로그인/로그아웃 등)
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        // 홈 화면 리스트/차트 세팅
        // A 방식에서 activity_main.xml에 rvCourses, rvQuests, chartContainer가 “실제로” 있을 때만 아래 코드가 동작해.
        // 만약 네 activity_main.xml에 fragmentContainer만 있고 rv*가 없다면,
        // 이 블록 전체를 제거해야 한다. (그 경우 B 방식으로 가거나 HomeFragment로 옮겨야 함)
        val rvCourses = findViewById<RecyclerView?>(R.id.rvCourses)
        val rvQuests  = findViewById<RecyclerView?>(R.id.rvQuests)
        val chartContainer = findViewById<android.widget.FrameLayout?>(R.id.chartContainer)

        if (rvCourses != null && rvQuests != null && chartContainer != null) {
            // 더미 데이터
            val courses = listOf(
                CourseItem("컴활 학습", 50, 30)
            )
            val quests = mutableListOf(
                QuestItem("일일 학습 30분", 30, false),
                QuestItem("복습하기", 20, false)
            )

            rvCourses.layoutManager = LinearLayoutManager(this)
            rvQuests.layoutManager  = LinearLayoutManager(this)

            rvCourses.adapter = CourseAdapter(
                items = courses,
                onStartClick = {
                    // 학습하기 버튼 → 퀴즈 화면(QuizActivity) 전환
                    startActivity(Intent(this, QuizActivity::class.java))
                },
                onCardClick = { /* Optional: 상세 이동 */ },
                onReviewClick = { /* Optional: 복습 화면 */ }
            )
            rvQuests.adapter = QuestAdapter(quests)

            val chartView = WeeklyBarChartView(this).apply {
                values = listOf(12, 16, 9, 20, 13, 17, 26)
                setPadding(24, 12, 24, 24)
            }
            chartContainer.addView(chartView)
        } else {
            // 만약 여기로 들어온다면 activity_main.xml에 rvCourses/rvQuests/chartContainer가 없다는 뜻.
            // A 방식 유지하려면 activity_main.xml에 해당 뷰들을 추가해야 하고,
            // B 방식(프래그먼트 전환)으로 갈 거면 이 블록 자체를 지우고 HomeFragment로 옮겨야 해.
        }
    }
}