package com.taskease.yksfoundation.Adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Activities.SuperAdmin.AddAdminActivity
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.ResponseModel.GetUserBySociety
import com.taskease.yksfoundation.Model.ResponseModel.GetUserBySocietyResponseModel
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.AdminCardBinding
import com.taskease.yksfoundation.databinding.SocietyLayoutBinding
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class UserAdapter(val context: Context, val list : List<GetUserBySociety>) : RecyclerView.Adapter<UserAdapter.onViewHolder>() {

    private var filteredList: List<GetUserBySociety> = list

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): onViewHolder {
        val view = AdminCardBinding.inflate(LayoutInflater.from(context),parent,false)
        return onViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: onViewHolder,
        position: Int
    ) {

        val data = filteredList[position]
        holder.binding.apply {
            Glide.with(context).load(data.profile_pic).placeholder(R.drawable.profile).into(imgProfile)
            txtName.text = data.fullName
            txtEmail.text = data.email
            txtPhone.text = data.phoneNo
            btnCall.setOnClickListener {
                Constant.callPhone(data.phoneNo,context)
            }

            btnChangeRole.setOnClickListener {

                // TODO: Remaiing to implement dialog for the roles
                callChangeRole(data.id)
            }

            btnDelete.setOnClickListener {
                deleteUser(data.id)
            }

        }
    }

    private fun deleteUser(id: Int)
    {
        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().deleteUser(id).enqueue(object :
                Callback<UniversalModel> {
                override fun onResponse(
                    call: retrofit2.Call<UniversalModel>,
                    response: Response<UniversalModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                Constant.success(context, data.MSG)
                            } else {
                                Constant.error(context, data.MSG)
                            }
                        } else {
                            Constant.error(context, "No data received")
                        }
                    } else {
                        Constant.error(context, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<UniversalModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(context, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(context, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }


    private fun callChangeRole(id: Int)
    {
        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().changeRole(id,"ROLE_ADMIN").enqueue(object :
                Callback<UniversalModel> {
                override fun onResponse(
                    call: retrofit2.Call<UniversalModel>,
                    response: Response<UniversalModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                Constant.success(context, data.MSG)
                            } else {
                                Constant.error(context, data.MSG)
                            }
                        } else {
                            Constant.error(context, "No data received")
                        }
                    } else {
                        Constant.error(context, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<UniversalModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(context, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(context, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchText = constraint?.toString()?.lowercase(Locale.getDefault())?.trim()
                filteredList = if (searchText.isNullOrEmpty()) {
                    list
                } else {
                    list.filter {
                        it.fullName.lowercase(Locale.getDefault()).contains(searchText) ||
                                it.address.lowercase(Locale.getDefault()).contains(searchText)
                    }
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<GetUserBySociety> ?: list
                notifyDataSetChanged()
            }
        }
    }

    class onViewHolder(val binding : AdminCardBinding) : RecyclerView.ViewHolder(binding.root)
}