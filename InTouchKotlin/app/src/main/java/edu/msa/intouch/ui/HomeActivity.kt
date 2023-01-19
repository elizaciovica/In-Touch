package edu.msa.intouch.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.adapter.ConnectionAdapter
import edu.msa.intouch.databinding.ActivityHomeBinding
import edu.msa.intouch.model.Client
import edu.msa.intouch.model.ConnectionStatus
import edu.msa.intouch.service.BackendApiService
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HomeActivity : AppCompatActivity() {

    private var storageRef = Firebase.storage
    private lateinit var binding: ActivityHomeBinding
    private val backendApiService = BackendApiService()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        getProfilePicture()
        getConnections()
        getMenu()
        setNavigation(this)
        initializeButtons()
        getClientDetails()
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

    private fun initializeButtons() {
        binding.connection.setOnClickListener {
            startActivity(Intent(this, ConnectionActivity::class.java))
        }

        binding.requestId.setOnClickListener {
            startActivity(Intent(this, RequestsActivity::class.java))
        }

    }

    private fun setBinding() {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getConnections() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backendApiService.getAllConnectionsByStatus(this, ConnectionStatus.ACCEPTED.status)
        backendApiService.observeConnectionsLiveData().observe(
            this
        ) { connectionsList ->
            if (connectionsList.isNotEmpty()) {

                binding.progressBar.isVisible = false
                binding.viewForNoConnections.isVisible = false
                binding.recyclerView.isVisible = true

                val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                val adapter = ConnectionAdapter(connectionsList)
                recyclerView.adapter = adapter

                adapter.setOnItemClickListener(object : ConnectionAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val sharedConnection =
                            Intent(this@HomeActivity, SharedConnectionActivity::class.java)
                        val selectedUser: Client =
                            if (connectionsList.get(position).receiverId.firebaseId == currentUser) {
                                connectionsList.get(position).senderId
                            } else {
                                connectionsList.get(position).receiverId
                            }
                        val json = Json.encodeToString(selectedUser)
                        sharedConnection.putExtra("selectedUser", json)
                        startActivity(sharedConnection)
                    }
                })
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
            //binding.homeIcon.setImageBitmap(Bitmap.createScaledBitmap(bitmap, binding.homeIcon.width, binding.homeIcon.height, false))
            binding.homeIcon.background = BitmapDrawable(
                resources,
                Bitmap.createScaledBitmap(
                    bitmap,
                    binding.homeIcon.width,
                    binding.homeIcon.height,
                    false
                )
            )

            //todo not working for round corners
//            val copyof = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//            val canvas = Canvas(copyof)
//
//            val rect = Rect(0, 0, copyof.width, copyof.height)
//            val rectF = RectF(rect)
//            lateinit var paint : Paint
//            paint = Paint()
//            paint.isAntiAlias = true
//            canvas.drawRoundRect(rectF, 200.toFloat(), 200.toFloat(), paint)
//            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
//            canvas.drawBitmap(copyof, rect, rect, paint)

        }.addOnFailureListener {
            // Handle any errors
        }
    }

    private fun getClientDetails() {
        backendApiService.getClientById(this)
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