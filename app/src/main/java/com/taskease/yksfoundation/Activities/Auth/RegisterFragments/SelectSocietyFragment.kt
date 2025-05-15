package com.taskease.yksfoundation.Activities.Auth.RegisterFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskease.yksfoundation.Adapter.SocietyAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.ResponseModel.GetAllSocietyResponseModel
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.FragmentSelectSocietyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectSocietyFragment : Fragment() {

    private var _binding: FragmentSelectSocietyBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SocietyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectSocietyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.getFilter().filter(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })


        getAllSociety()
    }

    private fun getAllSociety() {
        val progress = CustomProgressDialog(requireContext())
        progress.show()

        try {
            RetrofitInstance.getInstance().getAllSociety().enqueue(object :
                Callback<GetAllSocietyResponseModel> {
                override fun onResponse(
                    call: Call<GetAllSocietyResponseModel>,
                    response: Response<GetAllSocietyResponseModel>
                ) {
                    progress.dismiss()

                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                adapter = SocietyAdapter(requireContext(), data.CONTENT,"registration")
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

                override fun onFailure(call: Call<GetAllSocietyResponseModel>, t: Throwable) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}