package com.taskease.yksfoundation.Activities.SuperAdmin

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.taskease.yksfoundation.Adapter.SocietyAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Model.RequestModel.AddSocietyRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.AddSocietyResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllSocietyResponseModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityAddSocietyBinding
import retrofit2.Callback
import retrofit2.Response

class AddSocietyActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddSocietyBinding
    private lateinit var adapter : SocietyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSocietyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        binding.actionBar.toolbarTitle.text = "Society"


        binding.recyclerView.layoutManager = LinearLayoutManager(this@AddSocietyActivity)

        CallSocietyApi()


        binding.addSociety.setOnClickListener {
            showDialog()
        }
    }

    private fun valid(societyName: String, societyAddress: String, societyOwner: String, societyPhone: String): Boolean {
        if (societyName.isEmpty()) {
            Constant.error(this@AddSocietyActivity, "Society Name is required")
            return false
        }
        if (societyAddress.isEmpty()) {
            Constant.error(this@AddSocietyActivity, "Society Address is required")
            return false
        }
        if (societyOwner.isEmpty()) {
            Constant.error(this@AddSocietyActivity, "Society Owner is required")
            return false
        }
        if (societyPhone.isEmpty()) {
            Constant.error(this@AddSocietyActivity, "Society Phone is required")
            return false
        }
        return true
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_society_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()


        val name = dialogView.findViewById<TextInputEditText>(R.id.societyName)
        val address = dialogView.findViewById<TextInputEditText>(R.id.societyAddress)
        val owner = dialogView.findViewById<TextInputEditText>(R.id.societyOwner)
        val phone = dialogView.findViewById<TextInputEditText>(R.id.societyPhone)

        dialogView.findViewById<MaterialButton>(R.id.submit)?.setOnClickListener {
            val societyName = name.text.toString()
            val societyAddress = address.text.toString()
            val societyOwner = owner.text.toString()
            val societyPhone = phone.text.toString()

            if(valid(societyName,societyAddress,societyOwner,societyPhone))
            {
                callAddSociety(societyName,societyAddress,societyOwner,societyPhone,dialog)
            }
        }

        dialogView.findViewById<Button>(R.id.close).setOnClickListener {
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

    private fun callAddSociety(societyName: String, societyAddress: String, societyOwner: String, societyPhone: String , dialog: Dialog)
    {
        val progress = CustomProgressDialog(this)
        progress.show()

        try {

            val model = AddSocietyRequestModel(societyAddress,societyName,societyOwner,societyPhone)
            RetrofitInstance.getHeaderInstance().addSociety(model).enqueue(object :
                Callback<AddSocietyResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<AddSocietyResponseModel>,
                    response: Response<AddSocietyResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        dialog.dismiss()
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                Constant.success(this@AddSocietyActivity, data.MSG)
                                CallSocietyApi()
                            } else {
                                Constant.error(this@AddSocietyActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@AddSocietyActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@AddSocietyActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<AddSocietyResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@AddSocietyActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@AddSocietyActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun CallSocietyApi()
    {
        val progress = CustomProgressDialog(this)
        progress.show()

        try {
            RetrofitInstance.getInstance().getAllSociety().enqueue(object :
                Callback<GetAllSocietyResponseModel> {
                override fun onResponse(
                    call: retrofit2.Call<GetAllSocietyResponseModel>,
                    response: Response<GetAllSocietyResponseModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                adapter = SocietyAdapter(this@AddSocietyActivity, data.CONTENT , "society")
                                binding.recyclerView.adapter = adapter
                            } else {
                                Constant.error(this@AddSocietyActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@AddSocietyActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@AddSocietyActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<GetAllSocietyResponseModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@AddSocietyActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@AddSocietyActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

}