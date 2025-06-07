package com.taskease.yksfoundation.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Activities.Admin.UserApproval
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.CommentPost
import com.taskease.yksfoundation.Model.ResponseModel.GetAllUserDisabledResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.User
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.CommentLayoutBinding
import com.taskease.yksfoundation.databinding.UserApprovalLayoutBinding
import org.w3c.dom.Comment
import retrofit2.Callback
import retrofit2.Response


class UserApprovalAdapter(val context: Context, val list: List<User> , val listener: () -> Unit) :
    RecyclerView.Adapter<UserApprovalAdapter.ViewHolder>() {

    class ViewHolder(val binding : UserApprovalLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = UserApprovalLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.binding.apply {
            val profilePicture = Constant.base64ToBitmap(data.profile_pic.toString())
            Glide.with(context).load(profilePicture).placeholder(R.drawable.imagefalied).into(profilePic)
            name.text = data.fullName
            email.text = data.email
            phone.text = data.phoneNo
            approve.setOnClickListener {
                callApproveUser(data.id)
            }

            reject.setOnClickListener {
                callRejectUser(data.id)
            }
        }
    }


    private fun callApproveUser(id: Int) {

        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().enableUser(id).enqueue(object :
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
                                listener.invoke()
                                Constant.success(context,data.MSG)
                            } else {
                                Constant.error(context, data.MSG)
                            }
                        } else {
                            Constant.error(context, "No data received")
                        }
                    } else {
                        Constant.error(context, "Response unsuccessful")
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

    private fun callRejectUser(id: Int) {

        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().rejectUser(id).enqueue(object :
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
                                listener.invoke()
                                Constant.success(context,data.MSG)
                            } else {
                                Constant.error(context, data.MSG)
                            }
                        } else {
                            Constant.error(context, "No data received")
                        }
                    } else {
                        Constant.error(context, "Response unsuccessful")
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

    override fun getItemCount(): Int = list.size
}
