package com.taskease.yksfoundation.Activities.Auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.taskease.yksfoundation.Activities.Auth.ForgotPassword.EmailActivity
import com.taskease.yksfoundation.Activities.Auth.RegisterFragments.CompleteProfileFragment
import com.taskease.yksfoundation.Activities.Auth.RegisterFragments.PersonalDetailsFragment
import com.taskease.yksfoundation.Activities.Auth.RegisterFragments.SelectSocietyFragment
import com.taskease.yksfoundation.Activities.Auth.RegisterFragments.SocialMediaFragment
import com.taskease.yksfoundation.Activities.HomeActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.SuperAdminHomeActivity
import com.taskease.yksfoundation.Adapter.AuthenticationAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.RequestModel.LoginRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.LoginResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityLoginBinding
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabs = listOf("Sign In", "Sign Up")
        val fragments = listOf(LoginFragment(), SelectSocietyFragment())
        val adapter = AuthenticationAdapter(this, fragments)

        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView = createTabView(tabs[position])
        }.attach()

        highlightTab(binding.tabLayout.getTabAt(0))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                highlightTab(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                unhighlightTab(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 1) {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }
            }
        })
    }

    fun createTabView(title: String): View {
        val view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
        val tabText = view.findViewById<TextView>(R.id.tabText)
        tabText.text = title
        return view
    }

    fun highlightTab(tab: TabLayout.Tab?) {
        tab?.customView?.findViewById<TextView>(R.id.tabText)?.apply {
            setTextColor(Color.WHITE)
            background = ContextCompat.getDrawable(this@LoginActivity, R.drawable.bg_tab_active)
        }
    }

    fun unhighlightTab(tab: TabLayout.Tab?) {
        tab?.customView?.findViewById<TextView>(R.id.tabText)?.apply {
            setTextColor(Color.parseColor("#2980B9"))
            background = null
        }
    }
}

