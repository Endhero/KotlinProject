package com.lcd.kotlinproject.view.widget

import android.content.Context
import android.util.AttributeSet
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.utils.DateUtil
import com.lcd.kotlinproject.view.widget.DatePickerDialog.OnDateSetListener
import java.text.SimpleDateFormat
import java.util.*

class DatePickerTextView @JvmOverloads constructor(context: Context , attrs: AttributeSet? = null , defStyleAttr: Int = 0) : androidx.appcompat.widget.AppCompatTextView(context , attrs, defStyleAttr) {
    private var mOnDateSetListener: onDateSetListener? = null
    private var mOnAllCheckListener: OnAllCheckListener? = null
    private var mDatePickerDialog: DatePickerDialog
    private var mSimpleDateFormat: SimpleDateFormat

    private var mDate: Date? = null

    init {
        mDate = Date()
        mSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        mDatePickerDialog = DatePickerDialog(context)

        val ondatesetlistener: OnDateSetListener = object: OnDateSetListener {
            override fun onDateSet(nYear: Int, nMonth: Int, nDay: Int) {
                mDate = getDate(nYear, nMonth, nDay)
                text = mSimpleDateFormat?.format(mDate)
                mOnDateSetListener?.onDateSet(nYear, nMonth, nDay)
            }
        }

        val onallselectedlistener: DatePickerDialog.OnAllSelectedListener = object: DatePickerDialog.OnAllSelectedListener {
            override fun onAllSelected() {
                setText(R.string.all)
                mOnAllCheckListener?.onAllCheck()
            }
        }

        mDatePickerDialog.init(Date(), ondatesetlistener, onallselectedlistener)
        mDatePickerDialog.setTitle(R.string.please_select_date)
        mDatePickerDialog.setCanceledOnTouchOutside(true)
        mDatePickerDialog.setMinDate(DateUtil.getDate(2018, 1))

        setText(R.string.all)
    }

    fun setMaxDate(date: Date?) = mDatePickerDialog.setMaxDate(date!!)

    fun setMindate(date: Date?) = mDatePickerDialog.setMinDate(date!!)

    fun hideDay() = mDatePickerDialog.hideDay()

    fun setTitle(nTitle: Int) = mDatePickerDialog.setTitle(nTitle)

    fun getDate() = mDate

    fun setOnDateSetListener(ondatesetlistener: onDateSetListener) {
        mOnDateSetListener = ondatesetlistener
    }

    fun setOnAllCheckListener(onallchecklistener: OnAllCheckListener) {
        mOnAllCheckListener = onallchecklistener
    }

    fun setDateFormat(simpledateformat: SimpleDateFormat) {
        mSimpleDateFormat = simpledateformat
    }

    interface onDateSetListener{
        fun onDateSet(nYear: Int, nMonth: Int, nDay: Int)
    }

    interface OnAllCheckListener{
        fun onAllCheck()
    }

    private fun getDate(nYear: Int, nMonth: Int, nDay: Int): Date? {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = nYear
        calendar[Calendar.MONTH] = nMonth - 1
        calendar[Calendar.DAY_OF_MONTH] = nDay
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        return calendar.time
    }
}