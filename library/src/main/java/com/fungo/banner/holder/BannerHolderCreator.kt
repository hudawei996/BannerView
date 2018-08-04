package com.fungo.banner.holder

/**
 * @author Pinger
 * @since 18-7-20 下午15:52
 *
 * Banner条目的Holder，提供创建视图和绑定数据的接口
 * 在BannerView的setPages方法里实现该接口，并且实现onCreateBannerHolder
 * 创建BannerView的Holder，一般不会有多种数据类型，有的话也会统一到一个Bean里
 */
interface BannerHolderCreator<T, out BH : BaseBannerHolder<T>> {


    /**
     * 创建Banner的Holder
     * 生成了[BaseBannerHolder]后，可以继续使用接口的方法对holder进行视图初始化和绑定数据
     * @return 返回Holder对象
     */
    fun onCreateBannerHolder(): BH
}
