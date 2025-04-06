package com.taskease.yksfoundation.Constant

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Constant {

     fun success(context: Context, message: String) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
    }

    fun error(context: Context, message: String) {
        Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
    }

    fun showDatePicker(context: Context, textView: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = sdf.format(Date(selection))
            textView.setText(selectedDate)
        }
    }

    fun callPhone(phoneNo: String, context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNo")
        }
        context.startActivity(intent)
    }



}