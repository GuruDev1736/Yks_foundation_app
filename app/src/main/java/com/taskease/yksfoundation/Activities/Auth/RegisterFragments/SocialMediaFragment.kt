package com.taskease.yksfoundation.Activities.Auth.RegisterFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.taskease.yksfoundation.Activities.Auth.RegisterActivity
import com.taskease.yksfoundation.ViewModel.RegisterViewModel
import com.taskease.yksfoundation.databinding.FragmentSocialMediaBinding

class SocialMediaFragment : Fragment() {

    private lateinit var binding : FragmentSocialMediaBinding
    private lateinit var viewModel : RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSocialMediaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        binding.previous.setOnClickListener {
            val viewPager = (context as RegisterActivity).getViewPager()
            viewPager.currentItem -= 1
        }

        binding.next.setOnClickListener {

            val facebook = binding.faceBook.text.toString()
            val twitter = binding.twitterProfile.text.toString()
            val instagram = binding.instaProfile.text.toString()
            val snapchat = binding.snapChatProfile.text.toString()
            val linkedIn = binding.linkedInProfile.text.toString()

            viewModel.socialDetails(facebook,twitter,instagram,linkedIn,snapchat)
            val viewPager = (context as RegisterActivity).getViewPager()
            viewPager.currentItem += 1
        }
    }
}