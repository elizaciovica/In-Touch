package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    }

    private fun setUserDetails() {
        val usernameTextView: TextView = findViewById(R.id.username)
        val selectedUser =
            Json.decodeFromString<Client>(intent.getSerializableExtra("selectedUser") as String)
        usernameTextView.text = selectedUser!!.firstName + " " + selectedUser!!.lastName
    }
}