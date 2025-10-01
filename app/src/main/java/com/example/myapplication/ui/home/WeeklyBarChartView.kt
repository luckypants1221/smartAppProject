package com.example.myapplication.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class WeeklyBarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF6A5AE0.toInt() // 보라 톤 (원하는 색으로)
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x22000000.toInt() // 아주 연한 검정(투명)
        strokeWidth = 2f
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x66000000.toInt() // 반투명 라벨
        textSize = 28f
    }

    var values: List<Int> = emptyList()
        set(v) {
            field = v
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (values.isEmpty()) return

        val padH = paddingLeft + paddingRight
        val padV = paddingTop + paddingBottom
        val w = width - padH
        val h = height - padV

        // 그리드 가이드(바닥선)
        canvas.drawLine(
            paddingLeft.toFloat(),
            (height - paddingBottom - 1).toFloat(),
            (width - paddingRight).toFloat(),
            (height - paddingBottom - 1).toFloat(),
            gridPaint
        )

        val count = values.size
        val maxV = max(1, values.maxOrNull() ?: 1)
        val barSpace = w / (count * 1.8f) // 간격
        val barWidth = barSpace
        var x = paddingLeft + barSpace / 2

        values.forEachIndexed { idx, v ->
            val ratio = v.toFloat() / maxV
            val barH = ratio * (h * 0.8f) // 위쪽 20% 여백
            val left = x
            val top = (height - paddingBottom) - barH
            val right = x + barWidth
            val bottom = (height - paddingBottom).toFloat()

            canvas.drawRoundRect(
                left, top, right, bottom,
                barWidth / 3f, barWidth / 3f, barPaint
            )

            // 요일 라벨(일~토)
            val day = "일월화수목금토"[idx % 7].toString()
            val textW = labelPaint.measureText(day)
            canvas.drawText(
                day,
                left + (barWidth - textW) / 2f,
                bottom + labelPaint.textSize + 6f,
                labelPaint
            )

            x += (barWidth + barSpace)
        }
    }
}