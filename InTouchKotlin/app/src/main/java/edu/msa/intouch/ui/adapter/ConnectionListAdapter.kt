package edu.msa.intouch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.msa.intouch.R
import edu.msa.intouch.model.Client

class ConnectionListAdapter(private val connectionList: ArrayList<Client>) : RecyclerView.Adapter<ConnectionListAdapter.ConnectionListHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onBindViewHolder(holder: ConnectionListHolder, position: Int) {
        val itemsViewModel = connectionList[position]

        holder.textView.text = itemsViewModel.firstName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionListHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_connection, parent, false)

        return ConnectionListHolder(view, mListener)
    }

    override fun getItemCount(): Int {
        return connectionList.size
    }

    inner class ConnectionListHolder(ItemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.name)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}