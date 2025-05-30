package com.taskease.yksfoundation.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.ActivitySavePostBinding

class SavePostActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySavePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavePostBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}