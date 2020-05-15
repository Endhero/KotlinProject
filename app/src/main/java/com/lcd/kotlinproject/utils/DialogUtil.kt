package com.lcd.kotlinproject.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.widget.TextView
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.view.widget.PhotoViewDialog
import com.yuwell.androidbase.tool.DensityUtil

object DialogUtil {
    fun showConfirmDialog(mContext: Context?, nTitle: Int, strMessage: String?) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(nTitle)
        builder.setMessage(strMessage)
        builder.setPositiveButton(R.string.ensure, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        builder.create().show()
    }

    fun showConfirmDialog(mContext: Context?, nTitle: Int, strMessage: String?, listenerpositive: DialogInterface.OnClickListener?) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(nTitle)
        builder.setMessage(strMessage)
        builder.setPositiveButton(R.string.ensure, listenerpositive)
        builder.create().show()
    }

    fun showConfirmDialog(mContext: Context?, nTitle: Int, strMessage: String?, listenerPositive: DialogInterface.OnClickListener?, listenerNegative: DialogInterface.OnClickListener?) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(nTitle)
        builder.setMessage(strMessage)
        builder.setPositiveButton(R.string.ensure, listenerPositive)
        builder.setNegativeButton(R.string.cancel, listenerNegative)
        builder.create().show()
    }

    fun showListDialog(context: Context, title: Int, data: Array<String?>?, nSelected: Int, itemClickListener: DialogInterface.OnClickListener?,
                       negativeClickListener: DialogInterface.OnClickListener?, positiveClickListener: DialogInterface.OnClickListener?) {
        val strTitle = context.getString(title)
        val strCancel = context.getString(R.string.cancel)
        val strEnsure = context.getString(R.string.ensure)
        val textviewTitle = TextView(context)
        textviewTitle.setText(
            TextUtil.getTextWithSizeAndColor(
                strTitle,
                18,
                true,
                context.resources.getColor(R.color.normalText),
                0,
                strTitle.length
            )
        )
        textviewTitle.setPadding(0, DensityUtil.dip2px(context, 13f), 0, 0)
        textviewTitle.gravity = Gravity.CENTER
        val builder = AlertDialog.Builder(context)
        builder.setCustomTitle(textviewTitle)
        builder.setSingleChoiceItems(data, nSelected, itemClickListener)
        builder.setNegativeButton(strCancel, negativeClickListener)
        builder.setPositiveButton(strEnsure, positiveClickListener)
        builder.show()
    }

    fun showPhotoViewDialog(context: Context, strPath: String?) {
        val photoviewdialog = PhotoViewDialog(context)
        photoviewdialog.setImagePath(strPath)
        photoviewdialog.show()
    }
}