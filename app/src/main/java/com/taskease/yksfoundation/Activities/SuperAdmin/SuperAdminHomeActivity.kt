package com.taskease.yksfoundation.Activities.SuperAdmin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.taskease.yksfoundation.Adapter.MenuAdapter
import com.taskease.yksfoundation.Model.MenuItem
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.ActivitySuperAdminHomeBinding


class SuperAdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuperAdminHomeBinding
    private val menuList = listOf(
        MenuItem("Add Post", R.drawable.post),
        MenuItem("Add Society", R.drawable.society),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MenuAdapter(menuList) { menuItem ->
            when (menuItem.title) {
                "Add Post" -> Toast.makeText(this, "Add Post clicked", Toast.LENGTH_SHORT).show()
                "Add Society" -> Toast.makeText(this, "Add Society clicked", Toast.LENGTH_SHORT).show()
            }
        }

        binding.menuRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.menuRecyclerView.adapter = adapter
    }
}