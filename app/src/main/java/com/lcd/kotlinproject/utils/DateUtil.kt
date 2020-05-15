package com.lcd.kotlinproject.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chen on 2015/9/1.
 */
object DateUtil {
    private val ymdhms = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val ym = SimpleDateFormat("yyyy-MM")
    private val ymd = SimpleDateFormat("yyyy-MM-dd")
    private val mdhm = SimpleDateFormat("MM-dd HH:mm")
    private const val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private val mCalendar = Calendar.getInstance()

    fun parseCustomString(date: String, pattern: String): Date? {
        return try {
            val sdf = SimpleDateFormat(pattern)
            sdf.isLenient = false
            sdf.parse(date)
        } catch (var2: ParseException) {
            var2.printStackTrace()
            null
        }
    }

    fun formatYMDHMS(date: Date): String {
        return ymdhms.format(date)
    }

    fun formatYM(date: Date): String {
        return ym.format(date)
    }

    fun formatYMD(date: Date): String {
        return ymd.format(date)
    }

    fun formatMDHM(date: Date): String {
        return mdhm.format(date)
    }

    val currentTimeStamp: String
        get() {
            val simpledateformat = SimpleDateFormat(TIMESTAMP_FORMAT)
            return simpledateformat.format(Date())
        }

    fun add(date: Date, field: Int, value: Int): Date {
        mCalendar.time = date
        mCalendar.add(field, value)
        return mCalendar.time
    }

    fun formatCustomDate(date: Date, pattern: String): String {
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(date)
    }

    fun setTime(date: Date) {
        mCalendar.time = date
    }

    val year: Int
        get() = mCalendar[Calendar.YEAR]

    val month: Int
        get() = mCalendar[Calendar.MONTH]

    fun getYear(date: Date): Int {
        setTime(date)
        return year
    }

    fun getMonth(date: Date): Int {
        setTime(date)
        return month + 1
    }

    fun getFirstDayOfMonth(date: Date?): Date {
        mCalendar.time = date
        mCalendar[Calendar.HOUR_OF_DAY] = 0
        mCalendar[Calendar.MINUTE] = 0
        mCalendar[Calendar.SECOND] = 0
        mCalendar[Calendar.MILLISECOND] = 0
        mCalendar[Calendar.DAY_OF_MONTH] = mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        return mCalendar.time
    }

    fun getLastDayOfMonth(date: Date?): Date {
        mCalendar.time = date
        mCalendar[Calendar.HOUR_OF_DAY] = 0
        mCalendar[Calendar.MINUTE] = 0
        mCalendar[Calendar.SECOND] = 0
        mCalendar[Calendar.MILLISECOND] = 0
        mCalendar[Calendar.DAY_OF_MONTH] = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return mCalendar.time
    }

    fun getHourOfDay(date: Date): Int {
        setTime(date)
        return mCalendar[Calendar.HOUR_OF_DAY]
    }

    fun getDayofYear(date: Date): Int {
        setTime(date)
        return mCalendar[Calendar.DAY_OF_YEAR]
    }

    fun getDayOfMonth(date: Date): Int {
        setTime(date)
        return mCalendar[Calendar.DAY_OF_MONTH]
    }

    fun getDate(nYear: Int, nMonth: Int): Date {
        mCalendar[Calendar.YEAR] = nYear
        mCalendar[Calendar.MONTH] = nMonth - 1
        mCalendar[Calendar.DAY_OF_MONTH] = 1
        mCalendar[Calendar.HOUR_OF_DAY] = 0
        mCalendar[Calendar.MINUTE] = 0
        mCalendar[Calendar.SECOND] = 0
        mCalendar[Calendar.MILLISECOND] = 0
        return mCalendar.time
    }

    fun getDate(nYear: Int, nMonth: Int, nDay: Int): Date {
        mCalendar[Calendar.YEAR] = nYear
        mCalendar[Calendar.MONTH] = nMonth - 1
        mCalendar[Calendar.DAY_OF_MONTH] = nDay
        mCalendar[Calendar.HOUR_OF_DAY] = 0
        mCalendar[Calendar.MINUTE] = 0
        mCalendar[Calendar.SECOND] = 0
        mCalendar[Calendar.MILLISECOND] = 0
        return mCalendar.time
    }
}