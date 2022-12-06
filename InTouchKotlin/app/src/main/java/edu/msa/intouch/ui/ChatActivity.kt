package edu.msa.intouch.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setBiding()
    }

    private fun setBiding() {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}