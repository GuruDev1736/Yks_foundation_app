package com.taskease.yksfoundation.Adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPost
import com.taskease.yksfoundation.Model.ResponseModel.GetCommentByPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetUserByPostResponseModel
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ItemImageViewpagerBinding
import com.taskease.yksfoundation.databinding.PostLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class PostAdapter(val context: Context , val list : List<GetAllPost> , val onLikeSuccess: () -> Unit) : RecyclerView.Adapter<PostAdapter.onViewHolder>() {

    private var filteredList: List<GetAllPost> = list

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): onViewHolder {
        val view = PostLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return onViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: onViewHolder,
        position: Int
    ) {
        val data = filteredList[position]
        holder.binding.apply {
            Glide.with(context).load(data.user.profile_pic).into(imageProfile)
            textUsername.text = data.user.fullName
            textLocation.text = data.content
            textCaption.text = data.title
            imagePost.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            imagePost.adapter = ImageAdapter(context,data.imageUrls)

            val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

            if (data.likedBy.contains(userId)) {
                iconLike.setImageResource(R.drawable.ic_heart)
                iconLike.isEnabled = false
            } else {
                iconLike.setImageResource(R.drawable.ic_heart_outline)
                iconLike.isEnabled = true
            }

            val timestamp = getRelativeTime(data.createdDate)
            textTime.text = timestamp

            iconLike.setOnClickListener {
                callLikeAPI(data.id)
            }

            textLikes.text = "Liked By ${data.likeCount}"

            textLikes.setOnClickListener {
                callLikeBottomSheet(data.id)
            }

            iconComment.setOnClickListener {
                callCommentBottomSheet(data.id)
            }
        }
    }

    private fun callLikeBottomSheet(postId: Int) {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.like_bottom_sheet_layout, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerComments)
        val title = view.findViewById<TextView>(R.id.title)
        recyclerView.layoutManager = LinearLayoutManager(context)
        title.text = "Likes"

        bottomSheetDialog.setContentView(view)

        val bottomSheet = bottomSheetDialog.delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = 600
            behavior.isFitToContents = false
            behavior.isDraggable = true
        }

        bottomSheetDialog.show()

        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().getLikedUsers(postId).enqueue(object :
                Callback<GetUserByPostResponseModel> {
                override fun onResponse(
                    call: Call<GetUserByPostResponseModel>,
                    response: Response<GetUserByPostResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null && data.STS == "200") {
                            val adapter = LikeAdapter(context,data.CONTENT)
                            recyclerView.adapter = adapter
                        } else {
                            Constant.error(context, data?.MSG ?: "No message")
                        }
                    } else {
                        Constant.error(context, "Response unsuccessful")
                    }
                }

                override fun onFailure(call: Call<GetUserByPostResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(context, "Something went wrong: ${t.message}")
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(context, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun callCommentBottomSheet(postId: Int) {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.like_bottom_sheet_layout, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerComments)
        val title = view.findViewById<TextView>(R.id.title)
        recyclerView.layoutManager = LinearLayoutManager(context)
        title.text = "Comments"

        bottomSheetDialog.setContentView(view)

        val bottomSheet = bottomSheetDialog.delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = 600
            behavior.isFitToContents = false
            behavior.isDraggable = true
        }

        bottomSheetDialog.show()

        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().getComments(postId).enqueue(object :
                Callback<GetCommentByPostResponseModel> {
                override fun onResponse(
                    call: Call<GetCommentByPostResponseModel>,
                    response: Response<GetCommentByPostResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null && data.STS == "200") {
                            val adapter = CommentAdapter(context,data.CONTENT)
                            recyclerView.adapter = adapter
                        } else {
                            Constant.error(context, data?.MSG ?: "No message")
                        }
                    } else {
                        Constant.error(context, "Response unsuccessful")
                    }
                }

                override fun onFailure(call: Call<GetCommentByPostResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(context, "Something went wrong: ${t.message}")
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(context, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTime(timestamp: List<Int>): String {
        // Convert timestamp to ZonedDateTime in UTC or your server's time zone
        val postTime = ZonedDateTime.of(
            timestamp[0],
            timestamp[1],
            timestamp[2],
            timestamp[3],
            timestamp[4],
            timestamp[5],
            timestamp.getOrElse(6) { 0 },
            ZoneOffset.UTC // adjust if your backend uses a different zone
        )

        // Get current time in the same time zone
        val now = ZonedDateTime.now(ZoneOffset.UTC)

        val duration = Duration.between(postTime, now)

        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            duration.toDays() == 1L -> "Yesterday"
            duration.toDays() < 7 -> "${duration.toDays()} days ago"
            else -> postTime.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        }
    }

    private fun callLikeAPI(id : Int)
    {
        val progress = CustomProgressDialog(context)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        try {
            RetrofitInstance.getHeaderInstance().likePost(userId,id).enqueue(object :
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
                                onLikeSuccess.invoke()
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
                val query = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""
                val result = if (query.isEmpty()) {
                    list
                } else {
                    list.filter {
                        it.title.lowercase(Locale.getDefault()).contains(query) ||
                                it.content.lowercase(Locale.getDefault()).contains(query) ||
                                it.user.fullName.lowercase(Locale.getDefault()).contains(query)
                    }
                }
                return FilterResults().apply { values = result }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<GetAllPost>
                notifyDataSetChanged()
            }
        }
    }


    class onViewHolder(val binding : PostLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}



class ImageAdapter(val context: Context,val list : List<String>) : RecyclerView.Adapter<ImageAdapter.onViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): onViewHolder {
        val view = ItemImageViewpagerBinding.inflate(LayoutInflater.from(context),parent,false)
        return onViewHolder(view,view.root)
    }

    override fun onBindViewHolder(
        holder: onViewHolder,
        position: Int
    ) {
        val data = list[position]
        holder.binding.apply {
            Glide.with(context).load(data).into(imgViewPager)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class onViewHolder(val binding : ItemImageViewpagerBinding, itemView: View) : RecyclerView.ViewHolder(binding.root)

}