package com.lcd.kotlinproject.view.impl.org

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentStatePagerAdapter
import com.lcd.kotlinproject.R
import com.yuwell.androidbase.view.ToolbarActivity
import kotlinx.android.synthetic.main.activity_org_info.*

class OrgActivity : ToolbarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tablayout.setupWithViewPager(viewpager)
        viewpager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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