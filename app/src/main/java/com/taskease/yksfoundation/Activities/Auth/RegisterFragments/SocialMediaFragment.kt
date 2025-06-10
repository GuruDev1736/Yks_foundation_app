package com.taskease.yksfoundation.Activities.Auth.RegisterFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.taskease.yksfoundation.Activities.Auth.RegisterActivity
import com.taskease.yksfoundation.ViewModel.RegisterViewModel
import com.taskease.yksfoundation.databinding.FragmentSocialMediaBinding

class SocialMediaFragment : Fragment() {

    private lateinit var binding: FragmentSocialMediaBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSocialMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        binding.previous.setOnClickListener {
            val viewPager = (context as RegisterActivity).getViewPager()
            viewPager.currentItem -= 1
        }

        binding.faceBook.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isNotEmpty() && !isValidSocialMediaUrl(input)) {
                    binding.faceBook.error = "Invalid URL"
                } else {
                    binding.faceBook.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.twitterProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isNotEmpty() && !isValidSocialMediaUrl(input)) {
                    binding.twitterProfile.error = "Invalid URL"
                } else {
                    binding.twitterProfile.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.instaProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isNotEmpty() && !isValidSocialMediaUrl(input)) {
                    binding.instaProfile.error = "Invalid URL"
                } else {
                    binding.instaProfile.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.snapChatProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isNotEmpty() && !isValidSocialMediaUrl(input)) {
                    binding.snapChatProfile.error = "Invalid URL"
                } else {
                    binding.snapChatProfile.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.linkedInProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isNotEmpty() && !isValidSocialMediaUrl(input)) {
                    binding.linkedInProfile.error = "Invalid URL"
                } else {
                    binding.linkedInProfile.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.next.setOnClickListener {

            val facebook = binding.faceBook.text.toString()
            val twitter = binding.twitterProfile.text.toString()
            val instagram = binding.instaProfile.text.toString()
            val snapchat = binding.snapChatProfile.text.toString()
            val linkedIn = binding.linkedInProfile.text.toString()


            viewModel.socialDetails(facebook, twitter, instagram, linkedIn, snapchat)
            val viewPager = (context as RegisterActivity).getViewPager()
            viewPager.currentItem += 1
        }
    }

    fun isValidSocialMediaUrl(url: String): Boolean {
        val validUrlPattern = Regex(
            pattern = "^(https?|ftp)://[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*\$",
            option = RegexOption.IGNORE_CASE
        )
        return validUrlPattern.matches(url.trim())
    }

}