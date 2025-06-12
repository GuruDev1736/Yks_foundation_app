package com.taskease.yksfoundation.Activities.Admin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Activities.Admin.UserApproval
import com.taskease.yksfoundation.Adapter.PostAdapter
import com.taskease.yksfoundation.Adapter.PostApprovalAdapter
import com.taskease.yksfoundation.Adapter.UserApprovalAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPost
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllUserDisabledResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityPostApprovalBinding
import retrofit2.Callback
import retrofit2.Response

class PostApprovalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostApprovalBinding
    private lateinit var adapter: PostApprovalAdapter
    private var currentPage = 1
    private val pageSize = 20
    private var isLoading = false
    private var totalPages = Int.MAX_VALUE // Update this if your API returns total pages
    private val postList = mutableListOf<GetAllPost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostApprovalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarTitle.text = "Post Approval"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        adapter = PostApprovalAdapter(this@PostApprovalActivity, postList) {
            resetAndLoadPosts()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@PostApprovalActivity)
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItem == totalItemCount - 1 && currentPage < totalPages) {
                    currentPage++
                    getAllDisabledPost(currentPage)
                }
            }
        })

        getAllDisabledPost(currentPage)

    }

    private fun resetAndLoadPosts() {
        currentPage = 1
        totalPages = Int.MAX_VALUE
        postList.clear()
        adapter.notifyDataSetChanged()
        getAllDisabledPost(currentPage)
    }


    private fun getAllDisabledPost(page: Int) {
        if (isLoading || page > totalPages) return

        isLoading = true
        val progress =
            if (page == 1) CustomProgressDialog(this@PostApprovalActivity).also { it.show() } else null

        val societyId = SharedPreferenceManager.getInt(SharedPreferenceManager.SOCIETY_ID)

        try {
            RetrofitInstance.getHeaderInstance().getAllDisabledPost(societyId,page, pageSize).enqueue(object :
                Callback<GetAllPostResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<GetAllPostResponseModel>,
                    response: Response<GetAllPostResponseModel>
                ) {
                    progress?.dismiss()
                    isLoading = false

                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null && data.STS == "200") {

                            totalPages = data.CONTENT.totalPages
                            val newPosts = data.CONTENT.content
                            val previousSize = postList.size
                            postList.addAll(newPosts)
                            adapter.notifyItemRangeInserted(previousSize, newPosts.size)

                        } else {
                            Constant.error(
                                this@PostApprovalActivity,
                                data?.MSG ?: "No data received"
                            )
                        }
                    } else {
                        Constant.error(this@PostApprovalActivity, "Response unsuccessful")
                        Log.e("AdminHomeFragment", "Error code: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<GetAllPostResponseModel>,
                    t: Throwable
                ) {
                    progress?.dismiss()
                    isLoading = false
                    Constant.error(this@PostApprovalActivity, "Something went wrong: ${t.message}")
                    Log.e("AdminHomeFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress?.dismiss()
            isLoading = false
            Constant.error(this@PostApprovalActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }
}