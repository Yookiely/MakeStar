package com.yookie.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yookie.yangzihang.makestar.R

class RankHistoryFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_user_rankhistory, container, false)


        return view
    }
}