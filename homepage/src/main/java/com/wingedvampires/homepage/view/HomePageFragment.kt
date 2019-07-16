package com.wingedvampires.homepage.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wingedvampires.homepage.R
import com.wingedvampires.homepage.model.HomePageUtils

object HomePageType {
    val RECOMMEND: String = "recommend"
    const val LIVE: String = "live"
}

class HomePageFragment : Fragment() {
    private lateinit var pageType: String

    companion object {
        fun newInstance(type: String): HomePageFragment {
            val args = Bundle()
            args.putString(HomePageUtils.INDEX_KEY, type)
            val fragment = HomePageFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)
        val bundle = arguments
        pageType = bundle!!.getString(HomePageUtils.INDEX_KEY)

        when (pageType) {
            HomePageType.RECOMMEND -> {
            }
            HomePageType.LIVE -> {
            }
            else -> {
            }
        }

        return view
    }
}