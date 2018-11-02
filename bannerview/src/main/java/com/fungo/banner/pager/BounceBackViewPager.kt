package com.fungo.banner.pager

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Camera
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import java.util.*

/**
 * @author Pinger
 * @since 2018/11/3 1:52
 */

class BounceBackViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private val mOverscrollEffect = OverscrollEffect()
    private val mCamera = Camera()

    private val childCenterXAbs = ArrayList<Int>()
    private val childIndex = SparseIntArray()

    private var mScrollListener: ViewPager.OnPageChangeListener? = null
    private var mLastMotionX: Float = 0.toFloat()
    private var mActivePointerId: Int = 0
    private var mScrollPosition: Int = 0
    private var mScrollPositionOffset: Float = 0.toFloat()
    private val mTouchSlop: Int

    private var overscrollTranslation = DEFAULT_OVERSCROLL_TRANSLATION
    private var overscrollAnimationDuration = DEFAULT_OVERSCROLL_ANIMATION_DURATION

    private var mLastPosition = 0

    /**
     * @author renard, extended by Piotr Zawadzki
     */
    private inner class OverscrollEffect {
        var mOverscroll: Float = 0f
        private var mAnimator: Animator? = null

        val isOverscrolling: Boolean
            get() {
                if (mScrollPosition == 0 && mOverscroll < 0) {
                    return true
                }
                val isLast = adapter!!.count - 1 == mScrollPosition
                return isLast && mOverscroll > 0
            }

        /**
         * @param deltaDistance [0..1] 0->no overscroll, 1>full overscroll
         */
        fun setPull(deltaDistance: Float) {
            mOverscroll = deltaDistance
            invalidateVisibleChilds(mLastPosition)
        }

        /**
         * called when finger is released. starts to animate back to default position
         */
        fun onRelease() {
            if (mAnimator != null && mAnimator!!.isRunning) {
                mAnimator!!.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        startAnimation(0f)
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                })
                mAnimator!!.cancel()
            } else {
                startAnimation(0f)
            }
        }

        private fun startAnimation(target: Float) {
            mAnimator = ObjectAnimator.ofFloat(this, "pull", mOverscroll, target)
            mAnimator!!.interpolator = DecelerateInterpolator()
            val scale = Math.abs(target - mOverscroll)
            mAnimator!!.duration = (overscrollAnimationDuration * scale).toLong()
            mAnimator!!.start()
        }
    }

    init {
        clipChildren = false
        setStaticTransformationsEnabled(true)
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledPagingTouchSlop
        super.addOnPageChangeListener(MyOnPageChangeListener())
    }

    fun setOverscrollTranslation(overscrollTranslation: Float) {
        this.overscrollTranslation = overscrollTranslation
    }

    fun setOverscrollAnimationDuration(overscrollAnimationDuration: Long) {
        this.overscrollAnimationDuration = overscrollAnimationDuration
    }

    override fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mScrollListener = listener
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

    private fun invalidateVisibleChilds(position: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).invalidate()

        }
        //this.invalidate();
        // final View child = getChildAt(position);
        // final View previous = getChildAt(position - 1);
        // final View next = getChildAt(position + 1);
        // if (child != null) {
        // child.invalidate();
        // }
        // if (previous != null) {
        // previous.invalidate();
        // }
        // if (next != null) {
        // next.invalidate();
        // }
    }

    private inner class MyOnPageChangeListener : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (mScrollListener != null) {
                mScrollListener!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
            mScrollPosition = position
            mScrollPositionOffset = positionOffset
            mLastPosition = position
            invalidateVisibleChilds(position)
        }

        override fun onPageSelected(position: Int) {

            if (mScrollListener != null) {
                mScrollListener!!.onPageSelected(position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {

            if (mScrollListener != null) {
                mScrollListener!!.onPageScrollStateChanged(state)
            }
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                mScrollPositionOffset = 0f
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            val action = ev.action and MotionEvent.ACTION_MASK
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mLastMotionX = ev.x
                    mActivePointerId = ev.getPointerId(0)
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    val index = ev.actionIndex
                    mLastMotionX = ev.getX(index)
                    mActivePointerId = ev.getPointerId(index)
                }
            }
            return super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
            return false
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.dispatchTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return false
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
            return false
        }

    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            var callSuper = false
            val action = ev.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    callSuper = true
                    mLastMotionX = ev.x
                    mActivePointerId = ev.getPointerId(0)
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    callSuper = true
                    val index = ev.actionIndex
                    mLastMotionX = ev.getX(index)
                    mActivePointerId = ev.getPointerId(index)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mActivePointerId != INVALID_POINTER_ID) {
                        // Scroll to follow the motion event
                        val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                        val x = ev.getX(activePointerIndex)
                        val deltaX = mLastMotionX - x
                        val oldScrollX = scrollX.toFloat()
                        val width = width
                        val widthWithMargin = width + pageMargin
                        val lastItemIndex = adapter!!.count - 1
                        val currentItemIndex = currentItem
                        val leftBound = Math.max(0, (currentItemIndex - 1) * widthWithMargin).toFloat()
                        val rightBound = (Math.min(currentItemIndex + 1, lastItemIndex) * widthWithMargin).toFloat()
                        val scrollX = oldScrollX + deltaX
                        if (mScrollPositionOffset == 0f) {
                            if (scrollX < leftBound) {
                                if (leftBound == 0f) {
                                    val over = deltaX + mTouchSlop
                                    mOverscrollEffect.setPull(over / width)
                                }
                            } else if (scrollX > rightBound) {
                                if (rightBound == (lastItemIndex * widthWithMargin).toFloat()) {
                                    val over = scrollX - rightBound - mTouchSlop.toFloat()
                                    mOverscrollEffect.setPull(over / width)
                                }
                            }
                        } else {
                            mLastMotionX = x
                        }
                    } else {
                        mOverscrollEffect.onRelease()
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    callSuper = true
                    mActivePointerId = INVALID_POINTER_ID
                    mOverscrollEffect.onRelease()
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val pointerIndex = ev.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                    val pointerId = ev.getPointerId(pointerIndex)
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        val newPointerIndex = if (pointerIndex == 0) 1 else 0
                        mLastMotionX = ev.getX(newPointerIndex)
                        mActivePointerId = ev.getPointerId(newPointerIndex)
                        callSuper = true
                    }
                }
            }

            return if (mOverscrollEffect.isOverscrolling && !callSuper) {
                true
            } else {
                super.onTouchEvent(ev)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun getChildStaticTransformation(child: View, t: Transformation): Boolean {
        if (child.width == 0) {
            return false
        }
        val position = child.left / child.width
        val isFirstOrLast = position == 0 || position == adapter!!.count - 1
        if (mOverscrollEffect.isOverscrolling && isFirstOrLast) {
            val dx = (width / 2).toFloat()
            val dy = height / 2
            t.matrix.reset()
            val translateX = overscrollTranslation * if (mOverscrollEffect.mOverscroll > 0) Math.min(mOverscrollEffect.mOverscroll, 1f) else Math.max(mOverscrollEffect.mOverscroll, -1f)
            mCamera.save()
            mCamera.translate(-translateX, 0f, 0f)
            mCamera.getMatrix(t.matrix)
            mCamera.restore()
            t.matrix.preTranslate(-dx, (-dy).toFloat())
            t.matrix.postTranslate(dx, dy.toFloat())

            if (childCount == 1) {
                this.invalidate()
            } else {
                child.invalidate()
            }
            return true
        }
        return false
    }

    companion object {

        /**
         * maximum z distance to translate child view
         */
        private const val DEFAULT_OVERSCROLL_TRANSLATION = 600f

        /**
         * duration of overscroll animation in ms
         */
        private const val DEFAULT_OVERSCROLL_ANIMATION_DURATION = 400L

        private const val INVALID_POINTER_ID = -1
    }
}
