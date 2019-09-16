package com.yookie.discover.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.yookie.discover.R
import kotlinx.android.synthetic.main.fragment_discover_donate.*

class ActivityFragment :Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_discover_donate, container, false)
        activity_rec.layoutManager = LinearLayoutManager(activity)
        activity_rec.withItems {

        }
        return view
    }
}