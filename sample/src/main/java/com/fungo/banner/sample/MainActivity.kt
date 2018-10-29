package com.fungo.banner.sample

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.fungo.banner.BannerView
import com.fungo.banner.holder.BannerHolderCreator
import com.fungo.banner.sample.holder.BannerBean
import com.fungo.banner.sample.holder.BannerHolder
import com.fungo.imagego.strategy.ImageGoEngine
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mBannerView: BannerView<BannerBean>? = null
    private var mDatas: ArrayList<BannerBean>? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ImageGoEngine.setAutoGif(true)

        mBannerView = findViewById(R.id.bannerView)

        initSwipe()
        initEvent()
    }

    private fun initSwipe() {
        swipeRefreshLayout.setOnRefreshListener {
            mHandler.postDelayed({
                swipeRefreshLayout.isRefreshing = false
                rbWeb.isChecked = true
                rbNormal.isChecked = true
                rbHide.isChecked = true
            }, 1200)
        }


    }

    private fun initEvent() {
        rgDataFrom.setOnCheckedChangeListener { _, checkedId ->
            mDatas = when (checkedId) {
                R.id.rbLocal -> DataProvider.getLocalBannerData()
                else -> DataProvider.getWebBannerData()
            }
            updateBanner(mDatas)
        }

        rgPageMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCover -> mBannerView?.setPageMode(BannerView.PageMode.COVER)
                R.id.rbFar -> mBannerView?.setPageMode(BannerView.PageMode.FAR)
                else -> mBannerView?.setPageMode(BannerView.PageMode.NORMAL)
            }
            updateBanner(mDatas)
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

        rbWeb.isChecked = true
        rbNormal.isChecked = true
        rbHide.isChecked = true
    }


    /**
     * 更新BannerView
     */
    private fun updateBanner(data: ArrayList<BannerBean>?) {
        mBannerView?.setAutoLoop(data!!.size > 2)
        if (data!!.size < 2) {
            rbHide.isChecked = true
        }
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

