package com.thelazypeople.graphvisualiser

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FragmentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm){
    private val list = arrayListOf<Fragment>()

    fun addf(fragment:Fragment){
        list.add(fragment)
    }

    override fun getItem(position: Int): Fragment  = list[position]


    override fun getCount(): Int = list.size

}
