package com.yookie.discover.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.yookie.common.experimental.extensions.jumpchannel.Transfer
import com.yookie.discover.R
import com.yookie.discover.network.UserRank
import kotlinx.android.synthetic.main.fragment_discover_donate.*

class ActivityFragment :Fragment(){

    lateinit var search : TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_discover_donate, container, false)
//        val activityRec = view.findViewById<RecyclerView>(R.id.activity_rec)
//        activityRec.layoutManager = LinearLayoutManager(activity)
//
//        UserRank.getActivity(1,10000){
//            activityRec.withItems {
//                for (x in it.data){
//                    addActivityItem(x,this@ActivityFragment.activity!!)
//                }
//            }
//        }
//        search = view.findViewById(R.id.search)
//        search.setOnClickListener {
//            Transfer.startActivityWithoutClose(activity!!,"GlobalSearchActivity",Intent())
//        }


        return view
    }
}