package com.taskease.yksfoundation.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Model.MenuItem
import com.taskease.yksfoundation.R

class MenuAdapter(private val menuList: List<MenuItem>, private val listener: (MenuItem) -> Unit) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

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
