package edu.msa.intouch.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.adapter.RequestAdapter
import edu.msa.intouch.databinding.ActivityRequestsBinding
import edu.msa.intouch.model.ConnectionStatus
import edu.msa.intouch.service.BackendApiService

class RequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestsBinding
    private var storageRef = Firebase.storage

    private val backendApiService = BackendApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        getMenu()
        getProfilePicture()
        getRequestConnections()
        setNavigation(this)
    }

    private fun getMenu() {
        val button: ImageButton = binding.homeIcon

        val showPopUp = PopupMenu(
            this,
            button
        )

        showPopUp.menu.add(Menu.NONE, 0, 0, "See profile details")
        showPopUp.menu.add(Menu.NONE, 1, 1, "Log Out")

        showPopUp.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 1) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            if (id == 0) {
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            false
        }

        button.setOnClickListener {
            showPopUp.show()
        }
    }

    private fun setBinding() {
        binding = ActivityRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getRequestConnections() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backendApiService.getAllConnectionRequestsByStatus(this, ConnectionStatus.PENDING.status)
        backendApiService.observeRequestsLiveData().observe(
            this
        ) { connectionsList ->
            if (connectionsList.isNotEmpty()) {

                binding.progressBar.isVisible = false
                binding.viewForNoConnections.isVisible = false
                binding.recyclerView.isVisible = true

                connectionsList.forEach {
                    val adapter = RequestAdapter(connectionsList, this)
                    recyclerView.adapter = adapter
                }
            } else {

                binding.progressBar.isVisible = false
                binding.viewForNoConnections.isVisible = true
                binding.recyclerView.isVisible = false
            }
        }
    }

    private fun getProfilePicture() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var islandRef = storageRef.reference.child("images/$userId")
        val ONE_MEGABYTE: Long = 1024 * 1024 * 10
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.homeIcon.background = BitmapDrawable(
                resources,
                Bitmap.createScaledBitmap(
                    bitmap,
                    binding.homeIcon.width,
                    binding.homeIcon.height,
                    false
                )
            )
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    private fun setNavigation(activity: Activity) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            if(item.itemId == R.id.page_1) {
                activity.startActivity(Intent(activity, HomeActivity::class.java))
                true
            }
            if(item.itemId == R.id.page_2) {
                activity.startActivity(Intent(activity, ConnectionActivity::class.java))
                true
            }
            false
        }
    }
}