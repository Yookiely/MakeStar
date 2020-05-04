package com.example.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.UserImp
import com.yookie.common.experimental.preference.CommonPreferences

class MessageFragment : Fragment() {


    lateinit var recyclerView: RecyclerView
    lateinit var itemManager: ItemManager
    lateinit var messageRefresh: SwipeRefreshLayout
    var nums = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_message_message, container, false)
        messageRefresh = view.findViewById(R.id.sl_message_message)
        recyclerView = view.findViewById(R.id.rec_message_message)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        itemManager = ItemManager()
        recyclerView.adapter = ItemAdapter(itemManager)
        nums = CommonPreferences.newUserMessage
        refreshList(1)

        messageRefresh.setOnRefreshListener {
            itemManager.clear()
            refreshList(1)
        }
        return view
    }

    private fun refreshList(num: Int) {
        UserImp.getMessage(CommonPreferences.userid, 1.toString(), 10.toString(), num) {
            itemManager.autoRefresh {
                for (x in it.data) {
                    if (nums > 0) {
                        addPrivate(activity!!, x, true)
                        nums--
                    } else {
                        addPrivate(activity!!, x, false)
                    }

                }

                messageRefresh.isRefreshing = false
                if (it.last_page > num) {
                    refreshList(it.current_page + 1)

                }
            }

        }
    }
}