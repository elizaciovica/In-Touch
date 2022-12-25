package edu.msa.intouch.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import edu.msa.intouch.R
import edu.msa.intouch.service.BackendApiService

class RequestAdapter(
    private val dataSet: List<edu.msa.intouch.model.Connection>,
    private val activity: Activity
) :
    RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    private val backendApiService = BackendApiService()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val nameTextView = itemView.findViewById<TextView>(R.id.name)
        val updateButton = itemView.findViewById<Button>(R.id.acceptConnection)
        val deleteButton = itemView.findViewById<Button>(R.id.declineConnection)

        //todo add profile image in database so we can use it here
        val imageView = itemView.findViewById<ImageView>(R.id.profile)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_request, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUser.equals(dataSet[position].receiverId.firebaseId)) {
            viewHolder.nameTextView.text = dataSet[position].senderId.firstName
        } else {
            viewHolder.nameTextView.text = dataSet[position].receiverId.firstName
        }

        viewHolder.updateButton.setOnClickListener() {
            backendApiService.updateConnectionStatusFromPendingToAccepted(
                activity,
                dataSet[position].senderId.firebaseId
            )
        }

        viewHolder.deleteButton.setOnClickListener() {
            backendApiService.deleteConnectionRequest(
                activity,
                dataSet[position].senderId.firebaseId
            )
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}