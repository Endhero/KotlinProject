package com.lcd.kotlinproject.view.impl.maintain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.view.adapter.maintain.MaintainHistoryAdapter
import com.lcd.kotlinproject.view.impl.base.ToolbarActivity
import kotlinx.android.synthetic.main.activity_maintain.*
import java.util.*

class Maintain : ToolbarActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        tablayout.setupWithViewPager(viewpager)

        val arraylistFragment = ArrayList<Fragment>()
        val arraylistTitle = ArrayList<String>()
        val maintainlistfragment = MaintainPlanListFragment()
        val maintainjoblistfragment = MaintainJobListFragment()

        arraylistFragment.add(maintainlistfragment)
        arraylistFragment.add(maintainjoblistfragment)
        arraylistTitle.add(getString(R.string.maintain_plan))
        arraylistTitle.add(getString(R.string.maintain_job_list))

        val alarmhistoryadapter = MaintainHistoryAdapter(arraylistFragment, arraylistTitle, supportFragmentManager)

        viewpager.adapter = alarmhistoryadapter
        tablayout.getTabAt(0)!!.select()
    }

    override fun getLayoutId() =  R.layout.activity_maintain

    override fun getTitleId() = R.string.device_maintain

    companion object{
        fun start(context: Context) {
            val starter = Intent(context, Maintain::class.java)
            context.startActivity(starter)
        }
    }
}