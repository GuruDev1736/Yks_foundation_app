package com.taskease.yksfoundation.Activities.Auth.RegisterFragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.taskease.yksfoundation.Activities.Auth.LoginActivity
import com.taskease.yksfoundation.Activities.Auth.RegisterActivity
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.RequestModel.UserRegisterRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.UserRegisterResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.ViewModel.RegisterViewModel
import com.taskease.yksfoundation.databinding.FragmentCompleteProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CompleteProfileFragment : Fragment() {

    private lateinit var binding: FragmentCompleteProfileBinding
    private lateinit var storage: FirebaseStorage
    private  var profileUrl: String? = null
    private var cameraUri: Uri? = null
    private lateinit var viewModel: RegisterViewModel

    private val ImagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { UploadFile(it) }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraUri != null) {
                UploadFile(cameraUri!!)
            }
        }

    private fun UploadFile(uri: Uri) {
        val progressDialog = CustomProgressDialog(requireContext())
        progressDialog.show()
        try {
            storage.getReference("Yks/ProfilePic")
                .child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        profileUrl = downloadUri.toString()
                        Glide.with(requireContext()).load(profileUrl).into(binding.profilePic)
                        progressDialog.dismiss()
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Constant.error(requireContext(), "Failed to get download URL")
                    }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Constant.error(requireContext(), "Something went wrong during upload")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            progressDialog.dismiss()
            Constant.error(requireContext(), "An error occurred")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompleteProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = FirebaseStorage.getInstance()

        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        binding.previous.setOnClickListener {
            val viewPager = (context as RegisterActivity).getViewPager()
            viewPager.currentItem -= 1
        }

        binding.profilePic.setOnClickListener {
            showCustomDialog(requireContext())
        }

        binding.register.setOnClickListener {
            if (valid())
            {
                viewModel.profilePic(profileUrl.toString())
                callRegisterApi()
            }
        }
    }

    private fun callRegisterApi() {
        val progress = CustomProgressDialog(requireContext())
        progress.show()

        try {
            val data = viewModel.signupData.value
            if (data!=null)
            {
                val model = UserRegisterRequestModel(
                    data.address.toString(),
                    data.anniversaryDate.toString(),
                    data.dateOfBirth.toString(), data.designation.toString(), data.email.toString(),
                    data.facebookLink.toString(), data.fullName.toString(), data.gender.toString(),
                    data.instagramLink.toString(),
                    data.linkedinLink.toString(),
                    data.member,
                    data.password.toString(),
                    data.phoneNo.toString(),
                    data.profilePic.toString(),
                    data.snapchatLink.toString(), data.twitterLink.toString(),data.voter, data.whatsappNo.toString()
                )

                RetrofitInstance.getInstance().registerUser(data.societyId,model).enqueue(object :
                    Callback<UserRegisterResponseModel> {
                    override fun onResponse(
                        call: Call<UserRegisterResponseModel>,
                        response: Response<UserRegisterResponseModel>
                    ) {
                        progress.dismiss()

                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                if (data.STS == "200") {
                                    Constant.success(requireContext(), data.MSG)
                                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                                    requireActivity().finish()
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

                    override fun onFailure(call: Call<UserRegisterResponseModel>, t: Throwable) {
                        progress.dismiss()
                        Constant.error(requireContext(), "Something went wrong: ${t.message}")
                        Log.e("SelectSocietyFragment", "API call failed", t)
                    }
                })
            }
        }catch (e : Exception)
        {
            e.printStackTrace()
            progress.dismiss()
        }
    }

    private fun valid() : Boolean{
        if (profileUrl == null)
        {
            Constant.error(requireContext(), "Please select a profile picture")
            return false
        }
        return true
    }

    fun showCustomDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.capture_options, null)
        dialog.setContentView(view)

        val camera = view.findViewById<View>(R.id.camera)
        val gallery = view.findViewById<View>(R.id.gallery)

        camera.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }

        gallery.setOnClickListener {
            ImagePickerLauncher.launch("image/*")
            dialog.dismiss()
        }

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.8).toInt(), // 90% of screen width
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun openCamera() {
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        cameraUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        cameraUri?.let { cameraLauncher.launch(it) }
    }
}