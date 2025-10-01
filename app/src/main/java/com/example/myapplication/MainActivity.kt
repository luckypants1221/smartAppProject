package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.home.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawer = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        // 툴바 우측 아이콘 클릭 → 사이드 열기
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.action_open_side) {
                drawer.openDrawer(GravityCompat.END)
                true
            } else false
        }

        // 사이드 메뉴 클릭 처리
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_login -> {
                    // TODO: 로그인 화면 이동
                }
                R.id.action_logout -> {
                    // TODO: 로그아웃 처리
                }
                R.id.action_monthly_study -> {
                    // TODO: "이번달 학습량" 화면/다이얼로그
                }
                R.id.action_wrong_notes -> {
                    // TODO: 오답노트 화면 이동
                }
            }
            drawer.closeDrawer(GravityCompat.END)
            true
        }

        // 아래는 네 기존 홈 화면 세팅(예: 코스/퀘스트/차트)
        val courses = listOf(
            CourseItem("컴활 학습", 50, 30)
        )
        val quests = mutableListOf(
            QuestItem("일일 학습 30분", 30, false),
            QuestItem("복습하기", 20, false)
        )

        val rvCourses = findViewById<RecyclerView>(R.id.rvCourses)
        val rvQuests  = findViewById<RecyclerView>(R.id.rvQuests)

        rvCourses.layoutManager = LinearLayoutManager(this)
        rvQuests.layoutManager  = LinearLayoutManager(this)

        rvCourses.adapter = CourseAdapter(
            items = courses,
            onStartClick = { /* 컴활 학습 시작 */ },
            onCardClick = { /* 상세 이동(Optional) */ },
            onReviewClick = { /* 복습 화면 진입 */ }
        )
        rvQuests.adapter = QuestAdapter(quests)

        val chartContainer = findViewById<FrameLayout>(R.id.chartContainer)
        val chartView = WeeklyBarChartView(this).apply {
            values = listOf(12, 16, 9, 20, 13, 17, 26)
            setPadding(24, 12, 24, 24)
        }
        chartContainer.addView(chartView)
    }
}