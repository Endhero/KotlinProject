package com.lcd.kotlinproject.view.impl.org

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.view.impl.base.BKFragment
import com.lcd.kotlinproject.vm.rog.OrgViewModel
import kotlinx.android.synthetic.main.fragment_org.*

class OrgFragment : BKFragment() {
    private var vm: OrgViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)= inflater.inflate(R.layout.fragment_org, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProviders.of((context as FragmentActivity))[OrgViewModel::class.java]
        vm!!.getOrgDataLiveData().observe(this, Observer{ orgData ->
            text_org_name.text = orgData.name
            text_org_address.text = orgData.address
            text_org_province.text = orgData.province
            text_org_city.text = orgData.city
        })
        vm!!.getOrgInfo()
    }
}