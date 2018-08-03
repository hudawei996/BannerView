package com.fungo.banner

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.support.annotation.AttrRes
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import android.support.annotation.StyleRes
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.fungo.banner.holder.BannerHolderCreator
import com.fungo.banner.holder.BaseBannerHolder
import com.fungo.banner.pager.CustomViewPager
import com.fungo.banner.transformer.CoverModeTransformer
import com.fungo.banner.transformer.ScaleAlphaTransformer
import java.lang.reflect.Field
import java.util.*

/**
 *
 * @author Pinger
 * @since 18-7-18 下午13:21
 *
 * 封装ViewPager,实现了多种功能。包括延时无限循环，自动滑动慢速切换，手动快速自然切换，
 * 滑动缩放特效，指示器定制，Holder基类封装处理，页面点击事件，滑动监听事件等。
 * 基本满足了项目中Banner的需求，使用也十分简单。
 *
 * 本仓库基于项目[MZBannerView]进行优化改造，因此代码会有多处雷同，
 * 但是本仓库只是用于开源，并未商用，如有侵权违法请及时通知。
 *
 * 参考：[https://github.com/pinguo-zhouwei/MZBannerView]
 *
 */
class BannerView<T> : RelativeLayout {

    // 控制ViewPager滑动速度的Scroller
    private lateinit var mViewPagerScroller: ViewPagerScroller

    // ViewPager的适配器
    private lateinit var mAdapter: BannerPagerAdapter<T>

    // 是否自动播放
    private var isAutoPlay = true

    // ViewPager当前位置
    private var mCurrentItem = 0

    // Banner自动轮播的切换时间
    private var mDelayedTime = 4000L

    // 是否可以自动轮播图片，默认是可以自动轮播图片
    private var isCanLoop = true

    // 滑动模式
    private var mScrollMode = ScrollMode.NORMAL.ordinal

    // 覆盖模式下，覆盖的宽度
    private var mCoverWidth = 0

    // 覆盖模式下中间ViewPager左右的padding
    private var mCoverPadding = 0

    // 远离模式下，页面的边距
    private var mFarMargin = 0

    // 左右两边页面的缩放比例
    private var mPageScale = 0.9f

    // 左右两边页面的透明度
    private var mPageAlpha = 0.8f

    // 远离模式时，中间页面的宽度占比
    private var mPagePercent = 0.8f


    // Banner自动滑动时的滑动时长
    var mScrollDuration = 1200

    // 第一次手动触摸Banner的时候
    private var mFirstTouchTime = 0L


    // 指示器的ImageView集合
    private val mIndicators = ArrayList<ImageView>()
    // 指示器的图片资源 mIndicatorRes[0] 为为选中，mIndicatorRes[1]为选中
    private val mIndicatorRes = intArrayOf(R.drawable.banner_indicator_normal, R.drawable.banner_indicator_selected)

    // 距离左边的距离
    private var mIndicatorPaddingLeft = 0
    // 距离右边的距离
    private var mIndicatorPaddingRight = 0
    // 距离上边的距离
    private var mIndicatorPaddingTop = 0
    // 距离下边的距离
    private var mIndicatorPaddingBottom = 0

    // 指示器的位置
    private var mIndicatorAlign = IndicatorAlign.CENTER.ordinal


    // 页面改变的监听事件
    private var mOnPageChangeListener: ViewPager.OnPageChangeListener? = null
    // 页面点击的监听事件
    private var mBannerPageClickListener: BannerPageClickListener<T>? = null

    // ViewPager
    private val mViewPager: CustomViewPager by lazy {
        findViewById<CustomViewPager>(R.id.viewPager)
    }

    // IndicatorContainer
    private val mIndicatorContainer: LinearLayout  by lazy {
        findViewById<LinearLayout>(R.id.indicatorContainer)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        readAttrs(context, attrs)
        initView()
    }

