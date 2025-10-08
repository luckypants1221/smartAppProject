package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.quiz.QuizFragment
import com.example.myapplication.ui.home.CourseAdapter
import com.example.myapplication.ui.home.CourseItem
import com.example.myapplication.ui.home.QuestAdapter
import com.example.myapplication.ui.home.QuestItem import com.example.myapplication.ui.home.WeeklyBarChartView

class HomeFragment : Fragment() { private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 데이터
        val courses = listOf(
            CourseItem("컴활 학습", 50, 30)
        )
        val quests = mutableListOf(
            QuestItem("일일 학습 30분", 30, false),
            QuestItem("복습하기", 20, false)
        )

        // 리스트 세팅
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvQuests.layoutManager = LinearLayoutManager(requireContext())

        binding.rvCourses.adapter = CourseAdapter(
            items = courses,
            onStartClick = { startActivity( android.content.Intent( requireContext(),
                com.example.myapplication.ui.quiz.QuizActivity::class.java ) ) }
            ,
            onCardClick = { /* 필요 시 */ },
            onReviewClick = { /* 필요 시 */ }
        )
        binding.rvQuests.adapter = QuestAdapter(quests)

        // 차트 추가
        val chart = WeeklyBarChartView(requireContext()).apply {
            values = listOf(12, 16, 9, 20, 13, 17, 26)
            setPadding(24, 12, 24, 24)
        }
        (binding.chartContainer as FrameLayout).addView(chart)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


