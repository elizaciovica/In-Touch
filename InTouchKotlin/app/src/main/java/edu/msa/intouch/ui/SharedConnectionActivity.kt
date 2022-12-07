package edu.msa.intouch.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivitySharedConnectionBinding


class SharedConnectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeButtons()
        setUserDetails()
    }

    private fun setBinding(){
        binding = ActivitySharedConnectionBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }

    private fun initializeButtons() {
        binding.chatBtn.setOnClickListener {
            val chatIntent = Intent(this, ChatActivity::class.java)
            chatIntent.putExtra("userId", intent.extras?.getString("userId"))
            startActivity(chatIntent)
        }

    }

    private fun setUserDetails(){
        val usernameTextView : TextView = findViewById(R.id.username)
        val clientList = ConnectionListActivity().clientList
        val selectedUser = clientList.find{it.firebaseId == (intent.extras?.getString("userId"))}
        usernameTextView.text = selectedUser?.firstName + " " + selectedUser?.lastName
    }
}