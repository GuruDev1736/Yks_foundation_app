package com.taskease.yksfoundation.Activities.Auth.ForgotPassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.taskease.yksfoundation.Activities.Auth.LoginActivity
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityChangePasswordBinding
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submit.setOnClickListener {

            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (valid(password,confirmPassword))
            {
                callChangePassword(confirmPassword)
            }
        }
    }

    private fun callChangePassword(confirmPassword: String) {

        val email = intent.getStringExtra("email").toString()

        val progress = CustomProgressDialog(this)
        progress.show()

        try {
            RetrofitInstance.getInstance().changePassword(email,confirmPassword).enqueue(object :
                Callback<UniversalModel> {
                override fun onResponse(
                    call: Call<UniversalModel>,
                    response: Response<UniversalModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                Constant.success(this@ChangePasswordActivity, data.MSG)
                                val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Constant.error(this@ChangePasswordActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@ChangePasswordActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@ChangePasswordActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UniversalModel>, t: Throwable) {
                    progress.dismiss()
                    if (t is IOException) {
                        Constant.error(this@ChangePasswordActivity, "Network error")
                    } else {
                        Constant.error(this@ChangePasswordActivity, "Error: ${t.message}")
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            progress.dismiss()
        }
    }

    private fun valid(password: String, confirmPassword: String): Boolean {
        if (password.isEmpty())
        {
            binding.password.error = "Please enter password"
            binding.password.requestFocus()
            return false
        }
        if (confirmPassword.isEmpty())
        {
            binding.confirmPassword.error = "Please enter confirm password"
            binding.confirmPassword.requestFocus()
            return false

        }
        if (password != confirmPassword)
        {
            binding.confirmPassword.error = "Password does not match"
            binding.confirmPassword.requestFocus()
            return false
        }
        return true
    }
}