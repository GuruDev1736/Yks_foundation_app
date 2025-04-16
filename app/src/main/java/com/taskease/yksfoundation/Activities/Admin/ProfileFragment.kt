package com.taskease.yksfoundation.Activities.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Activities.Admin.PostApprovalActivity
import com.taskease.yksfoundation.Activities.CreatePostActivity
import com.taskease.yksfoundation.Activities.HomeActivity
import com.taskease.yksfoundation.Adapter.PostAdapter
import com.taskease.yksfoundation.Adapter.PostApprovalAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetUserByIdResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.FragmentProfileBinding
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        getUserById(userId)

    }

    private fun getUserById(userId: Int) {
        val progress = CustomProgressDialog(requireContext())
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().getUserById(userId).enqueue(object :
                Callback<GetUserByIdResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<GetUserByIdResponseModel>,
                    response: Response<GetUserByIdResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {

                                Glide.with(requireContext()).load(data.CONTENT.profile_pic).into(binding.profilePic)
                                Glide.with(requireContext()).load(data.CONTENT.bannerUrl).into(binding.bannerURL)
                                binding.name.text = data.CONTENT.fullName
                                binding.description.text = data.CONTENT.email

                                binding.createPost.setOnClickListener {
                                    startActivity(Intent(requireContext(), CreatePostActivity::class.java).putExtra("isSuperAdmin",false))
                                }

                                 binding.editProfile.setOnClickListener {

                                 }

                                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                getPostByUserId(data.CONTENT.id)

                            } else {
                                Constant.error(requireContext(), data.MSG)
                            }
                        } else {
                            Constant.error(requireContext(), "No data received")
                        }
                    } else {
                        Constant.error(requireContext(), "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<GetUserByIdResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(requireContext(), "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(requireContext(), "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun getPostByUserId(userId: Int) {
        val progress = CustomProgressDialog(requireContext())
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().getUserPost(userId).enqueue(object :
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

                                adapter = PostAdapter(requireContext(),data.CONTENT)
                                {
                                    getPostByUserId(userId)
                                }
                                binding.recyclerView.adapter = adapter

                            } else {
                                Constant.error(requireContext(), data.MSG)
                            }
                        } else {
                            Constant.error(requireContext(), "No data received")
                        }
                    } else {
                        Constant.error(requireContext(), "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<GetAllPostResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(requireContext(), "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(requireContext(), "Exception: ${e.message}")
            e.printStackTrace()
        }
    }
}