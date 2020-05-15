package com.lcd.kotlinproject.view.impl.org

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.OnClick
import com.lcd.base.exception.GlobalError
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.UserData
import com.lcd.kotlinproject.view.impl.base.ErrorObserver
import com.lcd.kotlinproject.vm.rog.ContactEditViewModel
import com.yuwell.androidbase.view.ToolbarActivity
import kotlinx.android.synthetic.main.activity_contact_edit.*
import org.jetbrains.anko.longToast

class ContactEditActivity : ToolbarActivity() {
    private var vm: ContactEditViewModel? = null
    private var user: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = intent.getParcelableExtra("user")

        vm = ViewModelProviders.of(this)[ContactEditViewModel::class.java]
        vm!!.globalErrorLiveData.observe(this, object : ErrorObserver(this) {
            override fun onChanged(error: GlobalError) {
                dismissProgressDialog()
                showMessage(error.throwable.message)
                //                super.onChanged(error);
            }
        })

        vm!!.getRetMutableLiveData().observe(this, Observer{ ret->
            dismissProgressDialog()
            longToast(ret.msg)
            setResult(RESULT_OK)
            finish()
        })

        user?.let {
            edit_name.setText(it.name)
            edit_phone.setText(it.mobile)
            switch_message.isChecked = it.isNotificationOn()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (intent.hasExtra("user")) {
            menuInflater.inflate(R.menu.contact_edit, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = if (item.itemId == R.id.menu_delete) {
        showDeleteConfirmDialog()
        true
        } else super.onOptionsItemSelected(item)

    override fun getLayoutId() = R.layout.activity_contact_edit

    override fun getTitleId() = if (intent.hasExtra("user")) R.string.edit_contact else R.string.add_contact

    @OnClick(R.id.button_save)
    fun onClick() {
        val name = edit_name.text.toString()

        if (name.isEmpty()) {
            showMessage(R.string.hint_contact)
            return
        }

        val phone = edit_phone.text.toString()

        if (phone.isEmpty()) {
            showMessage(R.string.hint_phone)
            return
        }

        if (phone.length < 11 || !phone.startsWith("1")) {
            showMessage(R.string.input_valid_phone)
            return
        }

        var user: UserData? = intent.getParcelableExtra("user")

        if (user == null) {
            user = UserData()
            user.name = name
            user.mobile = phone
            user.setNotification(switch_message.isChecked)
            vm!!.add(user)
        } else {
            user.name = name
            user.mobile = phone
            user.setNotification(switch_message.isChecked)
            vm!!.edit(user)
        }

        showProgressDialog(R.string.committing)
    }

    private fun showDeleteConfirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_contact_confirm)
        builder.setPositiveButton(R.string.ensure) { _, _-> vm!!.delete(user!!.id!!) }
        builder.setNegativeButton(R.string.cancel, null)
        builder.create().show()
    }

    companion object{
        fun start(fragment: Fragment, requestCode: Int, user: UserData?) {
            val starter = Intent(fragment.context, ContactEditActivity::class.java)

            user?.let {
                starter.putExtra("user", user)
            }

            fragment.startActivityForResult(starter, requestCode)
        }
    }
}