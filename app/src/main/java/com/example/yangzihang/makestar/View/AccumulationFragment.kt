package com.example.yangzihang.makestar.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yangzihang.makestar.R

class AccumulationFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_user_accumulation, container, false)


        return view
    }
}