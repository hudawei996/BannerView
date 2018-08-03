package com.fungo.banner.holder

/**
 * @author Pinger
 * @since 18-7-20 下午15:52
 *
 * Banner条目的Holder，提供创建视图和绑定数据的接口
 */
interface BannerHolderCreator<out BH : BaseBannerHolder<*>> {


    /**
     * 创建Banner的Holder
     * 生成了[BaseBannerHolder]后，可以继续使用接口的方法对holder进行视图初始化和绑定数据
     * @return 返回Holder
     */
    fun onCreateBannerHolder(): BH
}
