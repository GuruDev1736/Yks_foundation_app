package com.taskease.yksfoundation.Activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskease.yksfoundation.Adapter.PostAdapter
import com.taskease.yksfoundation.Adapter.SavedPostAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.SavedPostResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivitySavePostBinding
import retrofit2.Callback
import retrofit2.Response

class SavePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavePostBinding
    private lateinit var adapter: SavedPostAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarTitle.text = "Saved Post"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@SavePostActivity)

        getAllPost()


    }

    private fun getAllPost() {

        val progress = CustomProgressDialog(this@SavePostActivity)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        try {
            RetrofitInstance.getHeaderInstance().getAllSavedPost(userId).enqueue(object :
                Callback<SavedPostResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<SavedPostResponseModel>,
                    response: Response<SavedPostResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                adapter = SavedPostAdapter(this@SavePostActivity, data.CONTENT) {
                                    getAllPost()
                                }
                                binding.recyclerView.adapter = adapter
                            } else {
                                Constant.error(this@SavePostActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@SavePostActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@SavePostActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<SavedPostResponseModel>,
                    t: Throwable
                ) {
                    progress.dismiss()
                    Constant.error(this@SavePostActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@SavePostActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }
}