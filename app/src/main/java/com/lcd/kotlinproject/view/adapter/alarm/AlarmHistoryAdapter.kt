package com.lcd.kotlinproject.view.adapter.alarm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AlarmHistoryAdapter (private var listfragment : List<Fragment>, private var listitle: List<String>, var fragmentmanager: FragmentManager) : FragmentPagerAdapter (fragmentmanager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(nPosition: Int) = listfragment[nPosition]

    override fun getCount() = listfragment.size

    override fun getPageTitle(nPosition: Int) = listitle[nPosition]
}