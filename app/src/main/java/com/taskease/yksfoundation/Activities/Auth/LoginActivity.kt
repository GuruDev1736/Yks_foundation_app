package com.taskease.yksfoundation.Activities.Auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.taskease.yksfoundation.Activities.Auth.ForgotPassword.EmailActivity
import com.taskease.yksfoundation.Activities.HomeActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.SuperAdminHomeActivity
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.RequestModel.LoginRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.LoginResponseModel
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

        binding.email.setText(SharedPreferenceManager.getString(SharedPreferenceManager.EMAIL))
        binding.password.setText(SharedPreferenceManager.getString(SharedPreferenceManager.PASSWORD))

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, EmailActivity::class.java))
        }

        binding.login.setOnClickListener {

            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (valid(email, password)) {
                callLoginApi(email, password)
            }
        }
    }

    private fun valid(email: String, password: String): Boolean {
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
        return true
    }

    private fun callLoginApi(email: String, password: String) {
        val progress = CustomProgressDialog(this)
        progress.show()

        try {
            val model = LoginRequestModel(email, password)

            RetrofitInstance.getInstance().login(model).enqueue(object :
                Callback<LoginResponseModel> {
                override fun onResponse(
                    call: Call<LoginResponseModel>,
                    response: Response<LoginResponseModel>
                ) {
                    progress.dismiss()

                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {

                                SharedPreferenceManager.saveString(SharedPreferenceManager.EMAIL,email)
                                SharedPreferenceManager.saveString(SharedPreferenceManager.PASSWORD,password)
                                SharedPreferenceManager.saveInt(SharedPreferenceManager.USER_ID,data.CONTENT.userId)
                                SharedPreferenceManager.saveString(SharedPreferenceManager.TOKEN,"Bearer "+data.CONTENT.token)
                                SharedPreferenceManager.saveString(SharedPreferenceManager.ROLE,data.CONTENT.userRole)
                                SharedPreferenceManager.saveInt(SharedPreferenceManager.SOCIETY_ID,data.CONTENT.societyId)

                                if (data.CONTENT.enabled) {
                                    if (data.CONTENT.userRole == "ROLE_SUPER_ADMIN")
                                    {
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                SuperAdminHomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                    else if (data.CONTENT.userRole == "ROLE_ADMIN")
                                    {
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                    }
                                    else if (data.CONTENT.userRole == "ROLE_USER")
                                    {
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                    }
                                } else {
                                    startActivity(Intent(this@LoginActivity, ApprovalScreen::class.java))
                                }
                            } else {
                                Constant.error(this@LoginActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@LoginActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@LoginActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                    progress.dismiss()

                    if (t is IOException) {
                        Constant.error(
                            this@LoginActivity,
                            "Network error. Please check your connection."
                        )
                        Log.e("LoginActivity", "Network error", t)
                        return
                    }
                    Log.e("LoginActivity", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            progress.dismiss()
        }
    }
}

