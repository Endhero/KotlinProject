package com.lcd.kotlinproject.view.adapter.maintain

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MaintainHistoryAdapter (val listfragment: List<Fragment>, val listitle: List<String>, fragmentmanager: FragmentManager): FragmentPagerAdapter(fragmentmanager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(nPosition: Int)  = listfragment[nPosition]

    override fun getCount() = listfragment.size

    override fun getPageTitle(nPosition: Int) = listitle[nPosition]
}