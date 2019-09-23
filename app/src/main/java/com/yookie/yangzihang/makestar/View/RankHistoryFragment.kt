package com.yookie.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.yookie.common.experimental.preference.CommonPreferences
import com.yookie.yangzihang.makestar.R
import com.yookie.yangzihang.makestar.network.UserImp
import kotlinx.android.synthetic.main.fragment_user_rankhistory.*

class RankHistoryFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_user_rankhistory, container, false)
        val recUser = view.findViewById<RecyclerView>(R.id.rec_user_rankhistory)
        recUser.layoutManager = LinearLayoutManager(activity)
        UserImp.getHistoryRank(CommonPreferences.userid) {
            recUser.withItems {
                for (x in it){
                    addRankHistory(x)
                }

            }
        }



        return view
    }
}