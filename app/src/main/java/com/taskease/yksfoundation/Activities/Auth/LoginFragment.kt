package com.taskease.yksfoundation.Activities.Auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.taskease.yksfoundation.Activities.Auth.ForgotPassword.EmailActivity
import com.taskease.yksfoundation.Activities.HomeActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.SuperAdminHomeActivity
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.RequestModel.LoginRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.LoginResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-fill saved credentials
        binding.email.setText(SharedPreferenceManager.getString(SharedPreferenceManager.EMAIL))
        binding.password.setText(SharedPreferenceManager.getString(SharedPreferenceManager.PASSWORD))

        // Forgot Password
        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(requireContext(), EmailActivity::class.java))
        }

        // Login Button
        binding.login.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (valid(email, password)) {
                callLoginApi(email, password)
            }
        }
    }

    private fun valid(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.email.error = "Email is required"
                binding.email.requestFocus()
                false
            }
            password.isEmpty() -> {
                binding.password.error = "Password is required"
                binding.password.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun callLoginApi(email: String, password: String) {
        val progress = CustomProgressDialog(requireContext())
        progress.show()

        val model = LoginRequestModel(email, password)
        RetrofitInstance.getInstance().login(model).enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: Response<LoginResponseModel>
            ) {
                progress.dismiss()

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        if (data.STS == "200") {

                            // Save user data
                            SharedPreferenceManager.saveString(SharedPreferenceManager.EMAIL, email)
                            SharedPreferenceManager.saveString(SharedPreferenceManager.PASSWORD, password)
                            SharedPreferenceManager.saveInt(SharedPreferenceManager.USER_ID, data.CONTENT.userId)
                            SharedPreferenceManager.saveString(SharedPreferenceManager.TOKEN, "Bearer ${data.CONTENT.token}")
                            SharedPreferenceManager.saveString(SharedPreferenceManager.ROLE, data.CONTENT.userRole)
                            SharedPreferenceManager.saveInt(SharedPreferenceManager.SOCIETY_ID, data.CONTENT.societyId)
                            SharedPreferenceManager.saveString(SharedPreferenceManager.USER_NAME,data.CONTENT.fullName)

                            // Navigate to next activity
                            if (data.CONTENT.enabled) {
                                when (data.CONTENT.userRole) {
                                    "ROLE_SUPER_ADMIN" -> {
                                        startActivity(Intent(requireContext(), SuperAdminHomeActivity::class.java))
                                        requireActivity().finish()
                                    }
                                    "ROLE_ADMIN", "ROLE_USER" -> {
                                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                                        requireActivity().finish()
                                    }
                                }
                            } else {
                                startActivity(Intent(requireContext(), ApprovalScreen::class.java))
                            }

                        } else {
                            Constant.error(requireContext(), data.MSG)
                        }
                    } else {
                        Constant.error(requireContext(), "No data received from server.")
                    }
                } else {
                    Constant.error(requireContext(), "Unexpected response from server.")
                    Log.e("LoginFragment", "Response Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                progress.dismiss()

                if (t is IOException) {
                    Constant.error(requireContext(), "Network error. Please check your connection.")
                    Log.e("LoginFragment", "Network failure", t)
                } else {
                    Constant.error(requireContext(), "Something went wrong. Please try again.")
                    Log.e("LoginFragment", "Unexpected failure", t)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // prevent memory leaks
    }
}