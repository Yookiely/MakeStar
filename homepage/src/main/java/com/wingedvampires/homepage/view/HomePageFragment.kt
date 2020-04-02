package com.wingedvampires.homepage.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.bumptech.glide.Glide
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.extension.MyBanner
import com.wingedvampires.homepage.model.Banner
import com.wingedvampires.homepage.model.HomePageService
import com.wingedvampires.homepage.model.HomePageUtils
import com.wingedvampires.homepage.view.items.homePageItem
import com.yookie.common.experimental.extensions.QuietCoroutineExceptionHandler
import com.yookie.common.experimental.extensions.awaitAndHandle
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.common.experimental.preference.CommonPreferences
import com.youth.banner.BannerConfig
import com.youth.banner.loader.ImageLoader
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class HomePageFragment : Fragment() {
    private var workType = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var banner: MyBanner
    private var itemManager: ItemManager = ItemManager()
    private var isLoading = true
    private var page = 1
    private var lastPage = Int.MAX_VALUE
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var refreshAndLoad: () -> Unit
    lateinit var loadMore: () -> Unit

    companion object {
        var bannerData: List<Banner> = mutableListOf()

        fun newInstance(type: Int): HomePageFragment {
            val args = Bundle()
            args.putInt(HomePageUtils.INDEX_KEY, type)
            val fragment = HomePageFragment()
            fragment.arguments = args

            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)
        val sLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        mSwipeRefreshLayout = view.findViewById(R.id.sl_homepage_main)
        val bundle = arguments
        workType = bundle!!.getInt(HomePageUtils.INDEX_KEY)

        /** 根据不同的界面选择不同的方法
         * workType = 0 是推荐界面
         */
        refreshAndLoad = if (workType == HomePageUtils.WORK_TYPE_ID_OF_RECOMMEND) {
            { loadRecommend() }
        } else {
            { loadByType() }
        }
        loadMore = if (workType == HomePageUtils.WORK_TYPE_ID_OF_RECOMMEND) {
            { loadMoreRecommend() }
        } else {
            { loadMoreByType() }
        }

        banner = view.findViewById(R.id.br_homepage_main)
        recyclerView = view.findViewById(R.id.rv_homepage_main)
        recyclerView.apply {
            adapter = ItemAdapter(itemManager)
        }

        recyclerView.layoutManager = sLayoutManager
        setBanner()
        refreshAndLoad()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = sLayoutManager.itemCount
                val array = IntArray(2)
                sLayoutManager.findLastCompletelyVisibleItemPositions(array)

                if (!isLoading && (totalCount <= array[1] + 2)) {
                    isLoading = true
                    loadMore()
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener(this::refresh)

        return view
    }

    override fun onStart() {
        super.onStart()
        banner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        banner.stopAutoPlay()
    }

    private fun refresh() = refreshAndLoad()

    private fun setBanner() {
        launch(UI + QuietCoroutineExceptionHandler) {
            if (bannerData.isEmpty()) {
                bannerData = HomePageService.getRecentBannerByType().awaitAndHandle {
                    it.printStackTrace()
                    Toast.makeText(this@HomePageFragment.context, "加载banner失败", Toast.LENGTH_SHORT)
                        .show()
                }?.data ?: return@launch
            }

            val bannerCovers = bannerData.map { banner -> banner.banner_url }
            banner.apply {
                //设置图片加载器
                setImageLoader(object : ImageLoader() {
                    override fun displayImage(
                        context: Context?,
                        path: Any?,
                        imageView: ImageView?
                    ) {
                        Glide.with(context!!).load(path).error(R.drawable.ms_no_pic)
                            .into(imageView!!)
                    }
                })
                //设置banner样式
                setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                //设置图片集合
                setImages(bannerCovers)
                //设置自动轮播，默认为true
                isAutoPlay(true)
                //设置指示器位置（当banner模式中有指示器时）
                setIndicatorGravity(BannerConfig.CENTER)
                start()
            }
        }
    }

    private fun loadRecommend() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val works = HomePageService.getRecommendWork().awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@HomePageFragment.context, "加载失败", Toast.LENGTH_SHORT).show()
                mSwipeRefreshLayout.isRefreshing = false
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear()
                works.forEach { work ->
                    CommonPreferences.setAndGetUserHistory(work.work_ID)
                    homePageItem(activity!!, work) {
                        CommonPreferences.setAndGetUserHabit(work.work_type_ID)
                        val intent = Intent().also {
                            it.putExtra(
                                HomePageUtils.VIDEO_PALY_WORKID,
                                work.work_ID
                            )
                        }
                        Transfer.startActivityWithoutClose(activity!!, "VideoPlayActivity", intent)
                    }

                }
            }
            isLoading = false
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadMoreRecommend() {
        launch(UI + QuietCoroutineExceptionHandler) {
            val works = HomePageService.getRecommendWork().awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@HomePageFragment.context, "加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                works.forEach { work ->
                    CommonPreferences.setAndGetUserHistory(work.work_ID)
                    homePageItem(activity!!, work) {
                        CommonPreferences.setAndGetUserHabit(work.work_type_ID)
                        val intent = Intent().also {
                            it.putExtra(
                                HomePageUtils.VIDEO_PALY_WORKID,
                                work.work_ID
                            )
                        }
                        Transfer.startActivityWithoutClose(activity!!, "VideoPlayActivity", intent)
                    }

                }
            }
            isLoading = false
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    //音乐、舞蹈、搞笑、颜值、小剧场和其他...
    private fun loadByType() {
        launch(UI + QuietCoroutineExceptionHandler) {
            page = 1
            val worksWithType = HomePageService.getWorkByTypeID(page, workType).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@HomePageFragment.context, "加载失败", Toast.LENGTH_SHORT).show()
                mSwipeRefreshLayout.isRefreshing = false
            }?.data ?: return@launch

            itemManager.refreshAll {
                clear() // 删除之前的item
                worksWithType.data.forEach { work ->
                    CommonPreferences.setAndGetUserHistory(work.work_ID)
                    homePageItem(activity!!, work) {
                        CommonPreferences.setAndGetUserHabit(work.work_type_ID)
                        val intent = Intent().also {
                            it.putExtra(
                                HomePageUtils.VIDEO_PALY_WORKID,
                                work.work_ID
                            )
                        }
                        Transfer.startActivityWithoutClose(activity!!, "VideoPlayActivity", intent)
                    }

                }
            }

            page = worksWithType.to
            lastPage = worksWithType.last_page
            isLoading = false
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadMoreByType() {
        launch(UI + QuietCoroutineExceptionHandler) {
            if (page >= lastPage) {
                Toast.makeText(this@HomePageFragment.context, "加载到底了", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val worksWithType = HomePageService.getWorkByTypeID(page, workType).awaitAndHandle {
                it.printStackTrace()
                Toast.makeText(this@HomePageFragment.context, "加载失败", Toast.LENGTH_SHORT).show()
            }?.data ?: return@launch

            itemManager.autoRefresh {
                worksWithType.data.forEach { work ->
                    CommonPreferences.setAndGetUserHistory(work.work_ID)
                    homePageItem(activity!!, work) {
                        CommonPreferences.setAndGetUserHabit(work.work_type_ID)
                        val intent = Intent().also {
                            it.putExtra(
                                HomePageUtils.VIDEO_PALY_WORKID,
                                work.work_ID
                            )
                        }
                        Transfer.startActivityWithoutClose(activity!!, "VideoPlayActivity", intent)
                    }

                }
            }

            page = worksWithType.to
            lastPage = worksWithType.last_page
            isLoading = false
            mSwipeRefreshLayout.isRefreshing = false
        }
    }
}

