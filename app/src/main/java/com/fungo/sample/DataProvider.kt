package com.fungo.sample

import com.fungo.sample.banner.BannerBean

/**
 * @author Pinger
 * @since 18-8-4 下午5:55
 * 提供Banner数据
 */

object DataProvider {


    /**
     * 获取网络数据
     */
    fun getWebBannerData(): ArrayList<BannerBean> {
        return arrayListOf(
                BannerBean(
                        "只有那些从不仰望星空的人，才不会跌入坑中",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533386965252&di=ed239b5191a06831e55bee07d0c4283c&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0105455608951732f875a132b93e14.jpg%401280w_1l_2o_100sh.jpg"
                ),

                BannerBean(
                        "未经审视的人生不值得度过",
                        "https://img.zcool.cn/community/010ab1560893e36ac7251df84572b4.jpg@2o.jpg"
                ),
                BannerBean(
                        "真正的朋友，是一个灵魂孕育在两个躯体里",
                        "https://img.zcool.cn/community/011d73560893da6ac7251df8c02bdf.jpg@2o.jpg"
                ),
                BannerBean(
                        "人生而自由，却无往不再枷锁中",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533387100616&di=113ab7c5c71aa43dfdb5948cc7c3aac9&imgtype=0&src=http%3A%2F%2Fimg3.cache.netease.com%2Fphoto%2F0003%2F2015-03-20%2F600x450_AL5EDPNI00AJ0003.jpg"
                ),
                BannerBean(
                        "权利的相互转让就是人们所谓的契约",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533387100619&di=71954c490feb95c39b99fa882cd1aa95&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0121bb5608950d32f875a132611ce7.jpg%401280w_1l_2o_100sh.jpg"
                ),
                BannerBean(
                        "要么庸俗，要么孤独",
                        "http://img1qn.moko.cc/2016-07-26/79726bdb-2247-4db6-ac16-6d170dc48982.jpg"
                )
        )
    }


    /**
     * 获取本地数据
     */
    fun getLocalBannerData(): ArrayList<BannerBean> {
        return arrayListOf(
                BannerBean(
                        "只有那些从不仰望星空的人，才不会跌入坑中",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533386965252&di=ed239b5191a06831e55bee07d0c4283c&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0105455608951732f875a132b93e14.jpg%401280w_1l_2o_100sh.jpg"
                )

        )
    }
}