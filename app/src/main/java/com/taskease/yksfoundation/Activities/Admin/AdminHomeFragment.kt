package com.taskease.yksfoundation.Activities.Admin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Activities.Chat.ChattingActivity
import com.taskease.yksfoundation.Adapter.PostAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPost
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.FragmentAdminHomeBinding
import retrofit2.Callback
import retrofit2.Response

class AdminHomeFragment : Fragment() {

    private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var adapter: PostAdapter
    private var currentPage = 1
    private val pageSize = 20
    private var isLoading = false
    private var totalPages = Int.MAX_VALUE // Update this if your API returns total pages
    private val postList = mutableListOf<GetAllPost>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = SharedPreferenceManager.getString(SharedPreferenceManager.USER_NAME)
        binding.welcomeMessage.text = "Welcome $userName"

        adapter = PostAdapter(requireContext(), postList) {
            resetAndLoadPosts()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItem == totalItemCount - 1 && currentPage < totalPages) {
                    currentPage++
                    getAllPost(currentPage)
                }
            }
        })

        getAllPost(currentPage)

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::adapter.isInitialized) {
                    adapter.getFilter().filter(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chat.setOnClickListener {
            startActivity(Intent(context, ChattingActivity::class.java))
        }

        binding.welcomeMessage.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, ProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                resetAndLoadPosts()
                binding.swipeRefreshLayout.isRefreshing = false
            }, 2000)
        }
    }

    private fun resetAndLoadPosts() {
        currentPage = 1
        totalPages = Int.MAX_VALUE
        postList.clear()
        adapter.notifyDataSetChanged()
        getAllPost(currentPage)
    }

    private fun getAllPost(page: Int) {
        if (isLoading || page > totalPages) return

        isLoading = true
        val progress =
            if (page == 1) CustomProgressDialog(requireContext()).also { it.show() } else null

        try {
            RetrofitInstance.getHeaderInstance().getAllPost(page, pageSize).enqueue(object :
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
                            if (isAdded) {
                                totalPages = data.CONTENT.totalPages
                                val newPosts = data.CONTENT.content
                                val previousSize = postList.size
                                postList.addAll(newPosts)
                                adapter.notifyItemRangeInserted(previousSize, newPosts.size)
                            }
                        } else {
                            Constant.error(requireContext(), data?.MSG ?: "No data received")
                        }
                    } else {
                        Constant.error(requireContext(), "Response unsuccessful")
                        Log.e("AdminHomeFragment", "Error code: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<GetAllPostResponseModel>,
                    t: Throwable
                ) {
                    progress?.dismiss()
                    isLoading = false
                    Constant.error(requireContext(), "Something went wrong: ${t.message}")
                    Log.e("AdminHomeFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress?.dismiss()
            isLoading = false
            Constant.error(requireContext(), "Exception: ${e.message}")
            e.printStackTrace()
        }
    }
}