package com.taskease.yksfoundation.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Activities.Auth.RegisterActivity
import com.taskease.yksfoundation.Activities.SuperAdmin.OptionActivity
import com.taskease.yksfoundation.ViewModel.RegisterViewModel
import com.taskease.yksfoundation.Model.ResponseModel.Society
import com.taskease.yksfoundation.databinding.SocietyLayoutBinding
import java.util.Locale

class SocietyAdapter(val context: Context , val list : List<Society> , val code : String) : RecyclerView.Adapter<SocietyAdapter.onViewHolder>() {

    private lateinit var viewModel : RegisterViewModel
    private var filteredList: List<Society> = list

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): onViewHolder {
        val view = SocietyLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return onViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: onViewHolder,
        position: Int
    ) {

        val data = filteredList[position]
        holder.binding.apply {
            societyName.text = data.name
            societyAddress.text = data.address
        }

        if (code == "registration")
        {
            val viewModelStoreOwner = context as? ViewModelStoreOwner
            if (viewModelStoreOwner != null) {
                viewModel = ViewModelProvider(viewModelStoreOwner).get(RegisterViewModel::class.java)
            }


            holder.itemView.setOnClickListener {
                viewModel.selectSociety(data.id)
                val viewPager = (context as RegisterActivity).getViewPager()
                viewPager.currentItem += 1
            }
        }

        if (code == "society")
        {
            holder.itemView.setOnClickListener {
                context.startActivity(Intent(context, OptionActivity::class.java)
                    .putExtra("id",data.id)
                    .putExtra("societyName",data.name)
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchText = constraint?.toString()?.lowercase(Locale.getDefault())?.trim()
                filteredList = if (searchText.isNullOrEmpty()) {
                    list
                } else {
                    list.filter {
                        it.name.lowercase(Locale.getDefault()).contains(searchText) ||
                                it.address.lowercase(Locale.getDefault()).contains(searchText)
                    }
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<Society> ?: list
                notifyDataSetChanged()
            }
        }
    }

    class onViewHolder(val binding : SocietyLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}