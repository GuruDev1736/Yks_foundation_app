package com.taskease.yksfoundation.Activities.Admin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.RequestModel.UpdateProfileRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.GetUserByIdResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.UserRegisterResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityEditProfileBinding
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.actionBar.toolbarTitle.text = "Edit Profile"

        val userId = SharedPreferenceManager.getInt(SharedPreferenceManager.USER_ID)
        getUserById(userId)

        binding.btnSave.setOnClickListener {

            val name = binding.fullName.text.toString()
            val phoneNo = binding.phoneNo.text.toString()
            val email = binding.email.text.toString()
            val designation = binding.designation.text.toString()
            val address = binding.address.text.toString()
            val birthDate = binding.birthDate.text.toString()
            val anniversaryDate = binding.anniversaryDate.text.toString()
            val faceBook = binding.faceBook.text.toString()
            val instaProfile = binding.instaProfile.text.toString()
            val twitterProfile = binding.twitterProfile.text.toString()
            val linkedInProfile = binding.linkedInProfile.text.toString()
            val snapChatProfile = binding.snapChatProfile.text.toString()
            val whatsappNo = binding.whatsappNo.text.toString()
            val gender = binding.spinnerGender.selectedItem.toString()

            if (valid(name,phoneNo,email,designation,address,birthDate,anniversaryDate,faceBook,instaProfile,twitterProfile,linkedInProfile,snapChatProfile,whatsappNo))
            {
                saveDetailsApi(userId , name,phoneNo,email,designation,address,birthDate,anniversaryDate,faceBook,instaProfile,twitterProfile,linkedInProfile,snapChatProfile,whatsappNo,gender)
            }

        }

        binding.birthDate.setOnClickListener {
            Constant.showDatePicker(this@EditProfileActivity,binding.birthDate)
        }

        binding.anniversaryDate.setOnClickListener {
            Constant.showDatePicker(this@EditProfileActivity,binding.anniversaryDate)
        }
    }

    private fun saveDetailsApi(userId: Int, name: String, phoneNo: String, email: String, designation: String, address: String, birthDate: String, anniversaryDate: String, faceBook: String, instaProfile: String, twitterProfile: String, linkedInProfile: String, snapChatProfile: String, whatsappNo: String, gender: String) {
        val progress = CustomProgressDialog(this@EditProfileActivity)
        progress.show()

        val model = UpdateProfileRequestModel(address,anniversaryDate,birthDate,designation,email,faceBook,name,gender,instaProfile,linkedInProfile,phoneNo,snapChatProfile,twitterProfile,whatsappNo)

        try {
            RetrofitInstance.getHeaderInstance().updateProfile(userId,model).enqueue(object :
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
                                Constant.success(this@EditProfileActivity, data.MSG)
                                finish()
                            } else {
                                Constant.error(this@EditProfileActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@EditProfileActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@EditProfileActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<UserRegisterResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@EditProfileActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@EditProfileActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    fun valid(name: String,phoneNo: String,email: String,designation: String,address: String,birthDate: String,anniversaryDate: String,faceBook: String,instaProfile: String,twitterProfile: String,linkedInProfile: String,snapChatProfile: String,whatsappNo: String) : Boolean{
        if (name.isEmpty()){
            Constant.error(this@EditProfileActivity,"Please enter name")
            binding.fullName.requestFocus()
            return false
        }
        if (phoneNo.isEmpty()){
            Constant.error(this@EditProfileActivity,"Please enter phone number")
            binding.phoneNo.requestFocus()
            return false
        }
        if (email.isEmpty()){
            Constant.error(this@EditProfileActivity,"Please enter email")
            binding.email.requestFocus()
            return false
        }
        if (address.isEmpty()){
            Constant.error(this@EditProfileActivity,"Please enter address")
            binding.address.requestFocus()
            return false
        }

        return true
    }

    private fun getUserById(userId: Int) {
        val progress = CustomProgressDialog(this@EditProfileActivity)
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
                                setData(data)
                            } else {
                                Constant.error(this@EditProfileActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@EditProfileActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@EditProfileActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<GetUserByIdResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@EditProfileActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@EditProfileActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun setData(data: GetUserByIdResponseModel) {
        binding.apply {
            fullName.setText(data.CONTENT.fullName)
            email.setText(data.CONTENT.email)
            phoneNo.setText(data.CONTENT.phoneNo)
            designation.setText(data.CONTENT.designation)
            address.setText(data.CONTENT.address)
            birthDate.setText(data.CONTENT.birthdate)
            anniversaryDate.setText(data.CONTENT.anniversary)
            faceBook.setText(data.CONTENT.facebook)
            instaProfile.setText(data.CONTENT.instagram)
            twitterProfile.setText(data.CONTENT.twitter)
            linkedInProfile.setText(data.CONTENT.linkedin)
            snapChatProfile.setText(data.CONTENT.snapChat)
            whatsappNo.setText(data.CONTENT.whatsappNo)
        }
    }
}