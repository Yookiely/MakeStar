package com.wingedvampires.homepage.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import java.util.ArrayList

class DynamicFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val titles = ArrayList<String>()

    private var fragments: MutableList<Fragment> = ArrayList()

    fun update(fragments: MutableList<Fragment>) {
        this.fragments = fragments
        notifyDataSetChanged()
    }

    fun add(title: String, fragment: Fragment) {
        titles.add(title)
        fragments.add(fragment)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}