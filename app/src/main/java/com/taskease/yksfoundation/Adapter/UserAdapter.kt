package com.taskease.yksfoundation.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.taskease.yksfoundation.Model.ResponseModel.GetUserBySociety
import com.taskease.yksfoundation.databinding.SocietyLayoutBinding
import java.util.Locale

class UserAdapter(val context: Context, val list : List<GetUserBySociety>) : RecyclerView.Adapter<UserAdapter.onViewHolder>() {

    private var filteredList: List<GetUserBySociety> = list

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
            societyName.text = data.fullName
            societyAddress.text = data.address
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
                        it.fullName.lowercase(Locale.getDefault()).contains(searchText) ||
                                it.address.lowercase(Locale.getDefault()).contains(searchText)
                    }
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<GetUserBySociety> ?: list
                notifyDataSetChanged()
            }
        }
    }

    class onViewHolder(val binding : SocietyLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}