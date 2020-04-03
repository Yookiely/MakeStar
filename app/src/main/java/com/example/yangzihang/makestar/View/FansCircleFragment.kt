package com.example.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.UserImp
import com.yookie.common.experimental.preference.CommonPreferences
import kotlinx.android.synthetic.main.activity_fans.*


class FansCircleFragment : Fragment() {
    var items = mutableListOf<Item>()
    lateinit var recyclerView: RecyclerView
    private var pageNum: Int = 1
    private var itemManager: ItemManager =
        ItemManager()
    lateinit var fansRefresh: SwipeRefreshLayout
    private var isLoading = true
    private var hostUserId =1
    private var lastPage = Int.MAX_VALUE
    companion object {
        private const val HOST_USER_ID = "hostuserid"
        fun newInstance(hostUserId: Int): FansCircleFragment {
            val fcFragment = FansCircleFragment()
            val bundle = Bundle()
            bundle.putInt(HOST_USER_ID, hostUserId)
            fcFragment.arguments = bundle
            return fcFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_fans_circle, container, false)
        recyclerView = view.findViewById(R.id.rec_fans_circle)
        fansRefresh =view.findViewById(R.id.fans_home_refresh)
        val mLayoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager=mLayoutManager
        recyclerView.adapter = ItemAdapter(itemManager)
        hostUserId = arguments!!.getInt(HOST_USER_ID)
        loadFansCircle()
        recyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalCount = mLayoutManager.itemCount
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && (lastVisibleItem + 1 == totalCount)) {
                    isLoading = true
                    loadMoreFansCircle()
                }
            }
        })
        fansRefresh.setOnRefreshListener(this::loadFansCircle)
        return view
    }

    private fun loadFansCircle() {
        pageNum =1
        items .clear()
        UserImp.getRecentActions(8, pageNum, hostUserId) {
            lastPage = it.last_page
            it.data.forEach {
                items.add(FansCircleInfoItem(activity!!.applicationContext, it))
            }
            recyclerView.withItems(items)
        }
        isLoading = false
        fansRefresh.isRefreshing = false
    }
    private fun loadMoreFansCircle(){
        pageNum++
        if(pageNum>lastPage){
            Toast.makeText(context,"加载到底了",Toast.LENGTH_SHORT).show()
        }
        UserImp.getRecentActions(8, pageNum, hostUserId) { fansComment->
            lastPage = fansComment.last_page
            itemManager.refreshAll {
               fansComment.data.forEach { fansCircleData->
                   addFansCircleInfo(activity!!.applicationContext,fansCircleData)
               }
           }
        }
        isLoading = false
    }
}