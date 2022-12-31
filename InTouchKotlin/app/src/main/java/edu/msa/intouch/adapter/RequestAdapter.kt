package edu.msa.intouch.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
            getProfilePicture(dataSet[position].senderId.firebaseId, viewHolder)
        } else {
            viewHolder.nameTextView.text = dataSet[position].receiverId.firstName
            getProfilePicture(dataSet[position].receiverId.firebaseId, viewHolder)
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

    private fun getProfilePicture(userId : String, viewHolder: RequestAdapter.ViewHolder) {
        var storageRef = Firebase.storage
        var islandRef = storageRef.reference.child("images/$userId")
        val ONE_MEGABYTE: Long = 1024 * 1024 * 10
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            viewHolder.imageView.background = BitmapDrawable(
                Bitmap.createScaledBitmap(
                    bitmap,
                    viewHolder.imageView.width,
                    viewHolder.imageView.height,
                    false
                )
            )
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}