package com.taskease.yksfoundation.Activities.Auth.ForgotPassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityValidateOtpactivityBinding
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValidateOTPActivity : AppCompatActivity() {

    private lateinit var binding : ActivityValidateOtpactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidateOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submit.setOnClickListener {
            val otp  = binding.otpView.otp.toString()

            if (otp.isEmpty())
            {
                binding.otpView.showError()
                return@setOnClickListener
            }
            else
            {
                callValidateOtp(otp)
            }
        }
    }


    private fun callValidateOtp(otp : String)
    {

        val email = intent.getStringExtra("email").toString()

        val progress = CustomProgressDialog(this)
        progress.show()

        try {
            RetrofitInstance.getInstance().validateOtp(email,otp).enqueue(object :
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
                                startActivity(Intent(this@ValidateOTPActivity,
                                    ChangePasswordActivity::class.java)
                                    .putExtra("email",email))
                            } else {
                                Constant.error(this@ValidateOTPActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@ValidateOTPActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@ValidateOTPActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UniversalModel>, t: Throwable) {
                    progress.dismiss()
                    if (t is IOException) {
                        Constant.error(this@ValidateOTPActivity, "Network error")
                    } else {
                        Constant.error(this@ValidateOTPActivity, "Error: ${t.message}")
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            progress.dismiss()
        }
    }
}