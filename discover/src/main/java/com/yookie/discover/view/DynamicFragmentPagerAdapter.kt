package com.yookie.discover.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter

class DynamicFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val titles = mutableListOf<String>()
    private var fragments = mutableListOf<Fragment>()

    fun update(fragments: MutableList<Fragment>) {
        this.fragments = fragments
        notifyDataSetChanged()
    }

    fun add(title: String, fragment: Fragment) {
        titles.add(title)
        fragments.add(fragment)
    }

    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE


    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getPageTitle(position: Int) = titles[position]

    override fun getCount(): Int = fragments.size
}