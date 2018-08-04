package com.fungo.banner.transformer

import android.support.v4.view.ViewPager
import android.view.View

/**
 * @author Pinger
 * @since 18-7-21 上午11:06
 *
 * 左右两边缩小，滑动时中间会重叠的特效
 * @param viewPager 设置特效的ViewPager对象
 * @param coverWidth　左右页面覆盖中间页面的边距
 * @param pagePadding 中间页面的左右padding值
 * @param pageScale　左右页面高度的缩放比例
 */
class CoverModeTransformer(private val viewPager: ViewPager, private var coverMargin: Int, private var pagePadding: Int, private var pageScale: Float, private var pageAlpha: Float) : ViewPager.PageTransformer {

    private var reduceX = 0.0f
    private var itemWidth = 0f
    private var offsetPosition = 0f

    override fun transformPage(view: View, position: Float) {
        if (offsetPosition == 0f) {
            val width = viewPager.measuredWidth.toFloat()
            offsetPosition = pagePadding / (width - pagePadding - pagePadding)
        }
        val currentPos = position - offsetPosition
        if (itemWidth == 0f) {
            itemWidth = view.width.toFloat()
            //由于左右边的缩小而减小的x的大小的一半
            reduceX = (1.0f - pageScale) * itemWidth / 2.0f
        }
        when {
            currentPos <= -1.0f -> {
                view.translationX = reduceX + coverMargin
                view.scaleX = pageScale
                view.scaleY = pageScale
            }
            currentPos <= 1.0 -> {
                val scale = (1.0f - pageScale) * Math.abs(1.0f - Math.abs(currentPos))
                val translationX = currentPos * -reduceX
                when {
                    currentPos <= -0.5 -> //两个view中间的临界，这时两个view在同一层，左侧View需要往X轴正方向移动覆盖的值()
                        view.translationX = translationX + coverMargin * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f
                    currentPos <= 0.0f -> view.translationX = translationX
                    currentPos >= 0.5 -> //两个view中间的临界，这时两个view在同一层
                        view.translationX = translationX - coverMargin * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f
                    else -> view.translationX = translationX
                }
                view.scaleX = scale + pageScale
                view.scaleY = scale + pageScale
            }
            else -> {
                view.scaleX = pageScale
                view.scaleY = pageScale
                view.translationX = -reduceX - coverMargin
            }
        }

        val alpha = if (position < 0)
            (1 - pageAlpha) * position + 1
        else
            (pageAlpha - 1) * position + 1
        view.alpha = Math.abs(alpha)
    }
}

