package com.lcd.kotlinproject.view.impl.org

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.UserData
import com.lcd.kotlinproject.view.impl.base.BKFragment
import com.lcd.kotlinproject.view.impl.base.ErrorObserver
import com.lcd.kotlinproject.view.widget.SpaceItemDecoration
import com.lcd.kotlinproject.vm.rog.OrgViewModel
import com.yuwell.androidbase.tool.DensityUtil
import java.util.*

class ContactFragment : BKFragment() {
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private var mAdapter: ContactAdapter? = null
    private var vm: OrgViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        vm = ViewModelProviders.of((context as FragmentActivity))[OrgViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.addItemDecoration(SpaceItemDecoration(DensityUtil.dip2px(context, 9f), DensityUtil.dip2px(context, 12f)))
        recyclerView.adapter = ContactAdapter(this).also {mAdapter = it}

        vm!!.globalErrorLiveData.observe(this, ErrorObserver(context!!))
        vm!!.getUsersMutableLiveData().observe(this, androidx.lifecycle.Observer{ list -> mAdapter?.setUserList(list) })
        vm!!.getUserList()
    }

    @OnClick(R.id.button_add)
    fun onClick() = ContactEditActivity.start(this, 1, null)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            vm!!.getUserList()
        }
    }

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.text_name)
        lateinit var textName: TextView
        @BindView(R.id.text_phone)
        lateinit var textPhone: TextView
        @BindView(R.id.text_notification)
        lateinit var textNotification: TextView
        @BindView(R.id.button_edit)
        lateinit var buttonEdit: ImageButton

        init {
            ButterKnife.bind(this, view)
        }
    }

    class ContactAdapter(private val f: Fragment) : RecyclerView.Adapter<ContactViewHolder>() {
        val userList: MutableList<UserData?> = ArrayList<UserData?>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            return ContactViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))
        }

        override fun onBindViewHolder(holder: ContactViewHolder, nPosition: Int) {
            val data: UserData? = userList[nPosition]
            holder.textName.text = data!!.name
            holder.textPhone.text = data.mobile
            holder.textNotification.visibility = if (data.isNotificationOn()) View.VISIBLE else View.GONE
            holder.buttonEdit.setOnClickListener {
                ContactEditActivity.start(f, 1, userList[nPosition])
            }
        }

        override fun getItemCount() = userList?.size ?: 0

        inline fun <reified T: Collection<UserData?>> setUserList(list: T) {
            userList.clear()
            userList.addAll(list)
            notifyDataSetChanged()
        }
    }
}