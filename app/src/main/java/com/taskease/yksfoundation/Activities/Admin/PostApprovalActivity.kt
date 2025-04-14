package com.taskease.yksfoundation.Activities.Admin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskease.yksfoundation.Activities.Admin.UserApproval
import com.taskease.yksfoundation.Adapter.PostApprovalAdapter
import com.taskease.yksfoundation.Adapter.UserApprovalAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllUserDisabledResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityPostApprovalBinding
import retrofit2.Callback
import retrofit2.Response

class PostApprovalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPostApprovalBinding
    private lateinit var adapter : PostApprovalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostApprovalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarTitle.text = "Post Approval"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@PostApprovalActivity)

        getAllDisabledPost()

    }

    private fun getAllDisabledPost()
    {
        val progress = CustomProgressDialog(this@PostApprovalActivity)
        progress.show()

        val societyId = SharedPreferenceManager.getInt(SharedPreferenceManager.SOCIETY_ID)

        try {
            RetrofitInstance.getHeaderInstance().getAllDisabledPost(societyId).enqueue(object :
                Callback<GetAllPostResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<GetAllPostResponseModel>,
                    response: Response<GetAllPostResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                adapter = PostApprovalAdapter(this@PostApprovalActivity,data.CONTENT)
                                binding.recyclerView.adapter = adapter
                            } else {
                                Constant.error(this@PostApprovalActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@PostApprovalActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@PostApprovalActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<GetAllPostResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@PostApprovalActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@PostApprovalActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }
}