    private fun readAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView)
        isCanLoop = typedArray.getBoolean(R.styleable.BannerView_bannerCanLoop, true)

        mScrollMode = typedArray.getInt(R.styleable.BannerView_bannerMode, mScrollMode)
        mCoverWidth = typedArray.getDimensionPixelSize(R.styleable.BannerView_bannerCoverWidth, 0)
        mCoverPadding = typedArray.getDimensionPixelSize(R.styleable.BannerView_bannerCoverPadding, 0)
        mFarMargin = typedArray.getDimensionPixelSize(R.styleable.BannerView_bannerFarMargin, 0)
        mPageScale = typedArray.getFloat(R.styleable.BannerView_bannerPageScale, mPageScale)
        mPageAlpha = typedArray.getFloat(R.styleable.BannerView_bannerPageAlpha, mPageAlpha)
        mPagePercent = typedArray.getFloat(R.styleable.BannerView_bannerPagePercent, mPagePercent)

        mIndicatorAlign = typedArray.getInt(R.styleable.BannerView_indicatorAlign, mIndicatorAlign)
        mIndicatorPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicatorPaddingLeft, 0)
        mIndicatorPaddingRight = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicatorPaddingRight, 0)
        mIndicatorPaddingTop = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicatorPaddingTop, 0)
        mIndicatorPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicatorPaddingBottom, 0)
        typedArray.recycle()
    }


    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_banner, this, true)

        // 设置滑动模式
        when (mScrollMode) {
            ScrollMode.COVER.ordinal -> {
                mViewPager.setPadding(mCoverPadding, 0, mCoverPadding, 0)
                mViewPager.setPageTransformer(true, CoverModeTransformer(mViewPager, mCoverWidth, mCoverPadding, mPageScale))
            }
            ScrollMode.FAR.ordinal -> {
                val params = mViewPager.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.CENTER
                params.width = (context.resources.displayMetrics.widthPixels * mPagePercent).toInt()
                mViewPager.layoutParams = params
                mViewPager.pageMargin = mFarMargin
                mViewPager.setPageTransformer(true, ScaleAlphaTransformer(mPageScale, mPageAlpha))
            }
        }

        // 初始化Scroller
        initViewPagerScroll()

        // 初始化指示器的位置
        when (mIndicatorAlign) {
            IndicatorAlign.LEFT.ordinal -> setIndicatorAlign(IndicatorAlign.LEFT)
            IndicatorAlign.CENTER.ordinal -> setIndicatorAlign(IndicatorAlign.CENTER)
            else -> setIndicatorAlign(IndicatorAlign.RIGHT)
        }
    }

    /**
     * 返回ViewPager
     */
    val viewPager: ViewPager?
        get() = mViewPager

    /**
     * 指示器对齐方式
     */
    enum class IndicatorAlign {
        LEFT,     // 做对齐
        CENTER,   // 居中对齐
        RIGHT     // 右对齐
    }


    /**
     * Banner滑动的模式
     */
    enum class ScrollMode {
        NORMAL,    // 普通
        COVER,     // 覆盖
        FAR        // 远离
    }


    /**
     * 无限循环的Runnable，自动进行页面切换
     */
    private val mHandler = Handler()
    private val mLoopRunnable = object : Runnable {
        override fun run() {
            if (isAutoPlay) {
                mCurrentItem = mViewPager.currentItem
                mCurrentItem++
                if (mCurrentItem == mAdapter.count - 1) {
                    mCurrentItem = 0
                    mViewPager.setCurrentItem(mCurrentItem, false)
                    mHandler.postDelayed(this, mDelayedTime)
                } else {
                    mViewPager.currentItem = mCurrentItem
                    mHandler.postDelayed(this, mDelayedTime)
                }
            } else {
                mHandler.postDelayed(this, mDelayedTime)
            }
        }
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private fun initViewPagerScroll() {
        try {
            val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            mViewPagerScroller = ViewPagerScroller(mViewPager.context)
            mScroller.set(mViewPager, mViewPagerScroller)

        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }


    /**
     * 初始化指示器Indicator
     */
    private fun initIndicator(datas: List<T>?) {
        mIndicatorContainer.removeAllViews()
        mIndicators.clear()
        for (i in datas!!.indices) {
            val imageView = ImageView(context)
            if (mIndicatorAlign == IndicatorAlign.LEFT.ordinal) {
                if (i == 0) {
                    val paddingLeft = mIndicatorPaddingLeft
                    imageView.setPadding(paddingLeft + 6, 0, 6, 0)
                } else {
                    imageView.setPadding(6, 0, 6, 0)
                }

            } else if (mIndicatorAlign == IndicatorAlign.RIGHT.ordinal) {
                if (i == datas.size - 1) {
                    val paddingRight = mIndicatorPaddingRight
                    imageView.setPadding(6, 0, 6 + paddingRight, 0)
                } else {
                    imageView.setPadding(6, 0, 6, 0)
                }

            } else {
                imageView.setPadding(6, 0, 6, 0)
            }

            if (i == mCurrentItem % datas.size) {
                imageView.setImageResource(mIndicatorRes[1])
            } else {
                imageView.setImageResource(mIndicatorRes[0])
            }

            mIndicators.add(imageView)
            mIndicatorContainer.addView(imageView)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!isCanLoop) {
            return super.dispatchTouchEvent(ev)
        }
        when (ev.action) {
        // 按住Banner的时候，停止自动轮播
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_DOWN -> {
                mFirstTouchTime = System.currentTimeMillis()
                // 按下或者移动的时候，暂停轮播
                pause()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mFirstTouchTime = System.currentTimeMillis()
                start()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    /**
     * 开始轮播
     * 应该确保在调用用了[setPages] 之后调用这个方法开始轮播
     */
    fun start() {
        // 如果Adapter为null, 说明还没有设置数据，这个时候不应该轮播Banner
        if (isCanLoop) {
            isAutoPlay = true
            mHandler.postDelayed(mLoopRunnable, mDelayedTime)
        }
    }

    /**
     * 停止轮播
     */
    fun pause() {
        isAutoPlay = false
        mHandler.removeCallbacks(mLoopRunnable)
    }

    /**
     * 设置BannerView 的切换时间间隔
     */
    fun setDelayedTime(delayedTime: Long) {
        mDelayedTime = delayedTime
    }

    fun addPageChangeLisnter(onPageChangeListener: ViewPager.OnPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener
    }

    /**
     * 添加Banner点击事件
     */
    fun setBannerPageClickListener(bannerPageClickListener: BannerPageClickListener<T>) {
        mBannerPageClickListener = bannerPageClickListener
    }

    /**
     * 是否显示Indicator
     */
    fun setIndicatorVisible(visible: Boolean) {
        if (visible) {
            mIndicatorContainer.visibility = View.VISIBLE
        } else {
            mIndicatorContainer.visibility = View.GONE
        }
    }

    /**
     * 设置indicator 图片资源
     *
     * @param selectRes   选中状态资源图片
     * @param unSelectRes 未选中状态资源图片
     */
    fun setIndicatorRes(@DrawableRes selectRes: Int, @DrawableRes unSelectRes: Int) {
        mIndicatorRes[0] = unSelectRes
        mIndicatorRes[1] = selectRes
    }

    /**
     * 设置数据，这是最重要的一个方法
     * 其他的配置应该在这个方法之前调用
     *
     * @param datas  Banner展示的数据集合
     * @param holderCreator ViewHolder生成器 [BannerHolderCreator] And [BaseBannerHolder]
     */
    fun setPages(datas: List<T>?, holderCreator: BannerHolderCreator<*>) {
        if (datas == null) {
            return
        }
        mViewPager.offscreenPageLimit = datas.size

        // 如果在播放，就先让播放停止
        pause()

        // 如果Banner数据不够，就去处特效
        if (datas.size < 3) {
            val layoutParams = mViewPager.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            mViewPager.layoutParams = layoutParams
            clipChildren = true
            mViewPager.clipChildren = true
        }

        // 根据数据的大小，初始化Indicator
        initIndicator(datas)

        mAdapter = BannerPagerAdapter(datas, holderCreator, isCanLoop)
        mAdapter.setUpViewPager(mViewPager)
        mAdapter.setPageClickListener(mBannerPageClickListener)

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                val realPosition = position % mIndicators.size
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener!!.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentItem = position


                // 切换indicator
                val realSelectPosition = mCurrentItem % mIndicators.size
                for (i in datas.indices) {
                    if (i == realSelectPosition) {
                        mIndicators[i].setImageResource(mIndicatorRes[1])
                    } else {
                        mIndicators[i].setImageResource(mIndicatorRes[0])
                    }
                }
                // 不能直接将mOnPageChangeListener 设置给ViewPager ,否则拿到的position 是原始的positon
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener!!.onPageSelected(realSelectPosition)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_DRAGGING -> isAutoPlay = false
                    ViewPager.SCROLL_STATE_SETTLING -> isAutoPlay = true
                }
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener!!.onPageScrollStateChanged(state)
                }
            }
        })
    }


    /**
     * 设置Indicator 的对齐方式
     * @param indicatorAlign 包括三个方向
     * 中间：[IndicatorAlign.CENTER]
     * 左边：[IndicatorAlign.LEFT]
     * 右边：[IndicatorAlign.RIGHT]
     */
    private fun setIndicatorAlign(indicatorAlign: IndicatorAlign) {
        mIndicatorAlign = indicatorAlign.ordinal
        val layoutParams = mIndicatorContainer.layoutParams as RelativeLayout.LayoutParams
        when (indicatorAlign) {
            IndicatorAlign.LEFT -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            IndicatorAlign.RIGHT -> layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            else -> layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        }

        /**
         * 增加设置Indicator 的上下边距
         */
        layoutParams.setMargins(0, mIndicatorPaddingTop, 0, mIndicatorPaddingBottom)
        mIndicatorContainer.layoutParams = layoutParams
    }


    class BannerPagerAdapter<T>(
            private val datas: List<T>,
            private val holderCreator: BannerHolderCreator<*>,
            private val canLoop: Boolean) : PagerAdapter() {

        private var mViewPager: ViewPager? = null
        private var mPageClickListener: BannerPageClickListener<T>? = null
        private val mLooperCountFactor = 500

        /**
         * 我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
         * 但是要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
         * 直到找到从0开始的位置
         */
        private val startSelectItem: Int
            get() {
                var currentItem = realCount * mLooperCountFactor / 2
                if (currentItem % realCount == 0) {
                    return currentItem
                }
                while (currentItem % realCount != 0) {
                    currentItem++
                }
                return currentItem
            }

        /**
         * 获取真实的Count
         */
        private val realCount: Int
            get() = datas.size

        fun setPageClickListener(pageClickListener: BannerPageClickListener<T>?) {
            mPageClickListener = pageClickListener
        }

        /**
         * 初始化Adapter和设置当前选中的Item
         */
        fun setUpViewPager(viewPager: ViewPager) {
            mViewPager = viewPager
            mViewPager!!.adapter = this
            mViewPager!!.adapter!!.notifyDataSetChanged()
            val currentItem = if (canLoop) startSelectItem else 0
            // 设置当前选中的Item
            mViewPager!!.currentItem = currentItem
        }

        /**
         * 如果getCount 的返回值为Integer.MAX_VALUE 的话，
         * 那么在setCurrentItem的时候会ANR(除了在onCreate 调用之外)
         */
        override fun getCount(): Int {
            return if (canLoop) realCount * mLooperCountFactor else realCount
        }

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view === any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = getView(position, container)
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(any as View)
        }

        override fun finishUpdate(container: ViewGroup) {
            // 轮播模式才执行
            if (canLoop) {
                var position = mViewPager!!.currentItem
                if (position == count - 1) {
                    position = 0
                    setCurrentItem(position)
                }
            }

        }

        private fun setCurrentItem(position: Int) {
            try {
                mViewPager?.setCurrentItem(position, false)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }

        }

        /**
         * @param position
         * @param container
         * @return
         */
        private fun getView(position: Int, container: ViewGroup): View {

            val realPosition = position % realCount
            val holder: BaseBannerHolder<T> = holderCreator.onCreateBannerHolder() as BaseBannerHolder<T>

            // create View
            val view = LayoutInflater.from(container.context).inflate(holder.getHolderResId(), null)

            // bind data
            if (datas.isNotEmpty()) {
                holder.onBindData(view, datas[realPosition])
            }

            // add listener
            view.setOnClickListener { v ->
                mPageClickListener?.onPageClick(v, realPosition, datas[realPosition])
            }
            return view
        }
    }

    /**
     * 由于ViewPager 默认的切换速度有点快，因此用一个Scroller 来控制切换的速度
     * 而实际上ViewPager 切换本来就是用的Scroller来做的，因此我们可以通过反射来
     * 获取取到ViewPager 的 mScroller 属性，然后替换成我们自己的Scroller
     */
    inner class ViewPagerScroller(context: Context) : Scroller(context) {

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            var durationX = duration
            // 如果是自动轮播，就使用动画模式，如果不是就还是用原来的模式
            if (System.currentTimeMillis() - mFirstTouchTime >= mDelayedTime) {
                durationX = mScrollDuration
            }
            super.startScroll(startX, startY, dx, dy, durationX)
        }
    }

    /**
     * Banner的Holder点击回调
     */
    interface BannerPageClickListener<T> {
        fun onPageClick(view: View, position: Int, data: T)
    }

    private fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }

}
