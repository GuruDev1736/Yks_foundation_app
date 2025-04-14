package com.taskease.yksfoundation.Activities.Admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Activities.SuperAdmin.AddAdminActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.AddUserActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.OptionActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.subMenuAdapter
import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import com.taskease.yksfoundation.Model.MenuItem
import com.taskease.yksfoundation.R
import com.taskease.yksfoundation.databinding.FragmentRequestBinding


class RequestFragment : Fragment() {

    private lateinit var binding : FragmentRequestBinding

    private val menuList = listOf(
        MenuItem("Post Approval", R.drawable.admin),
        MenuItem("User Approval", R.drawable.user),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val societyId = SharedPreferenceManager.getInt(SharedPreferenceManager.SOCIETY_ID)

        val adapter = subMenuAdapter(menuList) { menuItem ->
            when (menuItem.title) {
                "Post Approval" -> startActivity(Intent(requireContext(), PostApprovalActivity::class.java)
                    .putExtra("id",societyId))
                "User Approval" -> startActivity(Intent(requireContext(),
                    UserApproval::class.java).putExtra("id",societyId))
            }
        }

        binding.recyclerView.adapter = adapter

    }
}


class RequestAdapter(private val menuList: List<MenuItem>, private val listener: (MenuItem) -> Unit) :
    RecyclerView.Adapter<RequestAdapter.MenuViewHolder>() {

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