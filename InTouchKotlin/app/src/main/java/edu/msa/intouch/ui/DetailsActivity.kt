package edu.msa.intouch.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.msa.intouch.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initializeButtons()
    }

    private fun initializeButtons() {
        binding.setProfileButton.setOnClickListener {
            createProfileAction()
            startActivity(Intent(this, ConnectionActivity::class.java))
            finish()
        }
    }

    private fun createProfileAction() {
        //todo request to server to create user
    }

    private fun setBinding() {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}