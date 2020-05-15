package com.lcd.kotlinproject.view.impl.org

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.lcd.kotlinproject.R
import com.yuwell.androidbase.view.ToolbarActivity

class OrgActivity : ToolbarActivity() {
    @BindView(R.id.tablayout)
    lateinit var tab: TabLayout
    @BindView(R.id.viewpager)
    lateinit var pager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ButterKnife.bind(this)

        tab.setupWithViewPager(pager)
        pager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int) = if (position == 0) OrgFragment() else ContactFragment()

            override fun getCount() = 2

            override fun getPageTitle(position: Int) = if (position == 0) getString(R.string.org_info) else getString(R.string.contact)
        }
    }

    override fun getLayoutId() = R.layout.activity_org_info

    override fun getTitleId() = R.string.org_info

    companion object{
        fun start(context: Context) {
            val starter = Intent(context, OrgActivity::class.java)
            context.startActivity(starter)
        }
    }
}