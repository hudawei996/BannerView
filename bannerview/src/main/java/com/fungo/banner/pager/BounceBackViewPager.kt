package com.fungo.banner.pager

import android.content.Context
import android.graphics.Rect
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import android.view.animation.TranslateAnimation
import java.util.*


/**
 * 重写ViewPager,支持越界回弹，修复多点触控崩溃，解决左右页面和上下滑动的冲突
 */
class BounceBackViewPager : ViewPager {

    private val childCenterXAbs = ArrayList<Int>()
    private val childIndex = SparseIntArray()
    private var currentPosition = 0
    private var preX = 0f
    private var handleDefault = true
    private val mRect = Rect()           // 用来记录初始位置

    companion object {
        private const val RATIO = 0.6f             // 页面距离左边的比例，摩擦系数
        private const val SCROLL_WIDTH = 10f       // 手指滑动的距离节点
    }


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
                    // 滑动冲突处理
                    dealtX += Math.abs(x - lastX)
                    dealtY += Math.abs(y - lastY)
                    // 这里是够拦截的判断依据是左右滑动
                    if (dealtX >= dealtY) {
                        parent.requestDisallowInterceptTouchEvent(false)
                    } else {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    lastX = x
                    lastY = y


                    // 回弹设置
                    if (adapter!!.count == 1) {
                        val nowX = ev.x
                        val offset = nowX - preX
                        preX = nowX
                        // 手指滑动的距离大于设定值
                        if (offset > SCROLL_WIDTH) {
                            whetherConditionIsRight(offset)
                        } else if (offset < -SCROLL_WIDTH) {
                            whetherConditionIsRight(offset)
                            // 这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
                        } else if (!handleDefault) {
                            if (left + (offset * RATIO).toInt() != mRect.left) {
                                layout(left + (offset * RATIO).toInt(), top, right + (offset * RATIO).toInt(), bottom)
                            }
                        }
                    } else if (currentPosition == 0 || currentPosition == adapter!!.count - 1) {
                        val nowX = ev.x
                        val offset = nowX - preX
                        preX = nowX

                        if (currentPosition == 0) {
                            if (offset > SCROLL_WIDTH) {
                                whetherConditionIsRight(offset)
                            } else if (!handleDefault) {
                                if (left + (offset * RATIO).toInt() >= mRect.left) {
                                    layout(left + (offset * RATIO).toInt(), top, right + (offset * RATIO).toInt(), bottom)
                                }
                            }
                        } else {
                            if (offset < -SCROLL_WIDTH) {
                                whetherConditionIsRight(offset)
                            } else if (!handleDefault) {
                                if (right + (offset * RATIO).toInt() <= mRect.right) {
                                    layout(left + (offset * RATIO).toInt(), top, right + (offset * RATIO).toInt(), bottom)
                                }
                            }
                        }
                    } else {
                        handleDefault = true
                    }

                    if (!handleDefault) {
                        return true
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                }
                MotionEvent.ACTION_UP -> {
                    onTouchActionUp()
                }
            }
            return super.dispatchTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            // 记录起点
            preX = ev.x
            currentPosition = currentItem
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun whetherConditionIsRight(offset: Float) {
        if (mRect.isEmpty) {
            mRect.set(left, top, right, bottom)
        }
        handleDefault = false
        layout(left + (offset * RATIO).toInt(), top, right + (offset * RATIO).toInt(), bottom)
    }

    private fun onTouchActionUp() {
        if (!mRect.isEmpty) {
            recoveryPosition()
        }
    }

    private fun recoveryPosition() {
        val ta = TranslateAnimation(left.toFloat(), mRect.left.toFloat(), 0f, 0f)
        ta.duration = 300
        startAnimation(ta)
        layout(mRect.left, mRect.top, mRect.right, mRect.bottom)
        mRect.setEmpty()
        handleDefault = true
    }

}
