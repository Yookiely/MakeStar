package com.example.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.ItemAdapter
import cn.edu.twt.retrox.recyclerviewdsl.ItemManager
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.UserImp
import com.yookie.common.experimental.preference.CommonPreferences

class VideoFragment : Fragment() {

    companion object {
        private const val HOST_USER_ID = "hostuserid"
        fun newInstance(hostUserId: String): VideoFragment {
            val fcFragment = VideoFragment()
            val bundle = Bundle()
            bundle.putString(HOST_USER_ID, hostUserId)
            fcFragment.arguments = bundle
            return fcFragment
        }
    }

    lateinit var itemManager : ItemManager
    lateinit var hostUserId : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_myself_video, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.myself_video)
         hostUserId = arguments!!.getString(HOST_USER_ID)!!
        val mlayoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager = mlayoutManager
        itemManager = ItemManager()
        recyclerView.adapter = ItemAdapter(itemManager)
//            UserImp.getMyVideoList(1,hostUserId){
////                recyclerView.withItems {
////                for (work in it){
////                    Log.d("what!!!",it.size.toString()+ work.cover_url + work.hot_value)
////                    addMyVideo(work,activity!!)
////                }
////            }
////        }
        refreshList(1)

        return view
    }

    private fun refreshList(num : Int){
        UserImp.getMyVideoList(num,hostUserId){
            itemManager.autoRefresh {
                for (x in it.data) {
                    addMyVideo(x,activity!!)

                }
                if (it.last_page>num){
                    refreshList(it.current_page+1)

                }
            }

        }
    }


}