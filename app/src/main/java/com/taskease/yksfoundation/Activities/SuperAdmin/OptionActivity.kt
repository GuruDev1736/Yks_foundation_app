package com.taskease.yksfoundation.Activities.SuperAdmin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Activities.CreatePostActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.SuperAdminHomeActivity
import com.taskease.yksfoundation.Adapter.MenuAdapter
import com.taskease.yksfoundation.Constant.Constant
import com.taskease.yksfoundation.Constant.CustomProgressDialog
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.MenuItem
import com.taskease.yksfoundation.Model.RequestModel.CreatePostRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.CreatePostResponseModel
import com.taskease.yksfoundation.Model.UniversalModel
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.Retrofit.RetrofitInstance
import com.taskease.yksfoundation.databinding.ActivityOptionBinding
import retrofit2.Callback
import retrofit2.Response

class OptionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOptionBinding
    private val menuList = listOf(
        MenuItem("Add Admin", R.drawable.admin),
        MenuItem("Add User", R.drawable.user),
        MenuItem("See Chats",R.drawable.chat),
        MenuItem("Download Excel Sheet",R.drawable.excel)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.toolbarBack.setOnClickListener {
            finish()
        }

        val societyId = intent.getIntExtra("id", -1)

        val adapter = subMenuAdapter(menuList) { menuItem ->
            when (menuItem.title) {
                "Add Admin" -> startActivity(Intent(this@OptionActivity, AddAdminActivity::class.java)
                    .putExtra("id",societyId))
                "Add User" -> startActivity(Intent(this@OptionActivity,
                    AddUserActivity::class.java).putExtra("id",societyId))
                "See Chats" -> Toast.makeText(this, "Add Post clicked", Toast.LENGTH_SHORT).show()
                "Download Excel Sheet" -> exportUserData(societyId)
            }
        }

        binding.actionBar.toolbarTitle.text = "Options"

        binding.recyclerView.layoutManager = GridLayoutManager(this@OptionActivity,2)
        binding.recyclerView.adapter = adapter

    }

    private fun exportUserData(societyId: Int) {
        val progress = CustomProgressDialog(this)
        progress.show()

        val societyName = intent.getStringExtra("societyName")

        try {
            RetrofitInstance.getHeaderInstance().exportUserData(societyId).enqueue(object :
                Callback<UniversalModel> {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onResponse(
                    call: retrofit2.Call<UniversalModel>,
                    response: Response<UniversalModel>
                ) {
                    progress.dismiss()
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            if (data.STS == "200") {
                                Constant.saveBase64ExcelToDownloads(this@OptionActivity,data.CONTENT,societyName.toString())
                                Constant.success(this@OptionActivity, data.MSG)
                            } else {
                                Constant.error(this@OptionActivity, data.MSG)
                            }
                        } else {
                            Constant.error(this@OptionActivity, "No data received")
                        }
                    } else {
                        Constant.error(this@OptionActivity, "Response unsuccessful")
                        Log.e("SelectSocietyFragment", "Error response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<UniversalModel>, t: Throwable) {
                    progress.dismiss()
                    Constant.error(this@OptionActivity, "Something went wrong: ${t.message}")
                    Log.e("SelectSocietyFragment", "API call failed", t)
                }
            })
        } catch (e: Exception) {
            progress.dismiss()
            Constant.error(this@OptionActivity, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

}


class subMenuAdapter(private val menuList: List<MenuItem>, private val listener: (MenuItem) -> Unit) :
    RecyclerView.Adapter<subMenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menuIcon: ImageView = view.findViewById(R.id.menuIcon)
        val menuTitle: TextView = view.findViewById(R.id.menuTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_card, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]
        holder.menuTitle.text = menuItem.title
        holder.menuIcon.setImageResource(menuItem.icon)
        holder.itemView.setOnClickListener { listener(menuItem) }
    }

    override fun getItemCount(): Int = menuList.size
}
