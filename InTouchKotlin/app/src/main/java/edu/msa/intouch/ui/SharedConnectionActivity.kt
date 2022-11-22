package edu.msa.intouch.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.msa.intouch.databinding.ActivitySharedConnectionBinding

class SharedConnectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
    }

    private fun setBinding(){
        binding = ActivitySharedConnectionBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }
}