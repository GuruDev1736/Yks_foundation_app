package com.taskease.yksfoundation.Activities.Admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.ActivityPostApprovalBinding

class PostApprovalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPostApprovalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostApprovalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarTitle.text = "Post Approval"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@PostApprovalActivity)

    }
}