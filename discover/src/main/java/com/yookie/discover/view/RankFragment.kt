package com.yookie.discover.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.yookie.discover.R
import com.yookie.discover.network.UserRank
import kotlinx.android.synthetic.main.fragment_discover_rank.*


class RankFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_discover_rank, container, false)
        val personRec = view.findViewById<RecyclerView>(R.id.person_rank)
        val videoRec = view.findViewById<RecyclerView>(R.id.video_rank)
        personRec.layoutManager = LinearLayoutManager(activity)
        videoRec.layoutManager = LinearLayoutManager(activity)
        UserRank.getUserRank(6) {
            personRec.withItems {
                if (it.size >= 3) {
                    addTopRankItem(it)
                }
                for (index in 3 until it.size) {
                    addRankItem(
                        index.toString(),
                        it[index],
                        this@RankFragment.context!!,
                        activity!!
                    )
                }
                addMoreItem(this@RankFragment.context!!, 0)
            }
        }

        UserRank.getWorkRank(3) {
            videoRec.withItems {
                for ((index, value) in it.withIndex()) {
                    addVideoItem(value, index + 1)
                }
                addMoreItem(this@RankFragment.context!!, 1)
            }


        }

        return view
    }
}