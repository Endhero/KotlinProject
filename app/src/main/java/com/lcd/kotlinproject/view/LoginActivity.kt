package com.lcd.kotlinproject.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.OnClick
import com.lcd.base.exception.GlobalError
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.view.impl.base.ErrorObserver
import com.lcd.kotlinproject.view.impl.base.ToolbarActivity
import com.lcd.kotlinproject.vm.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : ToolbarActivity() {
    private var vm: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this)[LoginViewModel::class.java]
        vm!!.getSendCodeRetLiveData().observe(this, Observer { ret ->
            if (ret.success) {
                button_code.startCountDown()
            } else {
                button_code.stopCountDown()
            }
        })

        vm!!.getLoginRetLiveData().observe(this, Observer { ret ->
            dismissProgressDialog()
            if (ret.success) {
//                scheduleRefreshTask();
                startMainActivity()
            } else {
                showMessage(ret.msg)
            }
        })

        vm!!.globalErrorLiveData.observe(this, object : ErrorObserver(this) {
            override fun onChanged(error: GlobalError) {
                dismissProgressDialog()
                super.onChanged(error)
            }
        })

        if (vm!!.canAutoLogin()) {
            showProgressDialog(R.string.logining)
        }
    }

    override fun getLayoutId() = R.layout.activity_login

    @OnClick(R.id.button_login)
    fun login() {
        val phone = edit_phone.text.toString()

        if (!checkPhone(phone)) {
            return
        }

        val code = edit_code.text.toString()

        if (!checkCode(code)) {
            return
        }

        showProgressDialog(R.string.logining)
        vm!!.login(phone, code)
    }

    @OnClick(R.id.button_code, R.id.text_get_audio_msg)
    fun onCodeButtonClick(v: View) {
        val phone = edit_phone.text.toString()

        if (!checkPhone(phone)) {
            return
        }

        if (v.id == R.id.button_code) {
            button_code.setTextWithLayoutParams(R.string.sending_code)
        }

        vm!!.sendCode(phone, if (v.id == R.id.button_code) 0 else 1)
    }

    private fun checkPhone(phone: String): Boolean {
        if (phone.isEmpty()) {
            showMessage(R.string.input_mobile)
            return false
        }

        if (phone.length < 11 || !phone.startsWith("1")) {
            showMessage(R.string.input_valid_phone)
            return false
        }

        return true
    }

    private fun checkCode(code: String): Boolean {
        if (code.isEmpty()) {
            showMessage(R.string.input_code)
            return false
        }

        return true
    }

    private fun startMainActivity() {
        MainActivity.start(this)
        finish()
    }

//    private void scheduleRefreshTask() {
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();
//
//        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
//                RefreshTokenWorker.class, 100, TimeUnit.MINUTES)
//                .setInitialDelay(60, TimeUnit.MINUTES)
//                .setConstraints(constraints)
//                .build();
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork("tktsk",
//                ExistingPeriodicWorkPolicy.REPLACE, request);
//    }
}