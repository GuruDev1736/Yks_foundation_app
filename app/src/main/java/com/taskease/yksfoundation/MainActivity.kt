package com.taskease.yksfoundation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.permissionx.guolindev.PermissionX
import com.taskease.yksfoundation.Activities.Auth.LoginActivity
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {



    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        SharedPreferenceManager.init(this@MainActivity)

        PermissionX.init(this)
            .permissions(android.Manifest.permission.CAMERA , android.Manifest.permission.CALL_PHONE)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Handler(Looper.getMainLooper()).postDelayed(object : Runnable{
                        override fun run() {
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            finish()
                        }
                    },1000)
                } else {
                    Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }


    }
}