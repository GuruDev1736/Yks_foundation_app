package com.taskease.yksfoundation.Activities.Admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskease.yksfoundation.Activities.Chat.ChattingActivity
import com.taskease.yksfoundation.Adapter.PostAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.FragmentAdminHomeBinding
import retrofit2.Callback
import retrofit2.Response


class AdminHomeFragment : Fragment() {

    private lateinit var binding : FragmentAdminHomeBinding
    private lateinit var adapter : PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = SharedPreferenceManager.getString(SharedPreferenceManager.USER_NAME)

        binding.welcomeMessage.text = "Welcome $userName"

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        getAllPost()

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::adapter.isInitialized)
                {
                    adapter.getFilter().filter(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chat.setOnClickListener {
            startActivity(Intent(context,ChattingActivity::class.java))
        }

    }

    private fun getAllPost() {

        val progress = CustomProgressDialog(requireContext())
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().getAllPost().enqueue(object :
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
                                adapter = PostAdapter(requireContext(),data.CONTENT){
                                    getAllPost()
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