package edu.msa.intouch.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.msa.intouch.R
import edu.msa.intouch.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}