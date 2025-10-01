package com.example.myapplication.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

data class QuestItem(
    val title: String,
    val reward: Int,
    var completed: Boolean
)

class QuestAdapter(
    private val items: MutableList<QuestItem>
) : RecyclerView.Adapter<QuestAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvQuestTitle)
        val tvReward: TextView = view.findViewById(R.id.tvReward)
        val cbDone: CheckBox = view.findViewById(R.id.cbDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_quest, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title
        holder.tvReward.text = "+${item.reward}P"

        // 리스너 중복 호출 방지
        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = item.completed

        holder.cbDone.setOnCheckedChangeListener { _, checked ->
            val pos = holder.absoluteAdapterPosition // 또는 holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                items[pos].completed = checked
                // 필요하면 변경 반영
                // notifyItemChanged(pos)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}