package com.taskease.yksfoundation.Adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Activities.Admin.UserApproval
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.CommentPost
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPost
import com.taskease.yksfoundation.Model.ResponseModel.GetAllUserDisabledResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.User
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.CommentLayoutBinding
import com.taskease.yksfoundation.databinding.PostApprovalLayoutBinding
import com.taskease.yksfoundation.databinding.PostLayoutBinding
import com.taskease.yksfoundation.databinding.UserApprovalLayoutBinding
import org.w3c.dom.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PostApprovalAdapter(val context: Context, val list: List<GetAllPost> , val listener: () -> Unit) :
    RecyclerView.Adapter<PostApprovalAdapter.ViewHolder>() {

    class ViewHolder(val binding : PostApprovalLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = PostApprovalLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.binding.apply {
            val profilePic = Constant.base64ToBitmap(data.user.profile_pic)
            Glide.with(context).load(profilePic).placeholder(R.drawable.imagefalied).into(imageProfile)
            textUsername.text = data.user.fullName
            textLocation.text = data.content
            textCaption.text = data.title
            imagePost.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            imagePost.adapter = ImageAdapter(context, data.imageUrls)

            approve.setOnClickListener {
                callApprovePost(data.id)
            }
            reject.setOnClickListener {
                callRejectPost(data.id)
            }
        }
    }


    private fun callApprovePost(id: Int) {

        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().enablePost(id).enqueue(object :
                Callback<UniversalModel> {
                override fun onResponse(
                    call: Call<UniversalModel>,
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

                override fun onFailure(call: Call<UniversalModel>, t: Throwable) {
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

    private fun callRejectPost(id: Int) {

        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().rejectPost(id).enqueue(object :
                Callback<UniversalModel> {
                override fun onResponse(
                    call: Call<UniversalModel>,
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

                override fun onFailure(call: Call<UniversalModel>, t: Throwable) {
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
