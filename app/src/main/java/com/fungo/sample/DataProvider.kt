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
                        "http://t2.hddhhn.com/uploads/tu/201309/036/1.jpg"
                ),

                BannerBean(
                        "未经审视的人生不值得度过",
                        "http://t2.hddhhn.com/uploads/tu/201710/9999/838147547c.jpg"
                ),
                BannerBean(
                        "真正的朋友，是一个灵魂孕育在两个躯体里",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533406646890&di=9386821975d18ac51aa9e63082fb04ba&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20161028%2F066d572c4d664d48a032275da69983ba.gif"
                ),
                BannerBean(
                        "人生而自由，却无往不再枷锁中",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533387100616&di=113ab7c5c71aa43dfdb5948cc7c3aac9&imgtype=0&src=http%3A%2F%2Fimg3.cache.netease.com%2Fphoto%2F0003%2F2015-03-20%2F600x450_AL5EDPNI00AJ0003.jpg"
                ),
                BannerBean(
                        "权利的相互转让就是人们所谓的契约",
                        "https://img.zcool.cn/community/016fba58438350a8012060c889b4d6.jpg@1280w_1l_2o_100sh.webp"
                ),
                BannerBean(
                        "要么庸俗，要么孤独",
                        "http://ww2.sinaimg.cn/mw690/006wfVgagw1f5lksuha76g30dw07thd2.gif"
                )
        )
    }


    /**
     * 获取本地数据
     */
    fun getLocalBannerData(): ArrayList<BannerBean> {
        return arrayListOf(
                BannerBean("一个女人一生的戏——葬花戏", "", R.mipmap.banner_item1),
                BannerBean("最牢固的感情，大都势均力敌", "", R.mipmap.banner_item2),
                BannerBean("承认自己软弱比故作坚强有用的多", "", R.mipmap.banner_item3),
                BannerBean("如果有一天，你来到我的城市", "", R.mipmap.banner_item4),
                BannerBean("多少红颜爱傻逼，多少傻逼不珍惜", "", R.mipmap.banner_item5))
    }
}