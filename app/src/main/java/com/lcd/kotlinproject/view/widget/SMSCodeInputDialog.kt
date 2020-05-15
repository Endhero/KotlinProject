package com.lcd.kotlinproject.view.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.lcd.base.exception.GlobalError
import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.view.impl.base.ErrorObserver
import com.lcd.kotlinproject.view.impl.device.DeviceStatusActivity
import com.lcd.kotlinproject.vm.LoginViewModel

/**
 * Created by Chen on 2020/3/19.
 */
class SMSCodeInputDialog : DialogFragment(), View.OnKeyListener {
    @BindView(R.id.text_mobile)
    lateinit var textMobile: TextView
    @BindView(R.id.button_code)
    lateinit var buttonCode: AuthCodeButton
    @BindView(R.id.edit_code_0)
    lateinit var editCode0: EditText
    @BindView(R.id.edit_code_1)
    lateinit var editCode1: EditText
    @BindView(R.id.edit_code_2)
    lateinit var editCode2: EditText
    @BindView(R.id.edit_code_3)
    lateinit var editCode3: EditText
    @BindView(R.id.edit_code_4)
    lateinit var editCode4: EditText


    private val ids = intArrayOf(
        R.id.edit_code_0,
        R.id.edit_code_1,
        R.id.edit_code_2,
        R.id.edit_code_3,
        R.id.edit_code_4
    )

    companion object{
        private const val TAG = "SMSCodeInputDialog"
        private val instance : SMSCodeInputDialog = SMSCodeInputDialog()

        fun getInstance() = instance

        fun showDialog(fm: FragmentManager, mobile: String, command: String) {
            val args = Bundle()
            args.putString("mobile", mobile)
            args.putString("command", command)

            instance.arguments = args
            instance.show(fm, TAG)
        }

        fun clear() {
            instance.code = arrayOfNulls(5)
            instance.editCode0.text = null
            instance.editCode1.text = null
            instance.editCode2.text = null
            instance.editCode3.text = null
            instance.editCode4.text = null
        }
    }

    private var code = arrayOfNulls<String>(5)
    private var vm: LoginViewModel = ViewModelProviders.of(this).get<LoginViewModel>(LoginViewModel::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sms_code_input, container, false)
        ButterKnife.bind(this, view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textMobile.text = arguments?.getString("mobile")
        editCode1.setOnKeyListener(this)
        editCode2.setOnKeyListener(this)
        editCode3.setOnKeyListener(this)
        editCode4.setOnKeyListener(this)

        vm.getSendCodeRetLiveData().observe(this, Observer<Ret<*>>{ ret ->
            if (ret.success) {
                buttonCode.startCountDown()
            } else {
                buttonCode.stopCountDown()
            }
        })

        vm.globalErrorLiveData.observe(this, object : ErrorObserver(context!!) {
            override fun onChanged(error: GlobalError) {
                buttonCode.setTextWithLayoutParams("立即发送")
                super.onChanged(error)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val dm = context!!.resources.displayMetrics
        val width = dm.widthPixels
        val window = dialog?.window
        
        window?.setLayout((width * 0.85f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        
        editCode0.post {
            editCode0.requestFocus()
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.showSoftInput( editCode0, InputMethodManager.SHOW_IMPLICIT)
        }
        
        dialog?.setCancelable(false)
    }

    @OnClick(R.id.button_close)
    fun onClick() {
        dismiss()
    }

    @OnClick(R.id.button_code)
    fun onCodeButtonClick(v: View?) {
        val phone = arguments?.getString("mobile")

        if (phone.isNullOrBlank()) {
            return
        }

        vm.sendCode(phone, 2)
    }

    @OnTextChanged(value = [R.id.edit_code_0, R.id.edit_code_1, R.id.edit_code_2, R.id.edit_code_3, R.id.edit_code_4],
        callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun afterTextChanged(s: Editable) {
        val currentFocus = dialog!!.currentFocus as? EditText ?: return
        val mEditText = currentFocus
        val code = getCode()

        setTextAtPosition(mEditText, s.toString())

        if (!TextUtils.isEmpty(code)) {
            val intent = Intent(context, DeviceStatusActivity::class.java)
            intent.putExtra("code", code)
            intent.putExtra("command", arguments!!.getString("command"))

            startActivity(intent)
        } else {
            if (s.length == 1) {
                val next =
                    dialog!!.findViewById<EditText>(mEditText.nextFocusDownId)
                next?.requestFocus() ?: focusEmptyEditText()
            }
            if (s.isEmpty()) {
                focusPrevious(mEditText)
            }
        }
    }

    private fun focusPrevious(mEditText: EditText) {
        val previous = dialog!!.findViewById<EditText>(mEditText.nextFocusUpId)
        if (previous != null) {
            previous.requestFocus()
            previous.setSelection(previous.text.length)
        }
    }

    private fun focusEmptyEditText() {
        for (i in code.indices) {
            if (TextUtils.isEmpty(code[i])) {
                val editText =
                    dialog!!.findViewById<EditText>(ids[i])
                editText?.requestFocus()
            }
        }
    }

    private fun setTextAtPosition(editText: EditText, text: String) {
        for (i in ids.indices) {
            if (editText.id == ids[i]) {
                code[i] = text
            }
        }
    }

    private fun getCode(): String? {
        val builder = StringBuilder()
        for (s in code) {
            if (TextUtils.isEmpty(s)) {
                return null
            } else {
                builder.append(s)
            }
        }
        return builder.toString()
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (v is EditText) {
            if (v.text.isEmpty() && keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                focusPrevious(v)
                return true
            }
        }
        return false
    }
}