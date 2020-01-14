package com.example.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.example.yangzihang.makestar.R
import com.example.yangzihang.makestar.network.UserImp

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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_myself_video, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.myself_video)
        val hostUserId = arguments!!.getString(HOST_USER_ID)
        val mlayoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager = mlayoutManager
            UserImp.getMyVideoList(hostUserId!!){
                recyclerView.withItems {
                for (work in it){
                    Log.d("what!!!",it.size.toString()+ work.cover_url + work.hot_value)
                    addMyVideo(work,activity!!)
                }
            }
        }

        return view
    }
}