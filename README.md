# [BannerView(轮播图控件)](https://github.com/PingerOne/BannerView)
[![](https://img.shields.io/badge/download-apk-yellow.svg)](https://www.pgyer.com/apiv2/app/install?appKey=9433e2bd2db02dbe4da8f2a97c7bf0cd&_api_key=c82f0298d6616c3dc3a4ac02c7919399) [![](https://img.shields.io/badge/release-v1.0.0-orange.svg)](https://github.com/PingerOne/BannerView/releases) [![](https://www.jitpack.io/v/PingerOne/BannerView.svg)](https://www.jitpack.io/#PingerOne/BannerView) [![](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/PingerOne/BannerView/blob/master/LICENSE) [![](https://img.shields.io/badge/简书-笑说余生-red.svg)](https://www.jianshu.com/u/64f479a1cef7)



轮播图控件，封装ViewPager，支持无限循环轮播，支持三种常用页面特效，支持设置指示器，支持自动切换手动滑动和自动滑动的滑动时长，封装Banner的Holder实现更加简单。使用Kotlin开发，在项目中使用，满足大部分Banner相关需求，可以直接使用。

本项目基于[MZBannerView](https://github.com/pinguo-zhouwei/MZBannerView)进行二次开发，只用于开源交流，如果侵权等问题请及时提醒。


## 使用方法

1. 在项目根目录的build.gradle文件中添加jitpack仓库

        allprojects {
            repositories {
                maven { url 'https://jitpack.io' }
            }
        }

2. 在application的build.gradle文件中引入仓库依赖

        dependencies {
             implementation 'com.github.PingerOne:BannerView:1.0.5'
        }

3. 在xml文件中引用BannerView控件

        <com.fungo.banner.BannerView
            android:id="@+id/bannerView"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            app:bannerAutoLoop="true"
            app:bannerPageMode="cover"
            app:bannerPageScale="0.9"
            app:bannerPageAlpha="0.6"
            app:bannerFarMargin="10dp"
            app:bannerCoverMargin="10dp"
            app:bannerPagePadding="20dp"
            app:indicatorVisible="true"
            app:indicatorAlign="right"
            app:indicatorPaddingLeft="12dp"
            app:indicatorPaddingBottom="12dp"
            app:indicatorPaddingRight="12dp"/>

4. 在代码中设置数据和适配器

        bannerView.setPages(data, object : BannerHolderCreator<BannerBean, BannerHolder> {
             override fun onCreateBannerHolder(): BannerHolder {
                 return BannerHolder()
             }
         })



## 常用属性
| Name | Format | Description |
| :- | :-| :- |
| bannerAutoLoop| Boolean | 是否开启自动轮播 |
| bannerPageMode| Int | 页面模式 |
| bannerPageScale| Float | 左右页面的缩放比例 |
| bannerPageAlpha| Float | 左右页面的透明度 |
| bannerFarMargin| Dimension | 远离模式下左右页面的外边距 |
| bannerCoverMargin| Dimension | 覆盖模式下左右页面的内边距 |
| bannerPagePadding| Dimension | 中间页面距离左右的距离 |
| indicatorVisible| Boolean | 指示器是否可见 |
| indicatorAlign| Int | 指示器的位置 |
| indicatorPaddingLeft| Int | 指示器距离左侧的距离 |
| indicatorPaddingRight| Int | 指示器距离右侧的边距 |
| indicatorPaddingTop| Int | 指示器距离顶部的边距 |
| indicatorPaddingBottom| Int | 指示器距离底部的边距 |


### 参考
* [仿魅族应用的广告BannerView](https://jianshu.com/p/653680cfe877)
* [巧用ViewPager 打造不一样的广告轮播切换效果](https://blog.csdn.net/lmj623565791/article/details/51339751)

---
> 欢迎大家访问我的[简书](http://www.jianshu.com/u/64f479a1cef7)，[博客](http://wanit.me/)和[GitHub](https://github.com/PingerOne)。
