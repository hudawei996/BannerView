package com.fungo.banner.holder

import android.view.View

/**
 * @author Pinger
 * @since 18-7-20 下午16:22
 *
 * Banner条目的Holder，提供创建视图和绑定数据的接口
 * 在BannerView的内部类BannerPagerAdapter的getView方法里会获取到Holder对象
 * 然后分别回调getHolderResId()和onBindData()方法
 */

interface BaseBannerHolder<in T> {


    /**
     * 获取Holder视图的资源id
     * @return　资源id
     */
    fun getHolderResId(): Int

    /**
     * 绑定数据
     * @param itemView Holder的视图
     * @param data Holder的数据
     */
    fun onBindData(itemView: View, data: T)


    /**
     * Holder的点击回调
     * 如果需要实现Holder的点击，只需要重写这个方法就好
     * 如果不需要点击，就不要理会
     */
    fun onPageClick(itemView: View, position: Int, data: T) {

    }

}
