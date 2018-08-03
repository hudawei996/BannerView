package com.fungo.banner.holder

import android.view.View

/**
 * @author Pinger
 * @since 18-7-20 下午16:22
 *
 * Banner条目的Holder，提供创建视图和绑定数据的接口
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


}
