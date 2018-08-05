package com.fungo.banner.pager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import java.util.*


class CustomViewPager : ViewPager {

    private val childCenterXAbs = ArrayList<Int>()
    private val childIndex = SparseIntArray()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        clipToPadding = false
        overScrollMode = View.OVER_SCROLL_NEVER
    }


    /**
     * @param childCount
     * @param n
     * @return 第n个位置的child 的绘制索引
     */
    override fun getChildDrawingOrder(childCount: Int, n: Int): Int {
        if (n == 0 || childIndex.size() != childCount) {
            childCenterXAbs.clear()
            childIndex.clear()
            val viewCenterX = getViewCenterX(this)
            for (i in 0 until childCount) {
                var indexAbs = Math.abs(viewCenterX - getViewCenterX(getChildAt(i)))
                // 两个距离相同，后来的那个做自增，从而保持abs不同
                ++indexAbs
                childCenterXAbs.add(indexAbs)
                childIndex.append(indexAbs, i)
            }
            childCenterXAbs.sort()//1,0,2  0,1,2
        }
        // 那个item距离中心点远一些，就先draw它。（最近的就是中间放大的item,最后draw）
        return childIndex.get(childCenterXAbs[childCount - 1 - n])
    }

    private fun getViewCenterX(view: View): Int {
        val array = IntArray(2)
        view.getLocationOnScreen(array)
        return array[0] + view.width / 2
    }


    /**
     * 避免多点触摸崩溃
     */
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }

        return false
    }

    /**
     * 解决ViewPager左右滑动与父容器上下滑动冲突的问题
     * 加上try避免多点触摸崩溃
     */
    private var lastX = 0
    private var lastY = 0
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        try {
            val x = ev.rawX.toInt()
            val y = ev.rawY.toInt()
            var dealtX = 0
            var dealtY = 0

            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 保证子View能够接收到Action_move事件
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    dealtX += Math.abs(x - lastX)
                    dealtY += Math.abs(y - lastY)
                    // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                    if (dealtX >= dealtY) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                    lastX = x
                    lastY = y
                }
                MotionEvent.ACTION_CANCEL -> {
                }
                MotionEvent.ACTION_UP -> {
                }
            }
            return super.dispatchTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

}
