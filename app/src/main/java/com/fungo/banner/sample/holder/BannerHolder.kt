package com.fungo.banner.sample.holder

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.fungo.banner.holder.BaseBannerHolder
import com.fungo.banner.sample.R
import com.fungo.imagego.loadImage

/**
 * @author Pinger
 * @since 18-8-4 下午6:07
 *
 */

class BannerHolder(private var isNormalBanner: Boolean) : BaseBannerHolder<BannerBean> {

    override fun getHolderResId(): Int {
        return if (isNormalBanner) {
            R.layout.holder_banner_normal
        } else R.layout.holder_banner
    }

    override fun onBindData(itemView: View, data: BannerBean) {
        if (TextUtils.isEmpty(data.imageUrl)) {
            itemView.findViewById<ImageView>(R.id.imageView).setImageResource(data.imageRes)
        } else {
            loadImage(data.imageUrl, itemView.findViewById(R.id.imageView))
        }
        itemView.findViewById<TextView>(R.id.textView)?.text = data.title
    }

    override fun onPageClick(itemView: View, position: Int, data: BannerBean) {
        Toast.makeText(itemView.context, data.title, Toast.LENGTH_SHORT).show()
    }
}