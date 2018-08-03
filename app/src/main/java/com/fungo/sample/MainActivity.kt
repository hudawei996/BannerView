package com.fungo.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.fungo.banner.BannerView
import com.fungo.banner.holder.BaseBannerHolder
import com.fungo.banner.holder.BannerHolderCreator
import com.fungo.imagego.loadImage

class MainActivity : AppCompatActivity() {

    private var mBannerView: BannerView<BannerBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBannerView = findViewById(R.id.bannerView)

        mBannerView?.setBannerPageClickListener(object : BannerView.BannerPageClickListener<BannerBean> {
            override fun onPageClick(view: View, position: Int, data: BannerBean) {
                Toast.makeText(this@MainActivity, data.title, Toast.LENGTH_SHORT).show()
            }
        })


        val data = ArrayList<BannerBean>()
        for (i in 0..5) {
            data.add(BannerBean("我是Banner标题$i", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1532628464134&di=b3aa02630ce090b5773b53fe1b1205b1&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0173eb59080ec0a801214550fd7500.jpg%401280w_1l_2o_100sh.jpg"))
        }

        mBannerView?.setPages(data, object : BannerHolderCreator<BannerHolder> {
            override fun onCreateBannerHolder(): BannerHolder {
                return BannerHolder()
            }
        })
        mBannerView?.start()
    }

    data class BannerBean(var title: String, var url: String)

    inner class BannerHolder : BaseBannerHolder<BannerBean> {

        override fun getHolderResId(): Int {
            return R.layout.holder_banner
        }

        override fun onBindData(itemView: View, data: BannerBean) {
            loadImage(data.url, itemView.findViewById(R.id.imageView))
            itemView.findViewById<TextView>(R.id.textView)?.text = data.title

        }
    }

    override fun onResume() {
        super.onResume()
        mBannerView?.start()
    }


    override fun onPause() {
        super.onPause()
        mBannerView?.pause()
    }

}

