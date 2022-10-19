package edu.msa.intouch.ui


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.msa.intouch.R

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val registerButton: Button = findViewById(R.id.signUpButton)
        registerButton.setOnClickListener {
            val register = Intent(this@StartActivity, RegisterActivity::class.java)
            startActivity(register)
        }
    }
}