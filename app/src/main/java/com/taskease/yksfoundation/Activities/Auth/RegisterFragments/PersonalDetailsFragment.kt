package com.taskease.yksfoundation.Activities.Auth.RegisterFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import com.taskease.yksfoundation.Activities.Auth.RegisterActivity
import com.taskease.yksfoundation.ViewModel.RegisterViewModel
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.databinding.FragmentPersonalDetailsBinding


class PersonalDetailsFragment : Fragment() {

    private lateinit var binding : FragmentPersonalDetailsBinding
    private lateinit var viewModel : RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        binding.anniversaryDate.setOnClickListener {
            Constant.showDatePicker(requireContext(),binding.anniversaryDate)
        }

        binding.birthDate.setOnClickListener {
            Constant.showDatePicker(requireContext(),binding.birthDate)
        }

        val selectedVoterId = binding.voter.checkedRadioButtonId
        val voterStatus = if (selectedVoterId != -1) {
            binding.voter.findViewById<RadioButton>(selectedVoterId).text.toString()
        } else {
            "Not Selected" // Default value if no selection
        }
        val selectedMemberId = binding.member.checkedRadioButtonId
        val memberStatus = if (selectedMemberId != -1) {
            binding.member.findViewById<RadioButton>(selectedMemberId).text.toString()
        } else {
            "Not Selected" // Default value if no selection
        }

        binding.previous.setOnClickListener {
            val viewPager = (context as RegisterActivity).getViewPager()
            viewPager.currentItem -= 1
        }

        binding.next.setOnClickListener {

            val name = binding.fullName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val address = binding.address.text.toString()
            val gender = binding.gender.selectedItem.toString()
            val designation = binding.designation.text.toString()
            val birthDate = binding.birthDate.text.toString()
            val anniversaryDate = binding.anniversaryDate.text.toString()
            val phoneNumber = binding.phoneNo.text.toString()
            val whatsAppNumber = binding.whatsappNo.text.toString()
            val vStatus = voterStatus.equals("Yes", ignoreCase = true)
            val mStatus = memberStatus.equals("Yes", ignoreCase = true)

            if (valid(name,email,password,address,gender,designation,birthDate,anniversaryDate,phoneNumber,whatsAppNumber,voterStatus,memberStatus))
            {
                viewModel.personalDetails(name,email,password,gender,designation,anniversaryDate,birthDate,phoneNumber,whatsAppNumber,vStatus,mStatus,address)
                val viewPager = (context as RegisterActivity).getViewPager()
                viewPager.currentItem += 1
            }
        }
    }

    private fun valid(name: String, email: String, password: String, address: String, gender: String, designation: String, birthDate: String, anniversaryDate: String, phoneNumber: String, whatsAppNumber: String, voterStatus: String, memberStatus: String): Boolean {
        if (name.isEmpty()) {
            binding.fullName.error = "Name is required"
            binding.fullName.requestFocus()
            return false
        }
        if (email.isEmpty()) {
            binding.email.error = "Email is required"
            binding.email.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            binding.password.error = "Password is required"
            binding.password.requestFocus()
            return false
        }
        if (address.isEmpty()) {
            binding.address.error = "Address is required"
            binding.address.requestFocus()
            return false
        }
        if (gender.isEmpty()) {
            Constant.error(requireContext(),"Please Select Gender")
            return false
        }
        if (phoneNumber.isEmpty()) {
            binding.phoneNo.error = "Phone Number is required"
            binding.phoneNo.requestFocus()
            return false
        }
        return true
    }
}