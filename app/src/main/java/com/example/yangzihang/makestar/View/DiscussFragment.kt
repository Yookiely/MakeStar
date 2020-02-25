package com.example.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.UserImp
import com.yookie.common.experimental.preference.CommonPreferences


class DiscussFragment : Fragment(){

    lateinit var recyclerView: RecyclerView
    lateinit var itemManager : ItemManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_message_discuss, container, false)
        recyclerView = view.findViewById(R.id.rec_message_discuss)
        recyclerView.layoutManager = LinearLayoutManager(activity)
         itemManager = ItemManager()
        recyclerView.adapter = ItemAdapter(itemManager)
        refreshList(1)


        return view
    }


    private fun refreshList(num : Int){
        UserImp.getAllMessage(num,10,CommonPreferences.userid){
            itemManager.autoRefresh {
                for (x in it.data) {
                    addStarItem(1,x.from_user_avatar,x.from_user_name,x.time,x.new_content,x.old_content)

                }
                if (it.last_page>num){
                    refreshList(it.current_page+1)

                }
            }

        }
    }
}