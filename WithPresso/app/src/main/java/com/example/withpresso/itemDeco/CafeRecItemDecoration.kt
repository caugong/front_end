package com.example.withpresso.itemDeco

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class CafeRecItemDecoration(context: Context) : ItemDecoration() {
    private val size10: Int
    private val size5: Int

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        //상하 설정
        if (position == 0 || position == 1) {
            // 첫번 째 줄 아이템
            outRect.top = size10
            outRect.bottom = size10
        } else {
            outRect.bottom = size10
        }

        // spanIndex = 0 -> 왼쪽
        // spanIndex = 1 -> 오른쪽
        val lp =
            view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = lp.spanIndex
        if (spanIndex == 0) {
            //왼쪽 아이템
            outRect.left = size10
            outRect.right = size5
        } else if (spanIndex == 1) {
            //오른쪽 아이템
            outRect.left = size5
            outRect.right = size10
        }
        outRect.top = size5
        outRect.right = size5
        outRect.bottom = size5
        outRect.left = size5
    }

    // dp -> pixel 단위로 변경
    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    init {
        size10 = dpToPx(context, 10)
        size5 = dpToPx(context, 5)
    }
}