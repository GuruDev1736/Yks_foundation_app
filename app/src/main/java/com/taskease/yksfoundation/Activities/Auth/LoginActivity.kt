package com.taskease.yksfoundation.Activities.Auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.taskease.yksfoundation.Activities.HomeActivity
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.RequestModel.LoginRequestModel
import com.taskease.yksfoundation.Model.RequestModel.UserRegisterRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.LoginResponse
import com.taskease.yksfoundation.Model.ResponseModel.LoginResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.UserRegisterResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityLoginBinding
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
                                if (data.CONTENT.enabled) {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    Constant.error(
                                        this@LoginActivity,
                                        "Your account is disabled , Get Approved by admin"
                                    )
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
                        Constant.error(this@LoginActivity, "Network error. Please check your connection.")
                        Log.e("LoginActivity", "Network error", t)
                        return
                    }

                    if (t is HttpException) {
                        val errorBody = t.response()?.errorBody()?.string()
                        if (!errorBody.isNullOrEmpty()) {
                            try {
                                val jsonObject = JSONObject(errorBody)
                                val errorMessage = jsonObject.optString("MSG", "Invalid Email or Password") // Extract MSG field
                                Constant.error(this@LoginActivity, errorMessage)
                            } catch (e: JSONException) {
                                Constant.error(this@LoginActivity, "Error parsing response")
                            }
                        }
                    } else {
                        Constant.error(this@LoginActivity, "Invalid Email or Password")
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

