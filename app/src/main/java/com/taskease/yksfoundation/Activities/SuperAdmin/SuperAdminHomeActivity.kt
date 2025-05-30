package com.taskease.yksfoundation.Activities.SuperAdmin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskease.yksfoundation.Activities.Auth.LoginActivity
import com.taskease.yksfoundation.Activities.CreatePostActivity
import com.taskease.yksfoundation.Adapter.MenuAdapter
import com.taskease.yksfoundation.Model.MenuItem
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.ActivitySuperAdminHomeBinding


class SuperAdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuperAdminHomeBinding
    private val menuList = listOf(
        MenuItem("Add Post", R.drawable.image_selection),
        MenuItem("Add Society", R.drawable.society_2),
        MenuItem("LogOut",R.drawable.logout)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MenuAdapter(menuList) { menuItem ->
            when (menuItem.title) {
                "Add Post" -> startActivity(Intent(this@SuperAdminHomeActivity,
                    CreatePostActivity::class.java).putExtra("isSuperAdmin",true))
                "Add Society" -> startActivity(Intent(this@SuperAdminHomeActivity,
                    AddSocietyActivity::class.java))
                "LogOut" -> showDialog()
            }
        }

        binding.menuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.menuRecyclerView.adapter = adapter
    }

    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                startActivity(Intent(this@SuperAdminHomeActivity, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}