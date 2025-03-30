package com.taskease.yksfoundation.Constant

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty

object Constant {

     fun success(context: Context, message: String) {
        Toasty.error(context, "This is an error toast.", Toast.LENGTH_SHORT, true).show();
    }

    fun error(context: Context, message: String) {
        Toasty.error(context, "This is an error toast.", Toast.LENGTH_SHORT, true).show();
    }


}