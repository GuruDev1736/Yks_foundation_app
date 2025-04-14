package com.taskease.yksfoundation.Activities.Admin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskease.yksfoundation.Adapter.PostAdapter
import com.taskease.yksfoundation.Adapter.UserApprovalAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllUserDisabledResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityUserApprovalBinding
import retrofit2.Callback
import retrofit2.Response

class UserApproval : AppCompatActivity() {

    private lateinit var binding : ActivityUserApprovalBinding
    private lateinit var adapter : UserApprovalAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserApprovalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarTitle.text = "User Approval"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@UserApproval)
        getAllDisabledUser()

    }

    private fun getAllDisabledUser()
    {
        val progress = CustomProgressDialog(this@UserApproval)
        progress.show()

        val societyId = SharedPreferenceManager.getInt(SharedPreferenceManager.SOCIETY_ID)

        try {
                RetrofitInstance.getHeaderInstance().disableUser(societyId).enqueue(object :
                    Callback<GetAllUserDisabledResponseModel> {
                    override fun onResponse(
                        call: retrofit2.Call<GetAllUserDisabledResponseModel>,
                        response: Response<GetAllUserDisabledResponseModel>
                    ) {
                        progress.dismiss()
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                if (data.STS == "200") {
                                    adapter = UserApprovalAdapter(this@UserApproval,data.CONTENT)
                                    binding.recyclerView.adapter = adapter
                                } else {
                                    Constant.error(this@UserApproval, data.MSG)
                                }
                            } else {
                                Constant.error(this@UserApproval, "No data received")
                            }
                        } else {
                            Constant.error(this@UserApproval, "Response unsuccessful")
                            Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<GetAllUserDisabledResponseModel>, t: Throwable) {
                        progress.dismiss()
                        Constant.error(this@UserApproval, "Something went wrong: ${t.message}")
                        Log.e("SelectSocietyFragment", "API call failed", t)
                    }
                })
            } catch (e: Exception) {
                progress.dismiss()
                Constant.error(this@UserApproval, "Exception: ${e.message}")
                e.printStackTrace()
            }
    }
}