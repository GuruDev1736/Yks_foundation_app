package com.taskease.yksfoundation.Constant

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.taskease.yksfoundation.Activities.SuperAdmin.AddUserActivity
import com.taskease.yksfoundation.R
import es.dmoral.toasty.Toasty
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
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


    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveBase64ExcelToDownloads(context: Context, base64String: String?, fileName: String) {
        if (base64String.isNullOrEmpty()) {
            Toast.makeText(context, "Base64 string is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val decodedBytes = Base64.getDecoder().decode(base64String)

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, if (fileName.endsWith(".xlsx")) fileName else "$fileName.xlsx")
            put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val itemUri = resolver.insert(collection, contentValues)

        itemUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(decodedBytes)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            Toast.makeText(context, "Excel saved to Downloads", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
        }
    }

    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val byteArray = outputStream.toByteArray()
            android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTime(timestamp: List<Int>): String {
        // Convert timestamp to ZonedDateTime in UTC or your server's time zone
        val postTime = ZonedDateTime.of(
            timestamp[0],
            timestamp[1],
            timestamp[2],
            timestamp[3],
            timestamp[4],
            timestamp[5],
            timestamp.getOrElse(6) { 0 },
            ZoneOffset.UTC // adjust if your backend uses a different zone
        )

        // Get current time in the same time zone
        val now = ZonedDateTime.now(ZoneOffset.UTC)

        val duration = Duration.between(postTime, now)

        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            duration.toDays() == 1L -> "Yesterday"
            duration.toDays() < 7 -> "${duration.toDays()} days ago"
            else -> postTime.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTimeFromMillis(timestampMillis: Long): String {
        return try {
            val postTime = Instant.ofEpochMilli(timestampMillis).atZone(ZoneOffset.UTC)
            val now = ZonedDateTime.now(ZoneOffset.UTC)
            val duration = Duration.between(postTime, now)

            when {
                duration.toMinutes() < 1 -> "Just now"
                duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
                duration.toHours() < 24 -> "${duration.toHours()} hours ago"
                duration.toDays() == 1L -> "Yesterday"
                duration.toDays() < 7 -> "${duration.toDays()} days ago"
                duration.toDays() < 30 -> "${duration.toDays() / 7} weeks ago"
                duration.toDays() < 365 -> "${duration.toDays() / 30} months ago"
                else -> "${duration.toDays() / 365} years ago"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun showImageChooserDialog(
        context: Context,
        onCameraClick: () -> Unit,
        onGalleryClick: () -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.capture_options, null)
        dialog.setContentView(view)

        val camera = view.findViewById<View>(R.id.camera)
        val gallery = view.findViewById<View>(R.id.gallery)

        camera.setOnClickListener {
            onCameraClick()
            dialog.dismiss()
        }

        gallery.setOnClickListener {
            onGalleryClick()
            dialog.dismiss()
        }

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}