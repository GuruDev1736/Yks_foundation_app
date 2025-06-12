package com.taskease.yksfoundation.Adapter

import android.animation.Animator
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.Filter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.RequestModel.CreateCommentRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.CreateCommentResponseModel
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
import javax.microedition.khronos.opengles.GL

class ProfilePostAdapter(
    val context: Context,
    val list: MutableList<GetAllPost>,
    val onLikeSuccess: () -> Unit
) :
    RecyclerView.Adapter<ProfilePostAdapter.onViewHolder>() {

    private var filteredList: MutableList<GetAllPost> = list

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): onViewHolder {
        val view = PostLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return onViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: onViewHolder,
        position: Int
    ) {
        val data = filteredList[position]
        holder.binding.apply {

            iconDelete.visibility = View.VISIBLE

            iconDelete.setOnClickListener {
                deletePost(data.id)
            }


            pageCount.text = "${data.imageUrls.size}"

            Glide.with(context).load(Constant.base64ToBitmap(data.user.profile_pic.toString()))
                .error(R.drawable.imagefalied)
                .into(imageProfile)
            textUsername.text = data.user.fullName
            textLocation.text = data.content
            textCaption.text = data.title
            imagePost.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            imagePost.adapter = ImageAdapter(context, data.imageUrls)

            val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

            if (data.likedBy.contains(userId)) {
                iconLike.setImageResource(R.drawable.heart_filled)
            } else {
                iconLike.setImageResource(R.drawable.hear_outline)
            }

            if (data.savedBy.contains(userId)) {
                iconSave.setImageResource(R.drawable.save_filled)
            } else {
                iconSave.setImageResource(R.drawable.save_outline)
                iconSave.setOnClickListener {
                    callSaveApi(data.id)
                }
            }

            val timestamp = Constant.getRelativeTime(data.createdDate)
            textTime.text = timestamp

            iconLike.setOnClickListener {
                val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)
                if (data.likedBy.contains(userId)) {
                    callUnlikeAPI(data.id, onLikeSuccess)
                } else {
                    callLikeAPI(data.id)
                }
            }

            textLikes.text = "${data.likeCount} Likes"

            textLikes.setOnClickListener {
                callLikeBottomSheet(data.id)
            }

            iconComment.setOnClickListener {
                callCommentBottomSheet(data.id)
            }

            val gestureDetector = GestureDetector(
                holder.itemView.context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

                        val isCurrentlyLiked = data.likedBy.contains(userId)

                        lottieView.visibility = View.VISIBLE
                        lottieView.playAnimation()
                        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {

                            }

                            override fun onAnimationEnd(animation: Animator) {
                                lottieView.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator) {
                                TODO("Not yet implemented")
                            }

                            override fun onAnimationRepeat(animation: Animator) {
                                TODO("Not yet implemented")
                            }
                        })

                        if (isCurrentlyLiked) {
                            callUnlikeAPI(data.id, onLikeSuccess)
                        } else {
                            callLikeAPI(data.id)
                        }

                        return true
                    }
                })

            wholeLayout.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }

            profileRow.setOnClickListener {
                try {

                    showUserDialog(
                        context,
                        data.user.fullName.toString(),
                        data.user.designation.toString(),
                        data.user.profile_pic.toString(),
                        data.user.gender.toString(),
                        data.user.address.toString()
                    )
                } catch (e: Exception) {
                    Constant.error(context, "Failed to Show Details")
                }
            }

            iconShare.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"

                val shareText = "Check out this amazing post!"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)

                val chooser = Intent.createChooser(shareIntent, "Share via")
                context.startActivity(chooser)
            }
        }
    }

    fun deletePost(id: Int) {
        val progress = CustomProgressDialog(context)
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().deletePost(id).enqueue(object :
                Callback<UniversalModel> {
                override fun onResponse(
                    call: Call<UniversalModel>,
                    response: Response<UniversalModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null && data.STS == "200") {
                            Constant.success(context, data.MSG)
                            notifyDataSetChanged()
                        } else {
                            Constant.error(context, data?.MSG ?: "No message")
                        }
                    } else {
                        Constant.error(context, "Response unsuccessful")
                    }
                }

                override fun onFailure(call: Call<UniversalModel>, t: Throwable) {
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


    fun showUserDialog(
        context: Context,
        name: String,
        designation: String,
        imageResId: String?,
        gender: String,
        location: String
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_user_profile, null)

        val imageView = dialogView.findViewById<ImageView?>(R.id.imageViewProfile)
        val nameText = dialogView.findViewById<TextView?>(R.id.textViewName)
        val designationText = dialogView.findViewById<TextView?>(R.id.textViewDesignation)
        val genderUser = dialogView.findViewById<TextView?>(R.id.gender)
        val locationUser = dialogView.findViewById<TextView?>(R.id.location)

        // Set image with null-safe Base64 decode
        val bitmap = if (!imageResId.isNullOrBlank()) Constant.base64ToBitmap(imageResId) else null
        if (bitmap != null && imageView != null) {
            Glide.with(context)
                .load(bitmap)
                .error(R.drawable.imagefalied)
                .into(imageView)
        } else {
            imageView?.setImageResource(R.drawable.imagefalied)
        }

        // Set other fields only if views are not null
        nameText?.text = name
        designationText?.text = designation
        genderUser?.text = gender
        locationUser?.text = location

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .show()
    }


    private fun callLikeBottomSheet(postId: Int) {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.like_bottom_sheet_layout, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerComments)
        val title = view.findViewById<TextView>(R.id.title)
        recyclerView.layoutManager = LinearLayoutManager(context)
        title.text = "Likes"

        bottomSheetDialog.setContentView(view)

        val bottomSheet =
            bottomSheetDialog.delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = 600
            behavior.isFitToContents = true
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
                            val adapter = LikeAdapter(context, data.CONTENT)
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
        val view = LayoutInflater.from(context).inflate(R.layout.comment_bottom_sheet_layout, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerComments)
        val title = view.findViewById<TextView>(R.id.title)
        val send = view.findViewById<ImageButton>(R.id.send)
        val comment = view.findViewById<EditText>(R.id.comment)

        send.setOnClickListener {
            val commentText = comment.text.toString()
            if (commentText.isEmpty()) {
                Constant.error(context, "Please enter a comment")
            } else if (commentText.length > 100) {
                Constant.error(context, "Comment should be less than 100 characters")
            } else {
                sendComment(postId, commentText, bottomSheetDialog, recyclerView, comment)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        title.text = "Comments"

        bottomSheetDialog.setContentView(view)

        val bottomSheet =
            bottomSheetDialog.delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = 600
            behavior.isFitToContents = true
            behavior.isDraggable = true
        }

        bottomSheetDialog.show()
        callGetCommentPost(postId, recyclerView)
    }


    private fun callGetCommentPost(postId: Int, recyclerView: RecyclerView) {
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
                            val adapter = CommentAdapter(context, data.CONTENT)
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

    private fun sendComment(
        postId: Int,
        commentText: String,
        bottomDialogSheet: BottomSheetDialog,
        recyclerView: RecyclerView,
        comment: EditText
    ) {
        val progress = CustomProgressDialog(context)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        val model = CreateCommentRequestModel(commentText)

        try {
            RetrofitInstance.getHeaderInstance().createComment(userId, postId, model)
                .enqueue(object :
                    Callback<CreateCommentResponseModel> {
                    override fun onResponse(
                        call: Call<CreateCommentResponseModel>,
                        response: Response<CreateCommentResponseModel>
                    ) {
                        progress.dismiss()
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                if (data.STS == "200") {
                                    callGetCommentPost(postId, recyclerView)
                                    comment.setText("")
                                } else {
                                    Constant.error(context, data.MSG)
                                }
                            } else {
                                Constant.error(context, "No data received")
                            }
                        } else {
                            Constant.error(context, "Response unsuccessful")
                            Log.e(
                                "SelectSocietyFragment",
                                "Error response code: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<CreateCommentResponseModel>,
                        t: Throwable
                    ) {
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

    private fun callLikeAPI(id: Int) {
        val progress = CustomProgressDialog(context)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        try {
            RetrofitInstance.getHeaderInstance().likePost(userId, id).enqueue(object :
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

    private fun callUnlikeAPI(postId: Int, onUnlikeSuccess: () -> Unit) {
        val progress = CustomProgressDialog(context)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        try {
            RetrofitInstance.getHeaderInstance().unlikePost(userId, postId)
                .enqueue(object : Callback<UniversalModel> {
                    override fun onResponse(
                        call: Call<UniversalModel>,
                        response: Response<UniversalModel>
                    ) {
                        progress.dismiss()
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null && data.STS == "200") {
                                Constant.success(context, data.MSG)
                                onUnlikeSuccess.invoke()
                            } else {
                                Constant.error(context, data?.MSG ?: "Error")
                            }
                        } else {
                            Constant.error(context, "Response unsuccessful")
                        }
                    }

                    override fun onFailure(call: Call<UniversalModel>, t: Throwable) {
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

    private fun callSaveApi(id: Int) {
        val progress = CustomProgressDialog(context)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        try {
            RetrofitInstance.getHeaderInstance().savePost(userId, id).enqueue(object :
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


    override fun getItemCount(): Int {
        return filteredList.size
    }

    class onViewHolder(val binding: PostLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}