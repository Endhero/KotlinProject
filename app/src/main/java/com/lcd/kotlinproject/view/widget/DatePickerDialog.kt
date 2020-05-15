package com.lcd.kotlinproject.view.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lcd.kotlinproject.R
import java.util.*

class DatePickerDialog @JvmOverloads constructor(context: Context, themeResId: Int = 0): Dialog(context, themeResId) {
    private var m_datepicker: DatePicker
    private var m_textviewTitle: TextView
    private var m_textviewEnsure: TextView
    private var m_textviewCancel: TextView
    private var m_checkboxAll: CheckBox
    private var m_ondatesetlistener: OnDateSetListener? = null
    private var m_onallselectedlistener: OnAllSelectedListener? = null
    private var m_nWidth = 0

    init {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val displaymetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getRealMetrics(displaymetrics)

        m_nWidth = displaymetrics.widthPixels

        val view = View.inflate(context, R.layout.dialog_datepicker, null)

        m_datepicker = view.findViewById(R.id.datepicker)
        m_datepicker.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS
        m_datepicker.init(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH],null)
        m_datepicker.maxDate = calendar.time.time

        resizePicker(m_datepicker)

        m_checkboxAll = view.findViewById(R.id.checkbox_all)
        m_textviewTitle = view.findViewById(R.id.textview_title)
        m_textviewEnsure = view.findViewById(R.id.textview_ensure)
        m_textviewEnsure.setOnClickListener{
            if (m_checkboxAll.isChecked) {
                m_onallselectedlistener?.onAllSelected()
            } else {
                m_ondatesetlistener?.onDateSet(m_datepicker.year, m_datepicker.month + 1, m_datepicker.dayOfMonth)
            }
            dismiss()
        }

        m_checkboxAll.setOnCheckedChangeListener{ _, bIsCheck ->
            if (bIsCheck) {
                m_checkboxAll.setTextColor(context.getResources().getColor(R.color.colorPrimary))
                m_datepicker.isEnabled = false
            } else {
                m_checkboxAll.setTextColor(context.getResources().getColor(R.color.tipText))
                m_datepicker.isEnabled = true
            }
        }

        m_textviewCancel = view.findViewById(R.id.textview_cancel)
        m_textviewCancel.setOnClickListener { dismiss() }

        setContentView(view)
    }

    fun init(date: Date, ondatesetlistener: OnDateSetListener, onallselectedlistener: OnAllSelectedListener?){
        val calendar = Calendar.getInstance()
        calendar.time = date

        m_datepicker.init( calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH], null
        )

        m_ondatesetlistener = ondatesetlistener
        m_onallselectedlistener = onallselectedlistener
    }

    override fun setTitle(nTitle: Int) {
        m_textviewTitle.setText(nTitle)
    }

    fun setMinDate(date: Date){
        val calendar = Calendar.getInstance()
        calendar.time = date

        m_datepicker.minDate = calendar.time.time
    }

    fun setMaxDate(date: Date){
        val calendar = Calendar.getInstance()
        calendar.time = date

        m_datepicker.maxDate = calendar.time.time
    }

    fun hideDay() {
        m_datepicker.findViewById<View>(context.resources.getIdentifier("android:id/day", null, null))?.visibility = View.GONE
    }

    fun hideCheckbox() {
       m_checkboxAll?.visibility = View.GONE
    }

    override fun show() {
        super.show()

        m_checkboxAll.isChecked = false
        val layoutparams = window?.attributes
        layoutparams?.width = m_nWidth
        window?.attributes = layoutparams
    }

    private fun resizePicker(framelayout: FrameLayout) {
        val listNumberPicker: List<NumberPicker> = findNumberPicker(framelayout)

        for (numberpicker in listNumberPicker) {
            val layoutParams = numberpicker.layoutParams as LinearLayout.LayoutParams
            layoutParams.bottomMargin = 0
            setUnderLineColor(numberpicker, R.color.divider)
        }
    }

    private fun findNumberPicker(viewgroup: ViewGroup): List<NumberPicker> {
        val listNumberPicker: MutableList<NumberPicker> = ArrayList()
        var viewChild: View?

        for (i in 0 until viewgroup.childCount) {
            viewChild = viewgroup.getChildAt(i)

            if (viewChild is NumberPicker) {
                listNumberPicker.add(viewChild)
            } else if (viewChild is LinearLayout) {
                val listResult = findNumberPicker(viewChild as ViewGroup)

                if (listResult.isNullOrEmpty()) {
                    return listResult
                }
            }
        }

        return listNumberPicker
    }

    private fun setUnderLineColor(numberpicker: NumberPicker, nColor: Int) {
        val fields = NumberPicker::class.java.declaredFields
        for (field in fields) {
            if (field.name == "mSelectionDivider") {
                field.isAccessible = true
                try {
                    field[numberpicker] = ColorDrawable(context.resources.getColor(nColor))
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                break
            }
        }
    }

    interface OnDateSetListener {
        fun onDateSet(nYear: Int, nMonth: Int, nDay: Int)
    }

    interface OnAllSelectedListener {
        fun onAllSelected()
    }
}