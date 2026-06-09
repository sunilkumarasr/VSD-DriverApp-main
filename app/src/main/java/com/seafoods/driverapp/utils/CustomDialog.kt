package com.seafoods.driverapp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView


class CustomDialog(ctx:Context): Dialog(ctx), DialogInterface.OnClickListener {
    lateinit var builder: AlertDialog.Builder;
    init {
        builder = AlertDialog.Builder(ctx)
    }
    override fun onClick(p0: DialogInterface?, p1: Int) {


    }
    fun showMessage(title: String?, isSingleButton: Boolean, message: String?) {
        builder.setTitle(title)
        builder.setMessage(message)
        if (isSingleButton) {
            builder.setNeutralButton("OK", this)
        } else {
            builder.setPositiveButton("Yes", this)
            builder.setNegativeButton("No", this)
        }
        builder.setCancelable(false)
        builder.show()
    }
   lateinit var mpd: Dialog
    fun showDialog(context: Activity?, cancelable: Boolean) {
        if (context == null) return
        val lyt_Inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view_lyt: View = lyt_Inflater.inflate(R.layout.dialog_loading, null)
        val imag = view_lyt.findViewById<View>(R.id.imag) as ImageView
        mpd = Dialog(context, R.style.ThemeDialogCustom)
        mpd.setContentView(view_lyt)
        mpd.setCancelable(cancelable)
        mpd.getWindow()?.setLayout(200, 200)
        mpd.setCanceledOnTouchOutside(cancelable)
        if (context == null) return
        mpd.show()
    }
    fun closeDialog() {
        /* if(animator!=null)
            animator.end();*/
        if (mpd != null && mpd.isShowing) {
            try {
                if (mpd.context == null) {
                    return
                }
                mpd.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}