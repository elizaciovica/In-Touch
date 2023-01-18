package edu.msa.intouch.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivitySharedConnectionBinding
import edu.msa.intouch.model.Client
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class SharedConnectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedConnectionBinding

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