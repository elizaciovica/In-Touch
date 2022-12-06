package edu.msa.intouch.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setBiding()
        setUserDetails()
    }

    private fun setBiding() {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setUserDetails(){
        val usernameTextView : TextView = findViewById(R.id.username)
        val clientList = ConnectionListActivity().clientList
        val selectedUser = clientList.find{it.firebaseId == (intent.extras?.getString("userId"))}
        usernameTextView.text = selectedUser?.firstName + " " + selectedUser?.lastName
    }
}