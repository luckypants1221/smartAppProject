package com.example.myapplication.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

data class CourseItem(
    val title: String,
    val dailyGoal: Int,
    val progress: Int
)

class CourseAdapter(
    private val items: List<CourseItem>,
    private val onStartClick: (CourseItem) -> Unit = {},
    private val onCardClick: (CourseItem) -> Unit = {},
    private val onReviewClick: (CourseItem) -> Unit = {}
) : RecyclerView.Adapter<CourseAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val goal: TextView = view.findViewById(R.id.tvGoal)
        val percent: TextView = view.findViewById(R.id.tvPercent)
        val circle: CircleProgressView = view.findViewById(R.id.circleProgress)
        val btnStart: View = view.findViewById(R.id.btnStart)

        init {
            view.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onCardClick(items[pos])
            }
            btnStart.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onStartClick(items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.goal.text = "하루 목표: ${item.dailyGoal}개"
        holder.percent.text = "${item.progress}%"
        holder.circle.progress = item.progress
    }

    override fun getItemCount(): Int = items.size
}