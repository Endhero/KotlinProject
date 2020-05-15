package com.lcd.kotlinproject.view.impl.org

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.view.impl.base.BKFragment
import com.lcd.kotlinproject.vm.rog.OrgViewModel

class OrgFragment : BKFragment() {
    @BindView(R.id.text_org_name)
    lateinit var textOrgName: TextView
    @BindView(R.id.text_org_address)
    lateinit var textOrgAddress: TextView
    @BindView(R.id.text_org_province)
    lateinit var textOrgProvince: TextView
    @BindView(R.id.text_org_city)
    lateinit var textOrgCity: TextView

    private var vm: OrgViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)= inflater.inflate(R.layout.fragment_org, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProviders.of((context as FragmentActivity))[OrgViewModel::class.java]
        vm!!.getOrgDataLiveData().observe(this, Observer{ orgData ->
            textOrgName.text = orgData.name
            textOrgAddress.text = orgData.address
            textOrgProvince.text = orgData.province
            textOrgCity.text = orgData.city
        })
        vm!!.getOrgInfo()
    }
}