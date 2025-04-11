package com.taskease.yksfoundation.Activities.SuperAdmin

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.RequestModel.CreateUserBySuperAdminRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.UserRegisterResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityAddUserBinding
import retrofit2.Callback
import retrofit2.Response

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddUserBinding
    private lateinit var storage: FirebaseStorage
    private  var profileUrl: String? = null
    private var cameraUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()


        binding.actionBar.toolbarTitle.text = "Add User"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.layout.profilePic.setOnClickListener {
            showCustomDialog(this@AddUserActivity)
        }

        binding.layout.submit.setOnClickListener {

            val name = binding.layout.fullName.text.toString()
            val email = binding.layout.email.text.toString()
            val phone = binding.layout.phoneNo.text.toString()
            val gender = binding.layout.gender.selectedItem.toString()
            val address = binding.layout.address.text.toString()
            val password = binding.layout.password.text.toString()


            if (valid(name,email,phone,gender,address,password))
            {
                callAddUser(name,email,phone,gender,address,password)
            }
        }

    }


    private fun valid(name: String, email: String, phone: String, gender: String, address: String, password: String): Boolean {
        if (name.isEmpty()) {
            Constant.error(this@AddUserActivity, "Name is required")
            return false
        }
        if (email.isEmpty()) {
            Constant.error(this@AddUserActivity, "Email is required")
            return false
        }
        if (phone.isEmpty()) {
            Constant.error(this@AddUserActivity, "Phone is required")
            return false
        }
        if (gender.isEmpty()) {
            Constant.error(this@AddUserActivity, "Gender is required")
            return false
        }
        if (address.isEmpty()) {
            Constant.error(this@AddUserActivity, "Address is required")
            return false
        }
        if (password.isEmpty()) {
            Constant.error(this@AddUserActivity, "Password is required")
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
        cameraUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        cameraUri?.let { cameraLauncher.launch(it) }
    }


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
        val progressDialog = CustomProgressDialog(this@AddUserActivity)
        progressDialog.show()
        try {
            storage.getReference("Yks/ProfilePic/users")
                .child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        profileUrl = downloadUri.toString()
                        Glide.with(this).load(profileUrl).into(binding.layout.profilePic)
                        progressDialog.dismiss()
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Constant.error(this, "Failed to get download URL")
                    }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Constant.error(this, "Something went wrong during upload")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            progressDialog.dismiss()
            Constant.error(this, "An error occurred")
        }
    }

    private fun callAddUser(name: String, email: String, phone: String, gender: String, address: String, password: String){
        val progress = CustomProgressDialog(this)
        progress.show()

        val societyId = intent.getIntExtra("id", -1)

        val model = CreateUserBySuperAdminRequestModel(address,email,true,name,gender,password,phone,profileUrl.toString())

        try {
            RetrofitInstance.getHeaderInstance().registerNewUser(societyId,model).enqueue(object :
                Callback<UserRegisterResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<UserRegisterResponseModel>,
                    response: Response<UserRegisterResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                Constant.success(this@AddUserActivity, data.MSG)
                                finish()
                            } else {
                                Constant.error(this@AddUserActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@AddUserActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@AddUserActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<UserRegisterResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@AddUserActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@AddUserActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }
}