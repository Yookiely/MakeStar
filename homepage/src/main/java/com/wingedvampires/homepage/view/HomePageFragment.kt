package com.wingedvampires.homepage.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.HomePageUtils

//音乐、舞蹈、搞笑、颜值、小剧场和其他
object HomePageType {
    const val RECOMMEND: Int = 1
    const val MUSIC: Int = 2
    const val DANCE: Int = 3
    const val AMUSE: Int = 4
    const val FACE: Int = 5
    const val THEATER: Int = 6
    const val ELSE: Int = 7
}

class HomePageFragment : Fragment() {
    private var pageType: Int = 0
    private lateinit var recyclerView: RecyclerView
    private val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
    private val itemManager by lazy { recyclerView.withItems(listOf()) }
    private var isLoading = true
    private var page = 1

    companion object {
        fun newInstance(type: Int): HomePageFragment {
            val args = Bundle()
            args.putInt(HomePageUtils.INDEX_KEY, type)
            val fragment = HomePageFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)
        val bundle = arguments
        pageType = bundle!!.getInt(HomePageUtils.INDEX_KEY)
        recyclerView = view.findViewById(R.id.rv_homepage_main)
        recyclerView.layoutManager = layoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = layoutManager.itemCount
                val array = IntArray(2)
                layoutManager.findLastCompletelyVisibleItemPositions(array)

                if (!isLoading && (totalCount <= array[1] + 2)) {
                    page++

                }
            }
        })
        isLoading = false

        return view
    }
}