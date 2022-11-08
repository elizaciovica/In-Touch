package edu.msa.intouch.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ItemConnectionBinding
import edu.msa.intouch.ui.model.User

class UserAdapter(var context: Context, var userList:ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding: ItemConnectionBinding = ItemConnectionBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_connection, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.useremail.text = user.email
    }

    override fun getItemCount(): Int = userList.size
}