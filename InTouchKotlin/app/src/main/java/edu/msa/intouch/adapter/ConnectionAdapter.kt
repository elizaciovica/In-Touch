package edu.msa.intouch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import edu.msa.intouch.R

class ConnectionAdapter(private val dataSet: List<edu.msa.intouch.model.Connection>) :
    RecyclerView.Adapter<ConnectionAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.name)
        //todo add profile image in database so we can use it here
        val imageView = itemView.findViewById<ImageView>(R.id.profile)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_connection, viewGroup, false)

        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        if(currentUser.equals(dataSet[position].receiverId.firebaseId)) {
            viewHolder.nameTextView.text = dataSet[position].senderId.firstName
        } else {
            viewHolder.nameTextView.text = dataSet[position].receiverId.firstName
        }

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

}