package com.fungo.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fungo.banner.BannerView
import com.fungo.banner.holder.BannerHolderCreator
import com.fungo.sample.banner.BannerBean
import com.fungo.sample.banner.BannerHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mBannerView: BannerView<BannerBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBannerView = findViewById(R.id.bannerView)

        initEvent()
    }

    private fun initEvent() {

        rgDataFrom.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbLocal -> updateBanner(DataProvider.getWebBannerData())
                else -> updateBanner(DataProvider.getWebBannerData())
            }

        }

        rgPageMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCover -> mBannerView?.setPageMode(BannerView.PageMode.COVER)
                R.id.rbFar -> mBannerView?.setPageMode(BannerView.PageMode.FAR)
                else -> mBannerView?.setPageMode(BannerView.PageMode.NORMAL)
            }
        }


        rgIndicatorAlign.setOnCheckedChangeListener { _, checkedId ->
            mBannerView?.setIndicatorVisible(true)
            when (checkedId) {
                R.id.rbLeft -> mBannerView?.setIndicatorAlign(BannerView.IndicatorAlign.LEFT)
                R.id.rbRight -> mBannerView?.setIndicatorAlign(BannerView.IndicatorAlign.RIGHT)
                R.id.rbCenter -> mBannerView?.setIndicatorAlign(BannerView.IndicatorAlign.CENTER)
                else -> mBannerView?.setIndicatorVisible(false)
            }
        }


        rbNormal.isChecked = true
        rbRight.isChecked = true
    }


    /**
     * 更新BannerView
     */
    private fun updateBanner(data: ArrayList<BannerBean>) {
        mBannerView?.setPages(data, object : BannerHolderCreator<BannerBean, BannerHolder> {
            override fun onCreateBannerHolder(): BannerHolder {
                return BannerHolder(mBannerView?.getPageMode() == BannerView.PageMode.NORMAL)
            }
        })
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

