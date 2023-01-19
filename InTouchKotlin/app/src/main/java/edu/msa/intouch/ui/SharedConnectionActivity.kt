package edu.msa.intouch.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivitySharedConnectionBinding
import edu.msa.intouch.model.Client
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class SharedConnectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedConnectionBinding
    private var storageRef = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeButtons()
        setUserDetails()
        setNavigation(this)
    }

    private fun setBinding() {
        binding = ActivitySharedConnectionBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }

    private fun initializeButtons() {
        binding.chatBtn.setOnClickListener {
            val chatIntent = Intent(this, ChatActivity::class.java)
            val selectedUser = intent.getSerializableExtra("selectedUser") as String
            chatIntent.putExtra("selectedUser", selectedUser)
            startActivity(chatIntent)
        }

        binding.notesBtn.setOnClickListener {
            val notesIntent = Intent(this, ViewNotesActivity::class.java)
            val selectedUser = intent.getSerializableExtra("selectedUser") as String
            notesIntent.putExtra("selectedUser", selectedUser)
            startActivity(notesIntent)
        }

        binding.calendarBtn.setOnClickListener {
            val calendarIntent = Intent(this, CalendarActivity::class.java)
            val selectedUser = intent.getSerializableExtra("selectedUser") as String
            calendarIntent.putExtra("selectedUser", selectedUser)
            startActivity(calendarIntent)
        }

    }

    private fun setUserDetails() {
        val usernameTextView: TextView = findViewById(R.id.username)
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        usernameTextView.text = selectedUser!!.firstName + " " + selectedUser!!.lastName

        var islandRef = storageRef.reference.child("images/${selectedUser.firebaseId}")
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
            if (item.itemId == R.id.page_1) {
                activity.startActivity(Intent(activity, HomeActivity::class.java))
                true
            }
            if (item.itemId == R.id.page_2) {
                activity.startActivity(Intent(activity, ConnectionActivity::class.java))
                true
            }
            false
        }
    }
}