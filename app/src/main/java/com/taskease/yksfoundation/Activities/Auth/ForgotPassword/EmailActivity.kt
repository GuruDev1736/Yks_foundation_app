package com.taskease.yksfoundation.Activities.Auth.ForgotPassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityEmailBinding
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submit.setOnClickListener {
            val email = binding.email.text.toString()
            if (valid(email))
            {
                callSendOtp(email)
            }
        }
    }

    private fun callSendOtp(email : String)
    {
        val progress = CustomProgressDialog(this)
        progress.show()

        try {
            RetrofitInstance.getInstance().sendOtp(email).enqueue(object :
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
                                startActivity(Intent(this@EmailActivity, ValidateOTPActivity::class.java)
                                    .putExtra("email",email))
                            } else {
                                Constant.error(this@EmailActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@EmailActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@EmailActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UniversalModel>, t: Throwable) {
                    progress.dismiss()
                    if (t is IOException) {
                        Constant.error(this@EmailActivity, "Network error")
                    } else {
                        Constant.error(this@EmailActivity, "Error: ${t.message}")
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            progress.dismiss()
        }
    }

    private fun valid(email : String) : Boolean{

        if (email.isEmpty())
        {
            binding.email.error = "Email is required"
            binding.email.requestFocus()
            return false
        }
        return true
    }
}