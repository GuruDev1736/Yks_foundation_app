package com.taskease.yksfoundation.Adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Model.ResponseModel.CommentPost
import com.taskease.yksfoundation.Model.ResponseModel.User
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.CommentLayoutBinding
import org.w3c.dom.Comment

class CommentAdapter(val context: Context, val list: List<CommentPost>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(val binding : CommentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CommentLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.binding.apply {
            Glide.with(context).load(Constant.base64ToBitmap(data.user.profile_pic.toString())).error(R.drawable.imagefalied).into(imgProfile)
            tvUsername.text = data.user.fullName
            tvComment.text = data.text
            tvTime.text = Constant.getRelativeTimeFromMillis(data.createdDate)
        }
    }

    override fun getItemCount(): Int = list.size
}
