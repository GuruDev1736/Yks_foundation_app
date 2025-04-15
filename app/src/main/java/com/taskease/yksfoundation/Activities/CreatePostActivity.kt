package com.taskease.yksfoundation.Activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.storage.FirebaseStorage
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.RequestModel.CreatePostRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.CreatePostResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityCreatePostBinding
import retrofit2.Callback
import retrofit2.Response

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCreatePostBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var imageAdapter: ImagePagerAdapter

    private val imageUris = mutableListOf<Uri>()
    private val uploadedUrls = mutableListOf<String>()


    private val launcher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        imageUris.clear()
        if (uris != null) {
            binding.postImages.visibility = View.GONE
            binding.ViewpagerLayout.visibility = View.VISIBLE
            imageUris.addAll(uris)
            imageAdapter.notifyDataSetChanged()
            setupDots()
        }
        else
        {
            binding.postImages.visibility = View.VISIBLE
            binding.ViewpagerLayout.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.actionBar.toolbarTitle.text = "Create Post"

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabDots)

        imageAdapter = ImagePagerAdapter(imageUris)
        viewPager.adapter = imageAdapter

        binding.postImages.setOnClickListener {
            launcher.launch("image/*")
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        binding.submit.setOnClickListener {

            val caption  = binding.caption.text.toString()
            val location = binding.location.text.toString()

            if (valid(caption,location))
            {
               uploadImagesToFirebase(caption,location)
            }
        }
    }

    private fun setupDots() {
        tabLayout.removeAllTabs()
        for (i in imageUris.indices) {
            tabLayout.addTab(tabLayout.newTab())
        }
    }

    private fun uploadImagesToFirebase(caption: String, location: String) {

        val progress = CustomProgressDialog(this)
        progress.show()

        val isSuperAdmin = intent.getBooleanExtra("isSuperAdmin",false)
        uploadedUrls.clear()
        if (imageUris.isEmpty()) return

        val storageRef = FirebaseStorage.getInstance().reference.child("images")

        imageUris.forEachIndexed { index, uri ->
            val fileRef = storageRef.child("${System.currentTimeMillis()}_$index.jpg")
            fileRef.putFile(uri).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    progress.dismiss()
                    uploadedUrls.add(downloadUri.toString())
                    if (uploadedUrls.size == imageUris.size) {
                        val finalUrls = uploadedUrls.joinToString(",")
                        Log.d("FinalURLs", finalUrls.toString())
                        if (isSuperAdmin)
                        {
                            CreatePostBySuperAdmin(caption,location,finalUrls)
                        }
                        else
                        {
                            CreatePostByUser(caption,location,finalUrls)
                        }
                    }
                }
            }.addOnFailureListener {
                progress.dismiss()
                Toast.makeText(this, "Upload Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun CreatePostBySuperAdmin(caption: String, location: String, finalUrls: String) {

            val progress = CustomProgressDialog(this)
            progress.show()

            val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

            try {

                val model = CreatePostRequestModel(location,finalUrls,caption)
                RetrofitInstance.getHeaderInstance().createSuperAdminPost(userId,model).enqueue(object :
                    Callback<CreatePostResponseModel> {
                    override fun onResponse(
                        call: retrofit2.Call<CreatePostResponseModel>,
                        response: Response<CreatePostResponseModel>
                    ) {
                        progress.dismiss()
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                if (data.STS == "200") {
                                    Constant.success(this@CreatePostActivity, data.MSG)
                                    finish()
                                } else {
                                    Constant.error(this@CreatePostActivity, data.MSG)
                                }
                            } else {
                                Constant.error(this@CreatePostActivity, "No data received")
                            }
                        } else {
                            Constant.error(this@CreatePostActivity, "Response unsuccessful")
                            Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<CreatePostResponseModel>, t: Throwable) {
                        progress.dismiss()
                        Constant.error(this@CreatePostActivity, "Something went wrong: ${t.message}")
                        Log.e("SelectSocietyFragment", "API call failed", t)
                    }
                })
            } catch (e: Exception) {
                progress.dismiss()
                Constant.error(this@CreatePostActivity, "Exception: ${e.message}")
                e.printStackTrace()
            }
    }

    private fun CreatePostByUser(caption: String, location: String, finalUrls: String) {

        val progress = CustomProgressDialog(this)
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)
        val societyId = SharedPreferenceManager.getInt(SharedPreferenceManager.SOCIETY_ID)

        try {

            val model = CreatePostRequestModel(location,finalUrls,caption)
            RetrofitInstance.getHeaderInstance().createPost(userId,societyId,model).enqueue(object :
                Callback<CreatePostResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<CreatePostResponseModel>,
                    response: Response<CreatePostResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                Constant.success(this@CreatePostActivity, data.MSG)
                                finish()
                            } else {
                                Constant.error(this@CreatePostActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@CreatePostActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@CreatePostActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<CreatePostResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@CreatePostActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@CreatePostActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }


    fun valid (caption: String, location: String) : Boolean
    {
        if (caption.isEmpty())
        {
            Toast.makeText(this,"Please Enter Caption",Toast.LENGTH_SHORT).show()
            return false
        }
        if (location.isEmpty())
        {
            Toast.makeText(this,"Please Enter Location",Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageUris.isEmpty())
        {
            Toast.makeText(this,"Please Select Images",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}


class ImagePagerAdapter(private val imageUris: List<Uri>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgViewPager)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_viewpager, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int = imageUris.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(imageUris[position]).into(holder.image)
    }
}
