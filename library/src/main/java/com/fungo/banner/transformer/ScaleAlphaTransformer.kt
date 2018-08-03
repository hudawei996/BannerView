package com.fungo.banner.transformer

import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.view.View

/**
 * @author Pinger
 * @since 18-7-21 上午9:11
 *
 * 左右按一定比例缩放和渐变的Transformer
 */
class ScaleAlphaTransformer(private var scaleValue: Float, private var alphaValue: Float) : ViewPager.PageTransformer {


    override fun transformPage(page: View, position: Float) {
        // 不同位置的缩放和透明度
        val scale = if (position < 0)
            (1 - scaleValue) * position + 1
        else
            (scaleValue - 1) * position + 1
        val alpha = if (position < 0)
            (1 - alphaValue) * position + 1
        else
            (alphaValue - 1) * position + 1
        // 保持左右两边的图片位置中心
        if (position < 0) {
            ViewCompat.setPivotX(page, page.width.toFloat())
            ViewCompat.setPivotY(page, (page.height / 2).toFloat())
        } else {
            ViewCompat.setPivotX(page, 0f)
            ViewCompat.setPivotY(page, (page.height / 2).toFloat())
        }
        ViewCompat.setScaleX(page, scale)
        ViewCompat.setScaleY(page, scale)
        ViewCompat.setAlpha(page, Math.abs(alpha))
    }
}
