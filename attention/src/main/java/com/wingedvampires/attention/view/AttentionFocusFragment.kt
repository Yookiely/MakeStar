package com.wingedvampires.attention.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.wingedvampires.attention.R

class AttentionFocusFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
    private val itemManager by lazy { recyclerView.withItems(listOf()) }
    private var isLoading = true
    private var page = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attention_focus, container, false)

        recyclerView = view.findViewById(R.id.rv_attention_focus)
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