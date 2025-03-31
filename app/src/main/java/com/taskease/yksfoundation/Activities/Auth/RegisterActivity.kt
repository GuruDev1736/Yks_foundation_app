package com.taskease.yksfoundation.Activities.Auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.taskease.yksfoundation.Activities.Auth.RegisterFragments.CompleteProfileFragment
import com.taskease.yksfoundation.Activities.Auth.RegisterFragments.PersonalDetailsFragment
import com.taskease.yksfoundation.Activities.Auth.RegisterFragments.SelectSocietyFragment
import com.taskease.yksfoundation.Activities.Auth.RegisterFragments.SocialMediaFragment
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = ViewPagerAdapter(this)

    }

    fun getViewPager(): ViewPager2 {
        return viewPager
    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SelectSocietyFragment()
                1 -> PersonalDetailsFragment()
                2 -> SocialMediaFragment()
                3 -> CompleteProfileFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }
}