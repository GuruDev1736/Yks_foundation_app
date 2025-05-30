package com.taskease.yksfoundation.Activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.taskease.yksfoundation.Activities.Admin.AdminHomeFragment
import com.taskease.yksfoundation.Activities.Admin.ProfileFragment
import com.taskease.yksfoundation.Activities.Admin.RequestFragment
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPreferenceManager.init(this@HomeActivity)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val role = SharedPreferenceManager.getString(SharedPreferenceManager.ROLE,"")

        if (role == "ROLE_USER") {
            bottomNavigationView.menu.clear()
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu)
            setupUserNavigation()
            if (savedInstanceState == null) {
                bottomNavigationView.selectedItemId = R.id.nav_home
            }
        } else if (role == "ROLE_ADMIN") {
            bottomNavigationView.menu.clear()
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_admin)
            setupAdminNavigation()
            if (savedInstanceState == null) {
                bottomNavigationView.selectedItemId = R.id.nav_home
            }
        }
    }


    private fun setupUserNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AdminHomeFragment()).commit()
                    true
                }
                R.id.nav_createPost -> {
                    startActivity(Intent(this@HomeActivity, CreatePostActivity::class.java).putExtra("isSuperAdmin",false))
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupAdminNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AdminHomeFragment()).commit()
                    true
                }
                R.id.nav_createPost -> {
                    startActivity(Intent(this@HomeActivity, CreatePostActivity::class.java).putExtra("isSuperAdmin",false))
                    true
                }
                R.id.nav_requests -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, RequestFragment()).commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }
}