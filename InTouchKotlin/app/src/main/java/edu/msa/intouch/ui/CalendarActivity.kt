package edu.msa.intouch.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityCalendarBinding
import edu.msa.intouch.databinding.ActivitySharedConnectionBinding

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding
    private var storageRef = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setNavigation(this)
        getProfilePicture()
    }

    private fun setBinding() {
        binding = ActivityCalendarBinding.inflate(layoutInflater);
        setContentView(binding.root);
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

    private fun initializeButtons() {
        binding.addEventBtn.setOnClickListener {
            // open popup with add event
//            val chatIntent = Intent(this, ChatActivity::class.java)
//            val selectedUser = intent.getSerializableExtra("selectedUser") as String
//            chatIntent.putExtra("selectedUser", selectedUser)
//            startActivity(chatIntent)
        }

        binding.selectDateBtn.setOnClickListener {
            // open popup with datepicker
//            val chatIntent = Intent(this, ChatActivity::class.java)
//            val selectedUser = intent.getSerializableExtra("selectedUser") as String
//            chatIntent.putExtra("selectedUser", selectedUser)
//            startActivity(chatIntent)
        }
    }
}