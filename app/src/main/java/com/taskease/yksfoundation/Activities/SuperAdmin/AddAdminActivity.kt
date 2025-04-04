package com.taskease.yksfoundation.Activities.SuperAdmin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskease.yksfoundation.Activities.SuperAdmin.AddSocietyActivity
import com.taskease.yksfoundation.Adapter.SocietyAdapter
import com.taskease.yksfoundation.Adapter.UserAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.ResponseModel.GetAllSocietyResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetUserBySocietyResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityAddAdminBinding
import retrofit2.Callback
import retrofit2.Response

class AddAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddAdminBinding
    private lateinit var adapter : UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarTitle.text = "Add Admin"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@AddAdminActivity)

        callGetAllUser()

        binding.addAdmin.setOnClickListener {

        }


    }

    private fun callGetAllUser() {
        val progress = CustomProgressDialog(this)
        progress.show()

        val societyId = intent.getIntExtra("id", -1)

        try {
            RetrofitInstance.getHeaderInstance().getAllUser(societyId).enqueue(object :
                Callback<GetUserBySocietyResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<GetUserBySocietyResponseModel>,
                    response: Response<GetUserBySocietyResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                adapter = UserAdapter(this@AddAdminActivity, data.CONTENT)
                                binding.recyclerView.adapter = adapter
                            } else {
                                Constant.error(this@AddAdminActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@AddAdminActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@AddAdminActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<GetUserBySocietyResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@AddAdminActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@AddAdminActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }


}