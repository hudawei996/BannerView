package com.fungo.banner.transformer

import android.support.v4.view.ViewPager
import android.view.View

/**
 * @author Pinger
 * @since 18-7-21 上午11:06
 *
 * 左右两边缩小，滑动时中间会重叠的特效
 */
class CoverModeTransformer(private val viewPager: ViewPager, private var coverWidth: Int, private var coverPadding: Int, private var pageScale: Float) : ViewPager.PageTransformer {

    private var reduceX = 0.0f
    private var itemWidth = 0f
    private var offsetPosition = 0f

    override fun transformPage(view: View, position: Float) {
        if (offsetPosition == 0f) {
            val width = viewPager.measuredWidth.toFloat()
            offsetPosition = coverPadding / (width - coverPadding - coverPadding)
        }
        val currentPos = position - offsetPosition
        if (itemWidth == 0f) {
            itemWidth = view.width.toFloat()
            //由于左右边的缩小而减小的x的大小的一半
            reduceX = (1.0f - pageScale) * itemWidth / 2.0f
        }
        when {
            currentPos <= -1.0f -> {
                view.translationX = reduceX + coverWidth
                view.scaleX = pageScale
                view.scaleY = pageScale
            }
            currentPos <= 1.0 -> {
                val scale = (1.0f - pageScale) * Math.abs(1.0f - Math.abs(currentPos))
                val translationX = currentPos * -reduceX
                when {
                    currentPos <= -0.5 -> //两个view中间的临界，这时两个view在同一层，左侧View需要往X轴正方向移动覆盖的值()
                        view.translationX = translationX + coverWidth * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f
                    currentPos <= 0.0f -> view.translationX = translationX
                    currentPos >= 0.5 -> //两个view中间的临界，这时两个view在同一层
                        view.translationX = translationX - coverWidth * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f
                    else -> view.translationX = translationX
                }
                view.scaleX = scale + pageScale
                view.scaleY = scale + pageScale
            }
            else -> {
                view.scaleX = pageScale
                view.scaleY = pageScale
                view.translationX = -reduceX - coverWidth
            }
        }

    }
}

