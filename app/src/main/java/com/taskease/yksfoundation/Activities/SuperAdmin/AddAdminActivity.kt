package com.taskease.yksfoundation.Activities.SuperAdmin

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import com.taskease.yksfoundation.Adapter.UserAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.RequestModel.CreateUserBySuperAdminRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.GetUserBySocietyResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.UserRegisterResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityAddAdminBinding
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Callback
import retrofit2.Response

class AddAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddAdminBinding
    private lateinit var adapter : UserAdapter
    private lateinit var storage: FirebaseStorage
    private  var profileUrl: String? = null
    private var cameraUri: Uri? = null
    private lateinit var profilePic : CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()

        binding.actionBar.toolbarTitle.text = "Add Admin"
        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@AddAdminActivity)

        callGetAllUser()

        binding.addAdmin.setOnClickListener {
            showDialog()
        }


    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_admin_layout, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        profilePic = dialogView.findViewById<CircleImageView>(R.id.profilePic)
        val name = dialogView.findViewById<TextInputEditText>(R.id.fullName)
        val email = dialogView.findViewById<TextInputEditText>(R.id.email)
        val phone = dialogView.findViewById<TextInputEditText>(R.id.phoneNo)
        val gender = dialogView.findViewById<Spinner>(R.id.gender)
        val address = dialogView.findViewById<TextInputEditText>(R.id.address)
        val password = dialogView.findViewById<TextInputEditText>(R.id.password)
        val submit = dialogView.findViewById<MaterialButton>(R.id.submit)
        val close = dialogView.findViewById<Button>(R.id.close)

        profilePic.setOnClickListener {
            showCustomDialog(this)
        }

       submit.setOnClickListener {

           val name = name.text.toString()
           val email = email.text.toString()
           val phone = phone.text.toString()
           val gender = gender.selectedItem.toString()
           val address = address.text.toString()
           val password = password.text.toString()

           if (valid(name,email,phone,gender,address,password))
           {
               callAddAdmin(name,email,phone,gender,address,password,dialog)
           }

       }

      close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.setOnShowListener {
            dialog.window?.setLayout(
                (Resources.getSystem().displayMetrics.widthPixels * 0.95).toInt(), // 95% of screen width
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
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
        val progressDialog = CustomProgressDialog(this@AddAdminActivity)
        progressDialog.show()
        try {
            storage.getReference("Yks/ProfilePic/admins")
                .child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        profileUrl = downloadUri.toString()
                        Glide.with(this).load(profileUrl).into(profilePic)
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

    private fun callAddAdmin(name: String, email: String, phone: String, gender: String, address: String, password: String, dialog: Dialog){
        val progress = CustomProgressDialog(this)
        progress.show()

        val societyId = intent.getIntExtra("id", -1)

        val model = CreateUserBySuperAdminRequestModel(address,email,true,name,gender,password,phone,profileUrl.toString())

        try {
            RetrofitInstance.getHeaderInstance().addAdmin(societyId,model).enqueue(object :
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
                                Constant.success(this@AddAdminActivity, data.MSG)
                                dialog.dismiss()
                                callGetAllUser()
                            } else {
                                Constant.error(this@AddAdminActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@AddAdminActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@AddAdminActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<UserRegisterResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@AddAdminActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@AddAdminActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }


    private fun valid(name: String, email: String, phone: String, gender: String, address: String, password: String): Boolean {
        if (name.isEmpty()) {
            Constant.error(this@AddAdminActivity, "Name is required")
            return false
        }
        if (email.isEmpty()) {
            Constant.error(this@AddAdminActivity, "Email is required")
            return false
        }
        if (phone.isEmpty()) {
            Constant.error(this@AddAdminActivity, "Phone is required")
            return false
        }
        if (gender.isEmpty()) {
            Constant.error(this@AddAdminActivity, "Gender is required")
            return false
        }
        if (address.isEmpty()) {
            Constant.error(this@AddAdminActivity, "Address is required")
            return false
        }
        if (password.isEmpty()) {
            Constant.error(this@AddAdminActivity, "Password is required")
            return false
        }
        return true
    }


    private fun callGetAllUser() {
        val progress = CustomProgressDialog(this)
        progress.show()

        val societyId = intent.getIntExtra("id", -1)

        try {
            RetrofitInstance.getHeaderInstance().getAllUser(societyId).enqueue(object :
                Callback<GetUserBySocietyResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<GetUserBySocietyResponseModel>,
                    response: Response<GetUserBySocietyResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                adapter = UserAdapter(this@AddAdminActivity, data.CONTENT)
                                binding.recyclerView.adapter = adapter
                            } else {
                                Constant.error(this@AddAdminActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@AddAdminActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@AddAdminActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<GetUserBySocietyResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@AddAdminActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@AddAdminActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }


}