package edu.msa.intouch.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivitySharedConnectionBinding

class SharedConnectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setUserDetails()
        initializeButtons()
    }

    private fun setBinding(){
        binding = ActivitySharedConnectionBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }

    private fun initializeButtons() {
        binding.chatBtn.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setUserDetails(){
        val usernameTextView : TextView = findViewById(R.id.username)
        val clientList = ConnectionListActivity().clientList
        val selectedUser = clientList.find{it.firebaseId == (intent.extras?.getString("userId"))}
        usernameTextView.text = selectedUser?.firstName + " " + selectedUser?.lastName
    }
}