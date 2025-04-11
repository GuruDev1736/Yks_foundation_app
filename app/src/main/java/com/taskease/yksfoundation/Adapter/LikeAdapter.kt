package com.taskease.yksfoundation.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Model.ResponseModel.User
import com.taskease.yksfoundation.databinding.CommentLayoutBinding

class LikeAdapter(val context: Context, val list: List<User>) :
    RecyclerView.Adapter<LikeAdapter.ViewHolder>() {

    class ViewHolder(val binding : CommentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CommentLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.binding.apply {
            Glide.with(context).load(data.profile_pic).into(imgProfile)
            tvUsername.text = data.fullName
            tvComment.text = data.gender
        }
    }

    override fun getItemCount(): Int = list.size
}
