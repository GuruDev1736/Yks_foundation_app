package com.taskease.yksfoundation.Activities.Admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Activities.Admin.PostApprovalActivity
import com.taskease.yksfoundation.Activities.Auth.LoginActivity
import com.taskease.yksfoundation.Activities.CreatePostActivity
import com.taskease.yksfoundation.Activities.HomeActivity
import com.taskease.yksfoundation.Activities.SavePostActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.AddAdminActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.SuperAdminHomeActivity
import com.taskease.yksfoundation.Adapter.PostAdapter
import com.taskease.yksfoundation.Adapter.PostApprovalAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetUserByIdResponseModel
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.FragmentProfileBinding
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var adapter: PostAdapter
    private var profileUrl: String? = null
    private var cameraUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)

        getUserById(userId)

        binding.logout.setOnClickListener {
           showDialog()
        }

        binding.editProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.save.setOnClickListener {
            startActivity(Intent(context, SavePostActivity::class.java))
        }

        binding.profilePic.setOnClickListener {
            Constant.showImageChooserDialog(requireContext(), onCameraClick = {
                openCamera()
            }, onGalleryClick = {
                ImagePickerLauncher.launch("image/*")
            })
        }
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                SharedPreferenceManager.clearAll()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun openCamera() {
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        cameraUri = context?.contentResolver?.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        cameraUri?.let { cameraLauncher.launch(it) }
    }


    private val ImagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                profileUrl = Constant.uriToBase64(requireContext(), it)
                saveProfilePic(profileUrl)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraUri != null) {
                profileUrl = Constant.uriToBase64(requireContext(), cameraUri!!)
                saveProfilePic(profileUrl)
            }
        }


    private fun saveProfilePic(profileUrl: String?) {

        val progress = CustomProgressDialog(requireContext())
        progress.show()

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)
        val model = mapOf(
            "profile_pic" to profileUrl
        )

        try {
            RetrofitInstance.getHeaderInstance().updateProfilePic(userId,
                model as Map<String, String>
            ).enqueue(object :
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
                                getUserById(userId)
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

                override fun onFailure(
                    call: retrofit2.Call<UniversalModel>,
                    t: Throwable
                ) {
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

    private fun getUserById(userId: Int) {
        val progress = CustomProgressDialog(requireContext())
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().getUserById(userId).enqueue(object :
                Callback<GetUserByIdResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<GetUserByIdResponseModel>,
                    response: Response<GetUserByIdResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                val profilePic =
                                    Constant.base64ToBitmap(data.CONTENT.profile_pic.toString())
                                Glide.with(requireContext()).load(profilePic)
                                    .error(R.drawable.imagefalied).into(binding.profilePic)
                                binding.name.text = data.CONTENT.fullName
                                binding.description.text = data.CONTENT.email

                                binding.recyclerView.layoutManager =
                                    LinearLayoutManager(requireContext())
                                getPostByUserId(data.CONTENT.id)

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

                override fun onFailure(
                    call: retrofit2.Call<GetUserByIdResponseModel>,
                    t: Throwable
                ) {
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

    private fun getPostByUserId(userId: Int) {
        val progress = CustomProgressDialog(requireContext())
        progress.show()

        try {
            RetrofitInstance.getHeaderInstance().getUserPost(userId).enqueue(object :
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

                                adapter = PostAdapter(requireContext(), data.CONTENT)
                                {
                                    getPostByUserId(userId)
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

                override fun onFailure(
                    call: retrofit2.Call<GetAllPostResponseModel>,
                    t: Throwable
                ) {
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