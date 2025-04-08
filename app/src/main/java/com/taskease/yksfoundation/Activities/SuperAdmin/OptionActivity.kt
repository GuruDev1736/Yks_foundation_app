package com.taskease.yksfoundation.Activities.SuperAdmin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Activities.SuperAdmin.SuperAdminHomeActivity
import com.taskease.yksfoundation.Adapter.MenuAdapter
import com.taskease.yksfoundation.Model.MenuItem
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.ActivityOptionBinding

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
                "Download Excel Sheet" -> Toast.makeText(this, "Add Post clicked", Toast.LENGTH_SHORT).show()
            }
        }

        binding.actionBar.toolbarTitle.text = "Options"

        binding.recyclerView.layoutManager = GridLayoutManager(this@OptionActivity,2)
        binding.recyclerView.adapter = adapter

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
